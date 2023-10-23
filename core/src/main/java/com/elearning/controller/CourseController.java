package com.elearning.controller;

import com.elearning.connector.Connector;
import com.elearning.entities.Category;
import com.elearning.entities.Course;
import com.elearning.handler.ServiceException;
import com.elearning.models.dtos.CategoryDTO;
import com.elearning.models.dtos.CourseDTO;
import com.elearning.models.dtos.FileRelationshipDTO;
import com.elearning.models.searchs.ParameterSearchCourse;
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

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Service
//@AllArgsConstructor
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
    Connector connector;

    @Transactional
    public CourseDTO createCourse(CourseDTO dto) {
        String userId = this.getUserIdFromContext();
        if (userId != null) {
            dto.setCreatedBy(userId);
        }
        dto.setId(null);
        Course course = buildEntity(dto);
//        return this.toDTOs(Collections.singletonList(this.saveCategory(course))).get(0);
        Course courseSaved = this.saveCourse(course);

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
        Optional<Course> course = courseRepository.findById(courseId);
        if (course.isEmpty()) {
            throw new ServiceException("Không tìm thấy khóa học");
        }
        Course cD = course.get();
        ParameterSearchCourse searchCategory = ParameterSearchCourse
                .builder().level(cD.getLevel()).build();
        return buildCourseTree(Collections.singletonList(toDTO(cD)), searchCategory).get(0);
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

    public List<CourseDTO> searchCourseDTOS(ParameterSearchCourse parameterSearchCourse) {
        if (parameterSearchCourse.getLevel() == null && !parameterSearchCourse.getBuildType().isBlankOrNull()
                && parameterSearchCourse.getBuildType().equals(EnumCategoryBuildType.TREE.name())) {
            parameterSearchCourse.setLevel(1);
        }
        List<Course> coursesEntities = courseRepository.searchCourse(parameterSearchCourse);
        List<CourseDTO> courseDTOS = toDTOs(coursesEntities);
        if (courseDTOS.isNullOrEmpty()) {
            return new ArrayList<>();
        }
        if (parameterSearchCourse.getBuildType() == null ||
                parameterSearchCourse.getBuildType().equals(EnumCategoryBuildType.TREE.name())) {
            return buildCourseTree(courseDTOS, parameterSearchCourse);
        }
        return courseDTOS;
    }

    private List<CourseDTO> buildCourseTree(List<CourseDTO> courseDTOS,
                                            ParameterSearchCourse parameterSearchCourse) {
        if (courseDTOS.isEmpty()) return new ArrayList<>();
        List<CourseDTO> list = new ArrayList<>();
        if (parameterSearchCourse.getLevel() == null) {
            parameterSearchCourse.setLevel(1);
        }
        try {
            ParameterSearchCourse parameterSearchCourseGt = parameterSearchCourse.clone();
            parameterSearchCourseGt.setLevel(null);
            List<CourseDTO> courseGT = toDTOs(courseRepository.searchCourse(parameterSearchCourseGt));
            Stack<CourseDTO> stack = new Stack<>();
            for (CourseDTO courseDTO : courseDTOS) {
                stack.push(courseDTO);
                while (!stack.empty()) {
                    CourseDTO courseParent = stack.pop();
                    List<CourseDTO> courseChild = courseGT.stream().filter(course -> (
                            null != course.getParentId() &&
                                    course.getParentId().equals(courseParent.getId()) &&
                                    course.getLevel() == courseParent.getLevel() + 1
                    )).collect(Collectors.toList());
                    if (!courseChild.isEmpty()) {
                        courseParent.setChildren(courseChild);
                        courseChild.forEach(stack::push);
                    }
                }
                list.add(courseDTO);
            }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return list;
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
        String videoPath = fileRelationshipController.getPathFile(entity.getId(),
                EnumParentFileType.COURSE.name(), EnumFileType.VIDEO.name());
        String imagePath = fileRelationshipController.getPathFile(entity.getId(),
                EnumParentFileType.COURSE.name(), EnumFileType.IMAGE.name());
        return CourseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .slug(entity.getSlug())
                .nameMode(entity.getNameMode())
                .videoPath(videoPath)
                .imagePath(imagePath)
                .parentId(entity.getParentId())
                .level(entity.getLevel())
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

//        if (course.getLevel() == 1) {
//            List<Course> courses = courseRepository.findAll();
//            List<Course> children = new ArrayList<>();
//            buildCourseChild(course.getId(), courses, children);
//
//            if (!children.isEmpty()) {
//                courseRepository.saveAll(children);
//            }
//        }
        return course;
    }

    private void buildCourseChild(String id, List<Course> courses, List<Course> childs) {
    }


}
