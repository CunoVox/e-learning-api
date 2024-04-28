package com.elearning.controller;

import com.elearning.connector.Connector;
import com.elearning.entities.Category;
import com.elearning.entities.Course;
import com.elearning.handler.ServiceException;
import com.elearning.models.dtos.CategoryDTO;
import com.elearning.models.dtos.CourseDTO;
import com.elearning.models.dtos.FileRelationshipDTO;
import com.elearning.models.searchs.ParameterSearchCategory;
import com.elearning.models.searchs.ParameterSearchCourse;
import com.elearning.models.wrapper.ListWrapper;
import com.elearning.reprositories.ICategoryRepository;
import com.elearning.reprositories.ISequenceValueItemRepository;
import com.elearning.utils.Extensions;
import com.elearning.utils.StringUtils;
import com.elearning.utils.enumAttribute.EnumCategoryBuildType;
import com.elearning.utils.enumAttribute.EnumConnectorType;
import com.elearning.utils.enumAttribute.EnumParentFileType;
import lombok.experimental.ExtensionMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
@ExtensionMethod(Extensions.class)
public class CategoryController extends BaseController {
    @Autowired
    private ICategoryRepository categoryRepository;

    @Autowired
    private ISequenceValueItemRepository sequenceValueItemRepository;
    @Autowired
    private FileRelationshipController fileRelationshipController;
    @Autowired
    private Connector connector;
    @Autowired
    private CourseController courseController;

    public CategoryDTO getCategoryById(String categoryId) {
        Optional<Category> categories = categoryRepository.findById(categoryId);
        if (categories.isEmpty()) {
            throw new ServiceException("Không tìm thấy danh mục");
        }
        Category category = categories.get();
        ParameterSearchCategory searchCategory = ParameterSearchCategory.builder().level(category.getLevel()).build();
        return buildCategoryTree(Collections.singletonList(toDTO(category)), searchCategory).get(0);
    }

    public List<CategoryDTO> searchCategoryDTOS(ParameterSearchCategory parameterSearchCategory) {
        if (parameterSearchCategory.getLevel() == null && parameterSearchCategory.getParentIds().isNullOrEmpty()
                && parameterSearchCategory.getCategoriesIds().isNullOrEmpty()) {
            parameterSearchCategory.setLevel(1);
        }
        List<Category> categoriesEntities = categoryRepository.searchCategories(parameterSearchCategory);
        List<CategoryDTO> categories = toDTOs(categoriesEntities, parameterSearchCategory.getCountTotalCourse());
        if (categories.isNullOrEmpty()) {
            return new ArrayList<>();
        }
        ParameterSearchCategory searchCategory = ParameterSearchCategory.builder().level(parameterSearchCategory.getLevel()).build();
        List<CategoryDTO> categoryDTOS = buildCategoryTree(categories, searchCategory);
        if (parameterSearchCategory.getBuildType() == null ||
                parameterSearchCategory.getBuildType().equals(EnumCategoryBuildType.TREE.name())) {
            return categoryDTOS;
        }
        return buildCategoryList(categoryDTOS);
    }

    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        String userId = this.getUserIdFromContext();
        if (!userId.isBlankOrNull()) {
            categoryDTO.setCreatedBy(userId);
            categoryDTO.setUpdateBy(userId);
            Category category = buildEntity(categoryDTO);
            return this.toDTOs(Collections.singletonList(this.saveCategory(category)), false).get(0);
        }
        return null;
    }

    public CategoryDTO updateCategory(CategoryDTO categoryDTO) {
        Optional<Category> validateCate = categoryRepository.findById(categoryDTO.getId());
        if (validateCate.isEmpty()) {
            throw new ServiceException("Danh mục không tồn tại");
        }
        Category category = buildEntity(categoryDTO);
        //TODO: chưa có cập nhật image
        return this.toDTOs(Collections.singletonList(this.saveCategory(category)), false).get(0);
    }

    public void updateCategoryName(String categoryId, String name) {
        Optional<Category> validateCate = categoryRepository.findById(categoryId);
        if (validateCate.isEmpty()) {
            throw new ServiceException("Danh mục không tồn tại");
        }
        if (name.isBlankOrNull()) {
            throw new ServiceException("Tên danh mục không được để trống");
        }
        Category category = validateCate.get();
        category.setName(name);
        category.setNameMode(StringUtils.stripAccents(name));
        category.setSlug(StringUtils.getSlug(name) + "-" + category.getId());
        category.setUpdatedAt(new Date());
        category.setUpdatedBy(getUserIdFromContext());
        categoryRepository.save(category);
    }

    public void deleteCategory(String categoryId) {
        List<CategoryDTO> categoryDTOS = searchCategoryDTOS(ParameterSearchCategory.builder()
                .categoriesIds(Collections.singletonList(categoryId))
                .buildType(EnumCategoryBuildType.LIST.name()).build());

        if (categoryDTOS.isNullOrEmpty()) {
            throw new ServiceException("Danh mục không tồn tại");
        }
        List<String> categoryIds = categoryDTOS.stream().map(CategoryDTO::getId).collect(Collectors.toList());
        Map<String, List<String>> mapProductIds = connector.getIdRelatedObjectsById(
                Category.class.getAnnotation(Document.class).collection(),
                categoryIds,
                Course.class.getAnnotation(Document.class).collection(),
                EnumConnectorType.COURSE_TO_CATEGORY.name());
        if (!mapProductIds.isEmpty()) {
            if (!mapProductIds.get(categoryId).isNullOrEmpty()) {
                throw new ServiceException("Không thể xoá danh mục đã có khóa học");
            }
            mapProductIds.remove(categoryId);
            if (!mapProductIds.isEmpty()) {
                throw new ServiceException("Không thể xoá danh mục, do danh mục con đã có khóa học");
            }
        }
        categoryRepository.deleteAllById(categoryIds);
    }

    private Category saveCategory(Category category) {
        if (category.getLevel() > 3 || category.getLevel() < 1) {
            throw new ServiceException("Cấp danh mục phải từ 1 đến 3");
        }
        if (null == category.getId()) {
            category.setId(sequenceValueItemRepository.getSequence(Category.class));
        } else {
            category.setUpdatedAt(new Date());
        }
        category.setName(category.getName().trim());
        category.setSlug(StringUtils.getSlug(category.getName().trim()) + "-" + category.getId());
        categoryRepository.save(category);

        if (category.getLevel() == 1) {
            List<Category> categories = categoryRepository.findAll();
            List<Category> childs = new ArrayList<>();
            buildCategoryChild(category.getId(), categories, childs);

            if (!childs.isEmpty()) {
                categoryRepository.saveAll(childs);
            }
        }
        return category;
    }

    private void buildCategoryChild(String parentId, List<Category> categoryAll, List<Category> categoryChild) {
        for (Category category : categoryAll) {
            if (!category.getParentId().isBlankOrNull() && category.getParentId().equals(parentId)) {
                categoryChild.add(category);
                buildCategoryChild(category.getId(), categoryAll, categoryChild);
            }
        }
    }


    private Category buildEntity(CategoryDTO inputDTO) {
        Category category = Category.builder()
                .name(inputDTO.getTitle())
                .nameMode(StringUtils.stripAccents(inputDTO.getTitle()))
                .createdBy(getUserIdFromContext())
                .createdAt(new Date())
                .build();
        if (!inputDTO.getId().isBlankOrNull()) {
            category.setId(inputDTO.getId());
            category.setUpdatedAt(inputDTO.getUpdatedAt() != null ? inputDTO.getUpdatedAt() : null);
            category.setUpdatedBy(inputDTO.getUpdateBy() != null ? inputDTO.getUpdateBy() : null);
        }
        if (!inputDTO.getParentId().isBlankOrNull()) {
            Optional<Category> parent = categoryRepository.findById(inputDTO.getParentId());
            if (parent.isEmpty()) {
                throw new ServiceException("Không tìm thấy danh mục cha trong hệ thống!");
            }
            if (parent.get().getLevel() == 3) {
                throw new ServiceException("Cấp danh mục không được lớn hơn 3!");
            }
            category.setParentId(inputDTO.getParentId());
            category.setLevel(parent.get().getLevel() + 1);

        } else {
            category.setLevel(1);
        }
        return category;
    }

    private List<CategoryDTO> buildCategoryTree(List<CategoryDTO> categoryDTOS, ParameterSearchCategory parameterSearchCategory) {
        if (categoryDTOS.isEmpty()) return new ArrayList<>();
        List<CategoryDTO> list = new ArrayList<>();
        if (parameterSearchCategory.getLevel() == null) {
            parameterSearchCategory.setLevel(1);
        }
        try {
            ParameterSearchCategory parameterSearchCategoryGt = parameterSearchCategory.clone();
            parameterSearchCategoryGt.setLevel(null);
            List<CategoryDTO> categoryGT = toDTOs(categoryRepository.searchCategories(parameterSearchCategoryGt), parameterSearchCategory.getCountTotalCourse());
            Stack<CategoryDTO> stack = new Stack<>();
            for (CategoryDTO categoryDTO : categoryDTOS) {
                stack.push(categoryDTO);
                while (!stack.empty()) {
                    CategoryDTO categoryParent = stack.pop();
                    List<CategoryDTO> categoryChild = categoryGT.stream().filter(cate -> (
                            null != cate.getParentId() &&
                                    cate.getParentId().equals(categoryParent.getId()) &&
                                    cate.getLevel() == categoryParent.getLevel() + 1
                    )).collect(Collectors.toList());
                    if (categoryChild.size() > 0) {
                        categoryParent.setChildren(categoryChild);
                        categoryChild.forEach(stack::push);
                    }
                }
                list.add(categoryDTO);
            }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return list;
    }

    private List<CategoryDTO> buildCategoryList(List<CategoryDTO> input) {
        List<CategoryDTO> categoryList = new ArrayList<>();
        buildCategoryItem(input, categoryList);
        return categoryList;
    }

    private void buildCategoryItem(List<CategoryDTO> categoryDTOS, List<CategoryDTO> categoryDTOReturn) {
        if (categoryDTOS != null && !categoryDTOS.isEmpty()) {
            for (CategoryDTO categoryDTO : categoryDTOS) {
                if (!categoryDTOReturn.contains(categoryDTO)) {
                    categoryDTOReturn.add(categoryDTO);
                    if (!categoryDTO.getChildren().isNullOrEmpty()) {
                        buildCategoryItem(categoryDTO.getChildren(), categoryDTOReturn);
                    }
                }
            }
        }
    }

    public CategoryDTO toDTO(Category entity) {
        if (entity == null) return null;
        return CategoryDTO.builder()
                .id(entity.getId())
                .title(entity.getName())
                .parentId(entity.getParentId())
                .level(entity.getLevel())
                .children(new ArrayList<>())
                .courses(new ArrayList<>())
                .createdBy(entity.getCreatedBy())
                .createAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt() : null)
                .updateBy(entity.getUpdatedBy() != null ? entity.getUpdatedBy() : null)
                .isDeleted(entity.getIsDeleted() != null ? entity.getIsDeleted() : false)
//                .totalCourse(0)
                .build();
    }

    public List<CategoryDTO> toDTOs(List<Category> entities, Boolean countCourses) {
        if (entities.isNullOrEmpty()) return null;
        List<CategoryDTO> categoryDTOS = new ArrayList<>();
        //Ảnh
        List<String> allIds = entities.stream().map(Category::getId).collect(Collectors.toList());
        List<FileRelationshipDTO> images = fileRelationshipController.getFileRelationships(allIds, EnumParentFileType.CATEGORY_IMAGE.name());
        Map<String, String> mapImageUrl = fileRelationshipController.getUrlOfFile(images);
        Map<String, List<CourseDTO>> mapCourses = new HashMap<>();
        if (countCourses != null && countCourses) {
            ListWrapper<CourseDTO> courses = courseController.searchCourseDTOS(ParameterSearchCourse.builder()
                    .categoriesIds(entities.stream().map(Category::getId).collect(Collectors.toList()))
                    .buildChild(false)
                    .isDeleted(false)
                    .build());
            for (CourseDTO courseDTO : courses.getData()) {
                for (String categoryId : courseDTO.getCategoryIds()) {
                    List<CourseDTO> courseDTOS = mapCourses.get(categoryId);
                    if (courseDTOS == null) {
                        courseDTOS = new ArrayList<>();
                    }
                    courseDTOS.add(courseDTO);
                    mapCourses.put(categoryId, courseDTOS);
                }
            }
        }

        for (Category entity : entities) {
            CategoryDTO dto = toDTO(entity);
            List<CourseDTO> coursesDTO = mapCourses.get(entity.getId());
            if(coursesDTO != null) {
                dto.setTotalCourse(coursesDTO.size());
            }
            dto.setImage(mapImageUrl.get(entity.getId()));
//            int totalCourses = (coursesDTO != null) ? coursesDTO.size() : 0;
//            dto.setTotalCourse(totalCourses);
            categoryDTOS.add(dto);
        }
        return categoryDTOS;
    }

    public List<CategoryDTO> getTopCategories(Integer top) {
        List<CategoryDTO> categories = this.searchCategoryDTOS(ParameterSearchCategory.builder().isDeleted(false).countTotalCourse(true).buildType(EnumCategoryBuildType.LIST.name()).build());
        return categories.stream().limit(top != null ? top : 6).sorted(Comparator.comparingInt(CategoryDTO::getTotalCourse).reversed()).collect(Collectors.toList());
    }
}
