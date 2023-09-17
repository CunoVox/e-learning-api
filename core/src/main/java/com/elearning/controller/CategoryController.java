package com.elearning.controller;

import com.elearning.entities.Category;
import com.elearning.handler.ServiceException;
import com.elearning.models.dtos.CategoryDTO;
import com.elearning.models.searchs.ParameterSearchCategory;
import com.elearning.reprositories.ICategoryRepository;
import com.elearning.reprositories.ISequenceValueItemRepository;
import com.elearning.utils.Extensions;
import com.elearning.utils.StringUtils;
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
        if (parameterSearchCategory.getLevel() == null && !parameterSearchCategory.getTypeBuild().isBlankOrNull()
                && parameterSearchCategory.getTypeBuild().equals("tree")) {
            parameterSearchCategory.setLevel(1);
        }
        List<Category> categoriesEntities = categoryRepository.searchCategories(parameterSearchCategory);
        List<CategoryDTO> categories = toDTOs(categoriesEntities);
        if(categories.isNullOrEmpty()){
            return new ArrayList<>();
        }
        if (parameterSearchCategory.getTypeBuild() == null || parameterSearchCategory.getTypeBuild().equals("tree")) {
            return buildCategoryTree(categories, parameterSearchCategory);
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

    private Category saveCategory(Category category) {
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

            if (childs.size() > 0) {
                childs.forEach(c -> {
                    categoryRepository.save(c);
                });
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
                .updateBy(inputDTO.getUpdateBy() != null ? inputDTO.getUpdateBy() : null)
                .build();
        if (!inputDTO.getId().isBlankOrNull()) {
            category.setId(inputDTO.getId());
            category.setUpdatedAt(inputDTO.getUpdatedAt() != null ? inputDTO.getUpdatedAt() : null);
        }
        if (!inputDTO.getParentId().isBlankOrNull()) {
            Optional<Category> parent = categoryRepository.findById(inputDTO.getParentId());
            if (parent.isEmpty()) {
                throw new ServiceException("Không tìm thấy danh mục cha trong hệ thống!");
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
                        categoryParent.setChilds(categoryChild);
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
                .childs(new ArrayList<>())
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
