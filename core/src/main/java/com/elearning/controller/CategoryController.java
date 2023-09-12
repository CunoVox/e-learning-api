package com.elearning.controller;

import com.elearning.entities.Category;
import com.elearning.handler.ServiceException;
import com.elearning.models.dtos.CategoryDTO;
import com.elearning.reprositories.ICategoryRepository;
import com.elearning.reprositories.ISequenceValueItemRepository;
import com.elearning.utils.Extensions;
import com.elearning.utils.StringUtils;
import lombok.experimental.ExtensionMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
@ExtensionMethod(Extensions.class)
public class CategoryController {
    @Autowired
    private ICategoryRepository categoryRepository;

    @Autowired
    private ISequenceValueItemRepository sequenceValueItemRepository;

    public CategoryDTO createCategory(CategoryDTO inputDTO, String createBy) {
        Category category = buildEntity(inputDTO, createBy);
        this.saveCategory(category);
        //TODO: chưa có lưu image
        return this.toDTOs(Collections.singletonList(category)).get(0);
    }

    private Category saveCategory(Category category) {
        if (null == category.getId()) {
                category.setId(sequenceValueItemRepository.getSequence(Category.class));
        }
        category.setSlug(StringUtils.getSlug(category.getName())+ "-" + category.getId());
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


    private Category buildEntity(CategoryDTO inputDTO, String createBy) {
        Category category = Category.builder()
                .name(inputDTO.getTitle())
                .nameMode(StringUtils.stripAccents(inputDTO.getTitle()))
                .createdBy(createBy)
                .createdAt(new Date())
                .build();
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

    public CategoryDTO toDTO(Category entity){
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
                .isDeleted(entity.getIsDeleted()!=null? entity.getIsDeleted() : false)
                .build();
    }

    public List<CategoryDTO> toDTOs(List<Category> entities) {
        if (entities.isNullOrEmpty()) return null;
        List<CategoryDTO> categoryDTOS = new ArrayList<>();
        for (Category entity : entities){
            categoryDTOS.add(toDTO(entity));
        }
        return categoryDTOS;
    }
}
