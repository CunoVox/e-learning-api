package com.elearning.controller;

import com.elearning.entities.Category;
import com.elearning.handler.ServiceException;
import com.elearning.models.dtos.CategoryDTO;
import com.elearning.models.searchs.ParameterSearchCategory;
import com.elearning.reprositories.ICategoryRepository;
import com.elearning.reprositories.ISequenceValueItemRepository;
import com.elearning.utils.Extensions;
import com.elearning.utils.StringUtils;
import com.elearning.utils.enumAttribute.EnumCategoryBuildType;
import lombok.experimental.ExtensionMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@ExtensionMethod(Extensions.class)
public class CategoryController {
    @Autowired
    private ICategoryRepository categoryRepository;

    @Autowired
    private ISequenceValueItemRepository sequenceValueItemRepository;

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
        if (parameterSearchCategory.getLevel() == null && !parameterSearchCategory.getBuildType().isBlankOrNull()
                && parameterSearchCategory.getBuildType().equals(EnumCategoryBuildType.TREE.name())
                && parameterSearchCategory.getParentIds().isNullOrEmpty()) {
            parameterSearchCategory.setLevel(1);
        }
        List<Category> categoriesEntities = categoryRepository.searchCategories(parameterSearchCategory);
        List<CategoryDTO> categories = toDTOs(categoriesEntities);
        if(categories.isNullOrEmpty()){
            return new ArrayList<>();
        }
        if (parameterSearchCategory.getBuildType() == null ||
                parameterSearchCategory.getBuildType().equals(EnumCategoryBuildType.TREE.name())) {
            ParameterSearchCategory searchCategory = ParameterSearchCategory.builder().level(parameterSearchCategory.getLevel()).build();
            return buildCategoryTree(categories, searchCategory);
        }
        return categories;
    }

    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category = buildEntity(categoryDTO);
        //TODO: chưa có lưu image
        return this.toDTOs(Collections.singletonList(this.saveCategory(category))).get(0);
    }

    public CategoryDTO updateCategory(CategoryDTO categoryDTO){
        Optional<Category> validateCate = categoryRepository.findById(categoryDTO.getId());
        if (validateCate.isEmpty()){
            throw new ServiceException("Danh mục không tồn tại");
        }
        Category category = buildEntity(categoryDTO);
        //TODO: chưa có cập nhật image
        return this.toDTOs(Collections.singletonList(this.saveCategory(category))).get(0);
    }

    public void deleteCategory(String categoryId, String deleteBy){
        Optional<Category> category = categoryRepository.findById(categoryId);
        if (category.isEmpty()){
            throw new ServiceException("Danh mục không tồn tại");
        }
        category.get().setUpdateBy(deleteBy);
        category.get().setIsDeleted(true);
        this.saveCategory(category.get());
    }

    private Category saveCategory(Category category) {
        if (category.getLevel()>3 || category.getLevel()<1){
            throw new ServiceException("Cấp danh mục phải từ 1 đến 3");
        }
        if (null == category.getId()) {
            category.setId(sequenceValueItemRepository.getSequence(Category.class));
        } else {
            category.setUpdatedAt(new Date());
        }
        category.setSlug(StringUtils.getSlug(category.getName()) + "-" + category.getId());
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
                .createdBy(inputDTO.getCreatedBy())
                .createdAt(new Date())
                .build();
        if (!inputDTO.getId().isBlankOrNull()) {
            category.setId(inputDTO.getId());
            category.setUpdatedAt(inputDTO.getUpdatedAt() != null ? inputDTO.getUpdatedAt() : null);
            category.setUpdateBy(inputDTO.getUpdateBy() != null ? inputDTO.getUpdateBy() : null);
        }
        if (!inputDTO.getParentId().isBlankOrNull()) {
            Optional<Category> parent = categoryRepository.findById(inputDTO.getParentId());
            if (parent.isEmpty()) {
                throw new ServiceException("Không tìm thấy danh mục cha trong hệ thống!");
            }
            if(parent.get().getLevel() == 3){
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
            List<CategoryDTO> categoryGT = toDTOs(categoryRepository.searchCategories(parameterSearchCategoryGt));
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
                .updateBy(entity.getUpdateBy() != null ? entity.getUpdateBy() : null)
                .isDeleted(entity.getIsDeleted() != null ? entity.getIsDeleted() : false)
                .build();
    }

    public List<CategoryDTO> toDTOs(List<Category> entities) {
        if (entities.isNullOrEmpty()) return null;
        List<CategoryDTO> categoryDTOS = new ArrayList<>();
        for (Category entity : entities) {
            categoryDTOS.add(toDTO(entity));
        }
        return categoryDTOS;
    }
}
