package com.elearning.controller;

import com.elearning.connector.Connector;
import com.elearning.entities.Attribute;
import com.elearning.entities.Category;
import com.elearning.entities.Course;
import com.elearning.handler.ServiceException;
import com.elearning.models.dtos.*;
import com.elearning.models.searchs.ParameterSearchCourse;
import com.elearning.models.wrapper.ListWrapper;
import com.elearning.reprositories.ICategoryRepository;
import com.elearning.reprositories.ICourseRepository;
import com.elearning.reprositories.ISequenceValueItemRepository;
import com.elearning.utils.Extensions;
import com.elearning.utils.StringUtils;
import com.elearning.utils.enumAttribute.*;
import lombok.experimental.ExtensionMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@ExtensionMethod(Extensions.class)
public class CourseController extends BaseController {
    @Autowired
    private ICourseRepository courseRepository;
    @Autowired
    private ISequenceValueItemRepository sequenceValueItemRepository;
    @Autowired
    private ICategoryRepository categoryRepository;
    @Autowired
    private CategoryController categoryController;
    @Autowired
    private FileRelationshipController fileRelationshipController;
    @Autowired
    private PriceController priceController;
    @Autowired
    private UserController userController;
    @Autowired
    private RatingController ratingController;
    //    @Autowired
//    private IPriceRepository priceRepository;
    @Autowired
    Connector connector;

    @Transactional
    public CourseDTO createCourse(CourseDTO dto) {
        String userId = this.getUserIdFromContext();
        if (userId != null) {
            dto.setCreatedBy(userId);
        }
        Course course = buildEntity(dto);
        //Price
        if (!dto.getId().isBlankOrNull()) {
            BigDecimal currentPrice = priceController.getPriceByParentId(dto.getId(), EnumPriceType.SELL.name());
            if (!Objects.equals(currentPrice, dto.getPriceSell()) && course.getCourseType() == EnumCourseType.OFFICIAL) {
                List<Attribute> attributes = course.getAttributes();
                if (!attributes.isNullOrEmpty()) {
                    attributes.removeIf(p -> p.getAttributeName().equals(EnumAttribute.COURSE_SELL_PRICE.name()));
                }
                if (attributes == null) attributes = new ArrayList<>();
                attributes.add(Attribute.builder()
                        .attributeName(EnumAttribute.COURSE_SELL_PRICE.name())
                        .attributeValue(dto.getPriceSell())
                        .build());
                course.setAttributes(attributes.stream()
                        .filter(p -> p.getAttributeName().equals(EnumAttribute.COURSE_SELL_PRICE.name()))
                        .findFirst()
                        .map(Collections::singletonList)
                        .orElseGet(Collections::emptyList));
                course.setCourseType(EnumCourseType.CHANGE_PRICE);
            } else {
                priceController.updatePriceSell(dto.getId(), dto.getPriceSell());
            }

        }
        Course courseSaved = saveCourse(course);
        if (!dto.getCategoryIds().isNullOrEmpty()) {
            connector.deleteConnector(
                    Course.class.getAnnotation(Document.class).collection(),
                    courseSaved.getId(),
                    Category.class.getAnnotation(Document.class).collection(),
                    EnumConnectorType.COURSE_TO_CATEGORY.name());
            addCategoryToCourse(courseSaved.getId(), dto.getCategoryIds());
        }
        if (dto.getPricePromotion() != null && dto.getPricePromotion().getPrice() != null)
            priceController.createPrice(dto.getPricePromotion());
        return getCourseById(courseSaved.getId());
    }

    public void changeCourseType(String courseId, EnumCourseType courseType, Boolean isRejected) {
        CourseDTO courseDTO = getCourseById(courseId);
        if (courseDTO != null) {
            courseRepository.updateCourseType(courseId, courseType.name(), getUserIdFromContext());
            if (courseDTO.getCourseType().equals(EnumCourseType.CHANGE_PRICE)
                    && courseType.equals(EnumCourseType.OFFICIAL)
                    && (isRejected == null || !isRejected)) {
                priceController.updatePriceSell(courseId, new BigDecimal((String) courseDTO.getAttributes().get(0).getAttributeValue()));
            }
            if (!courseDTO.getChildren().isNullOrEmpty()) {
                String userId = getUserIdFromContext();
                CompletableFuture<Void> allOf = CompletableFuture.allOf(courseDTO.getChildren().stream()
                        .map(course -> CompletableFuture.runAsync(() -> {
                            courseRepository.updateCourseType(course.getId(), courseType.name(), userId);
                            //accept course lv3
                            if (course.getLevel() == 2 && !course.getChildren().isEmpty()) {
                                CompletableFuture<Void> future3 = CompletableFuture.allOf(course.getChildren().stream()
                                        .map(courselv3 -> CompletableFuture.runAsync(() -> {
                                            courseRepository.updateCourseType(courselv3.getId(), courseType.name(), userId);
                                        })).toArray(CompletableFuture[]::new));
                                future3.join();
                            }
                        })).toArray(CompletableFuture[]::new));
                allOf.join();
            }
        }
    }

    public void updateIsDeleted(String courseId, Boolean isDeleted) {
        Optional<Course> course = courseRepository.findById(courseId);
        if (course.isEmpty()) throw new ServiceException("Khoá học không tồn tại trong hệ thống");
        courseRepository.updateIsDeleted(courseId, isDeleted, getUserIdFromContext());
    }

    public void updateIsPreview(String courseId, Boolean isPreview) {
        Optional<Course> course = courseRepository.findById(courseId);
        if (course.isEmpty()) throw new ServiceException("Khoá học không tồn tại trong hệ thống");
        courseRepository.updateIsPreview(courseId, isPreview, getUserIdFromContext());
    }

    public CourseDTO getCourseById(String courseId) {
        ListWrapper<CourseDTO> listWrapper =
                searchCourseDTOS(ParameterSearchCourse.builder()
                        .ids(Collections.singletonList(courseId))
                        .buildChild(Boolean.TRUE)
                        .searchType(List.of(EnumCourseType.OFFICIAL.name(), EnumCourseType.CHANGE_PRICE.name(), EnumCourseType.WAITING.name(), EnumCourseType.DRAFT.name()))
                        .build()
                );
        if (!listWrapper.getData().isNullOrEmpty()) {
            return listWrapper.getData().get(0);
        }
        return new CourseDTO();
    }

    public List<CategoryDTO> addCategoryToCourse(String courseId, List<String> categoryIds) {
        String userId = this.getUserIdFromContext();

        Optional<Course> course = courseRepository.findById(courseId);
        if (course.isEmpty()) {
            throw new ServiceException("Không tìm thấy khóa học");
        } else {
            if (course.get().getLevel() > 1) {
                throw new ServiceException("không thể thêm danh mục vào thành phần của khóa học");
            }
        }
        List<CategoryDTO> categoryDTOS = new ArrayList<>();

        if (categoryIds != null) {
            List<Category> categories = categoryRepository.findAllByIdIn(categoryIds);
            for (Category category : categories) {
//                var category = categoryController.getCategoryById(id);
                if (category != null) {
                    connector.addRelatedObjectById(
                            Course.class.getAnnotation(Document.class).collection(),
                            course.get().getId(),
                            Category.class.getAnnotation(Document.class).collection(),
                            category.getId(),
                            EnumRelatedObjectsStatus.ACTIVE.getValue(),
                            EnumConnectorType.COURSE_TO_CATEGORY.name(),
                            userId);
                    categoryDTOS.add(categoryController.toDTO(category));
                }
            }
        }
        return categoryDTOS;
    }

    public ListWrapper<CourseDTO> searchCourseDTOS(ParameterSearchCourse parameterSearchCourse) {
        ListWrapper<Course> wrapper = courseRepository.searchCourse(parameterSearchCourse);
        List<CourseDTO> courseDTOS = buildCourseDTO(wrapper.getData(), parameterSearchCourse);
        return ListWrapper.<CourseDTO>builder()
                .currentPage(wrapper.getCurrentPage())
                .totalPage(wrapper.getTotalPage())
                .total(wrapper.getTotal())
                .maxResult(wrapper.getMaxResult())
                .data(courseDTOS)
                .build();
    }

    private List<CourseDTO> buildCourseDTO(List<Course> courses, ParameterSearchCourse parameterSearchCourse) {
        courses = new ArrayList<>(courses);
        List<CourseDTO> courseDTOS = new ArrayList<>();
        if (!courses.isNullOrEmpty()) {
            List<Course> allCourse = new ArrayList<>();
            allCourse.addAll(courses);
            //build khoá học con level 1 + 2 +3
            Long courseLevel3Size = 0L;
            List<String> courseIds = courses.stream().map(Course::getId).collect(Collectors.toList());
            List<Course> courseLevel2 = courseRepository.findAllByParentIdInAndIsDeletedNotIn(courseIds, Collections.singletonList(true));
            List<String> level2Ids;
            List<Course> courseLevel3 = new ArrayList<>();
            if (!courseLevel2.isNullOrEmpty()) {
                level2Ids = courseLevel2.stream().map(Course::getId).collect(Collectors.toList());
                courseLevel3 = courseRepository.findAllByParentIdInAndIsDeletedNotIn(level2Ids, Collections.singletonList(true));
                courseLevel3Size = (long) courseLevel3.size();
            }
            if (parameterSearchCourse.getBuildChild() != null && parameterSearchCourse.getBuildChild()) {
                allCourse.addAll(courseLevel2);
                allCourse.addAll(courseLevel3);
            }
            Map<String, List<String>> mapCategoryIds = connector.getIdRelatedObjectsById(
                    Course.class.getAnnotation(Document.class).collection(),
                    courseIds,
                    Category.class.getAnnotation(Document.class).collection(),
                    EnumConnectorType.COURSE_TO_CATEGORY.name());
            List<String> allIds = allCourse.stream().map(Course::getId).collect(Collectors.toList());

            //Video
            List<FileRelationshipDTO> videos = fileRelationshipController.getFileRelationships(allIds, EnumParentFileType.COURSE_VIDEO.name());
            Map<String, String> mapVideoUrl = fileRelationshipController.getUrlOfFile(videos);
            //Ảnh
            List<FileRelationshipDTO> images = fileRelationshipController.getFileRelationships(allIds, EnumParentFileType.COURSE_IMAGE.name());
            Map<String, String> mapImageUrl = fileRelationshipController.getUrlOfFile(images);
            //attachments
            Map<String, List<FileRelationshipDTO>> mapAttachments = fileRelationshipController.mapFileRelationships(allIds, EnumParentFileType.COURSE_ATTACHMENT.name());
            //Chi tiết người tạo khoá học
            List<String> createdUserIds = courses.stream().map(Course::getCreatedBy).collect(Collectors.toList());
            Map<String, UserDTO> userDTOMap = userController.getUserByIds(createdUserIds);
            //toDTO
            for (Course course : courses) {
                CourseDTO courseDTO = toDTO(course);
                //chi tiết người tạo
                courseDTO.setCreatedUserInfo(new HashMap<>() {{
                    put(course.getCreatedBy(), userDTOMap.get(course.getCreatedBy()).getFullName());
                }});
                courseDTO.setTotalLesson(courseLevel3Size);
                //Danh mục
                courseDTO.setCategoryIds(mapCategoryIds.get(course.getId()));
                //Giá tiền
                courseDTO.setPriceSell(priceController.getPriceByParentId(courseDTO.getId(), EnumPriceType.SELL.name()));
//                courseDTO.setRatings(ratingController.courseRating(courseDTO.getId()));
                courseDTO.setCourseRatings(ratingController.calcRating(courseDTO.getId()));
                courseDTO.setVideoPath(mapVideoUrl.get(course.getId()));
                courseDTO.setImagePath(mapImageUrl.get(course.getId()));
                courseDTOS.add(courseDTO);
            }
            //build child
            if (parameterSearchCourse.getBuildChild() != null && parameterSearchCourse.getBuildChild()) {
                List<CourseDTO> allChildDTOS = new ArrayList<>();
                for (Course course : allCourse) {
                    CourseDTO courseDTO = toDTO(course);
                    courseDTO.setVideoPath(mapVideoUrl.get(course.getId()));
                    courseDTO.setImagePath(mapImageUrl.get(course.getId()));
                    allChildDTOS.add(courseDTO);
                }
                //Build TREE
                Stack<CourseDTO> stack = new Stack<>();
                for (CourseDTO courseDTO : courseDTOS) {
                    stack.push(courseDTO);
                    while (!stack.empty()) {
                        CourseDTO courseParent = stack.pop();
                        List<CourseDTO> courseChild = allChildDTOS.stream().filter(course -> (
                                null != course.getParentId() &&
                                        course.getParentId().equals(courseParent.getId()) &&
                                        course.getLevel() == courseParent.getLevel() + 1
                        )).collect(Collectors.toList());
                        if (!courseChild.isEmpty()) {
                            courseParent.setChildren(courseChild);
                            courseChild.forEach(stack::push);
                        }
                    }
                }
            }
        }
        return courseDTOS;
    }

    private Course buildEntity(CourseDTO inputDTO) {
        if (inputDTO.getName().isBlankOrNull()) {
            throw new ServiceException("Tiêu đề không được để trống!");
        }
        Course course = Course.builder()
                .name(inputDTO.getName())
                .nameMode(StringUtils.stripAccents(inputDTO.getName()))
                .contentType(inputDTO.getType())
                .description(inputDTO.getDescription())
                .requirement(inputDTO.getRequirement())
                .isPreview(inputDTO.getIsPreview())
                .createdBy(inputDTO.getCreatedBy())
                .createdAt(new Date())
                .isDeleted(inputDTO.isDeleted())
                .contentType(inputDTO.getType())
                .build();
        if (!inputDTO.getId().isBlankOrNull()) {
            Optional<Course> courseCheck = courseRepository.findById(inputDTO.getId());
            if (courseCheck.isEmpty()) {
                throw new ServiceException("Khoá học không tồn tại trong hệ thống!");
            }
            if (!courseCheck.get().getCreatedBy().isBlankOrNull()) {
                course.setCreatedBy(courseCheck.get().getCreatedBy());
            }
            course.setCourseType(courseCheck.get().getCourseType());
            course.setDescription(courseCheck.get().getDescription());
            if (!inputDTO.getDescription().isBlankOrNull()) {
                course.setDescription(inputDTO.getDescription());
            }
            course.setRequirement(courseCheck.get().getRequirement());
            if (!inputDTO.getRequirement().isBlankOrNull()) {
                course.setRequirement(inputDTO.getRequirement());
            }
            course.setSubscriptions(courseCheck.get().getSubscriptions());
            course.setId(inputDTO.getId());
            course.setUpdatedAt(inputDTO.getUpdatedAt() != null ? inputDTO.getUpdatedAt() : null);
            course.setUpdatedBy(getUserIdFromContext());
        } else {
            course.setCourseType(EnumCourseType.DRAFT);
        }
        if (!inputDTO.getParentId().isBlankOrNull()) {
            Optional<Course> parent = courseRepository.findById(inputDTO.getParentId());
            if (parent.isEmpty()) {
                throw new ServiceException("Không tìm thấy khoá học cha trong hệ thống!");
            }
            course.setParentId(inputDTO.getParentId());
            course.setLevel(parent.get().getLevel() + 1);
        } else {
            course.setLevel(1);
        }
        return course;
    }

    public CourseDTO toDTO(Course entity) {
        if (entity == null) return null;
        return CourseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .slug(entity.getSlug())
                .nameMode(entity.getNameMode())
                .parentId(entity.getParentId())
                .level(entity.getLevel())
                .subscriptions(entity.getSubscriptions())
                .children(new ArrayList<>())
                .attributes(toAttributesDTO(entity.getAttributes()))
                .description(entity.getDescription())
                .requirement(entity.getRequirement())
                .type(entity.getContentType())
                .courseType(entity.getCourseType())
                .isPreview(entity.getIsPreview())
                .createdBy(entity.getCreatedBy())
                .createAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt() : null)
                .isDeleted(entity.getIsDeleted() != null ? entity.getIsDeleted() : false)
                .build();
    }

    List<AttributeDTO> toAttributesDTO(List<Attribute> attributes) {
        if (attributes.isNullOrEmpty()) return null;
        List<AttributeDTO> attributeDTOS = new ArrayList<>();
        for (Attribute attribute : attributes) {
            attributeDTOS.add(AttributeDTO.builder()
                    .attributeName(attribute.getAttributeName())
                    .attributeValue(attribute.getAttributeValue())
                    .build());
        }
        return attributeDTOS;
    }

    public List<CourseDTO> toDTOs(List<Course> entities) {
        if (entities.isNullOrEmpty()) return null;
        List<CourseDTO> courseDTOS = new ArrayList<>();
        for (Course entity : entities) {
            courseDTOS.add(toDTO(entity));
        }
        return courseDTOS;
    }

    @Transactional(rollbackFor = {NullPointerException.class, ServiceException.class})
    public Course saveCourse(Course course) {
        if (course.getLevel() > 3 || course.getLevel() < 1) {
            throw new ServiceException("Cấp khóa học phải từ 1 đến 3");
        }
        if (null == course.getId()) {
            course.setId(sequenceValueItemRepository.getSequence(Course.class));
        } else {
            course.setUpdatedAt(new Date());
        }
        course.setSlug(StringUtils.getSlug(course.getName()) + "-" + course.getId());
        course = courseRepository.save(course);
        return course;
    }
}
