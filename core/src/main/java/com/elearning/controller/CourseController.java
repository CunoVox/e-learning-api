package com.elearning.controller;

import com.elearning.connector.Connector;
import com.elearning.entities.Category;
import com.elearning.entities.Course;
import com.elearning.entities.Price;
import com.elearning.handler.ServiceException;
import com.elearning.models.dtos.CategoryDTO;
import com.elearning.models.dtos.CourseDTO;
import com.elearning.models.dtos.FileRelationshipDTO;
import com.elearning.models.searchs.ParameterSearchCourse;
import com.elearning.models.wrapper.ListWrapper;
import com.elearning.reprositories.ICategoryRepository;
import com.elearning.reprositories.ICourseRepository;
import com.elearning.reprositories.IPriceRepository;
import com.elearning.reprositories.ISequenceValueItemRepository;
import com.elearning.utils.Extensions;
import com.elearning.utils.StringUtils;
import com.elearning.utils.enumAttribute.*;
import lombok.experimental.ExtensionMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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
        dto.setId(null);
        Course course = buildEntity(dto);
        Course courseSaved = saveCourse(course);

        //Price
        priceController.updatePriceSell(courseSaved.getId(), dto.getPriceSell());
        if (dto.getPricePromotion() != null && dto.getPricePromotion().getPrice() != null)
            priceController.createPrice(dto.getPricePromotion());
        return getCourseById(course.getId());
    }

    public void acceptCourse(String courseId) {
        CourseDTO courseDTO = getCourseById(courseId);
        if (courseDTO != null) {
            if (courseDTO.getCourseType().equals(EnumCourseType.DRAFT)) {
                courseRepository.updateCourseType(courseId, EnumCourseType.OFFICIAL.name(), getUserIdFromContext());
            }
            if (!courseDTO.getChildren().isNullOrEmpty()) {
                for (CourseDTO children : courseDTO.getChildren()) {
                    courseRepository.updateCourseType(children.getId(), EnumCourseType.OFFICIAL.name(), getUserIdFromContext());
                }
            }
        }
    }

    public CourseDTO getCourseById(String courseId) {
        ListWrapper<CourseDTO> listWrapper = searchCourseDTOS(ParameterSearchCourse.builder().ids(Collections.singletonList(courseId)).build());
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
            for (String id : categoryIds) {
                var category = categoryController.getCategoryById(id);
                if (category != null) {
                    connector.addRelatedObjectById(
                            Course.class.getAnnotation(Document.class).collection(),
                            course.get().getId(),
                            Category.class.getAnnotation(Document.class).collection(),
                            category.getId(),
                            EnumRelatedObjectsStatus.ACTIVE.getValue(),
                            EnumConnectorType.COURSE_TO_CATEGORY.name(),
                            userId);
                    categoryDTOS.add(category);
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
            List<Course> courseLevel2 = courseRepository.findAllByParentIdIn(courseIds);
            List<String> level2Ids;
            List<Course> courseLevel3 = new ArrayList<>();
            if (!courseLevel2.isNullOrEmpty()) {
                level2Ids = courseLevel2.stream().map(Course::getId).collect(Collectors.toList());
                courseLevel3 = courseRepository.findAllByParentIdIn(level2Ids);
                courseLevel3Size = (long) courseLevel3.size();
            }
            if (parameterSearchCourse.getBuildChild() != null && parameterSearchCourse.getBuildChild()) {
                allCourse.addAll(courseLevel2);
                allCourse.addAll(courseLevel3);
            }
            List<String> allIds = allCourse.stream().map(Course::getId).collect(Collectors.toList());

            //Video
            List<FileRelationshipDTO> videos = fileRelationshipController.getFileRelationships(allIds, EnumParentFileType.COURSE_VIDEO.name());
            Map<String, String> mapVideoUrl = fileRelationshipController.getUrlOfFile(videos);
            //Ảnh
            List<FileRelationshipDTO> images = fileRelationshipController.getFileRelationships(allIds, EnumParentFileType.COURSE_IMAGE.name());
            Map<String, String> mapImageUrl = fileRelationshipController.getUrlOfFile(images);

            //toDTO
            for (Course course : courses) {
                CourseDTO courseDTO = toDTO(course);
                courseDTO.setTotalLesson(courseLevel3Size);
                //Giá tiền
                courseDTO.setPriceSell(priceController.findCoursePriceSell(courseDTO.getId()));
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
                .createdBy(inputDTO.getCreatedBy())
                .createdAt(new Date())
                .build();
        if (!inputDTO.getId().isBlankOrNull()) {
            course.setId(inputDTO.getId());
            course.setUpdatedAt(inputDTO.getUpdatedAt() != null ? inputDTO.getUpdatedAt() : null);
        }
        int flag = 0;
        course.setCourseType(EnumCourseType.DRAFT);
        if (!inputDTO.getParentId().isBlankOrNull()) {
            Optional<Course> parent = courseRepository.findById(inputDTO.getParentId());
            if (parent.isEmpty()) {
                throw new ServiceException("Không tìm thấy danh mục cha trong hệ thống!");
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
                .totalLesson(entity.getTotalLesson())
                .subscriptions(entity.getSubscriptions())
                .children(new ArrayList<>())
                .type(entity.getContentType())
                .courseType(entity.getCourseType())
                .createdBy(entity.getCreatedBy())
                .createAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt() : null)
                .isDeleted(entity.getIsDeleted() != null ? entity.getIsDeleted() : false)
                .build();
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
