package com.elearning.controller;

import com.elearning.entities.CourseDraft;
import com.elearning.handler.ServiceException;
import com.elearning.models.dtos.CourseDraftDTO;
import com.elearning.reprositories.ICourseDraftRepository;
import com.elearning.reprositories.ISequenceValueItemRepository;
import com.elearning.utils.Extensions;
import com.elearning.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
//@AllArgsConstructor
@ExtensionMethod(Extensions.class)
public class CourseDraftController {
    @Autowired
    private ICourseDraftRepository courseDraftRepository;
    @Autowired
    private ISequenceValueItemRepository sequenceValueItemRepository;
    public CourseDraftDTO createCourseDraft(CourseDraftDTO dto){
        CourseDraft courseDraft = buildEntity(dto);
//        return this.toDTOs(Collections.singletonList(this.saveCategory(courseDraft))).get(0);
        return this.toDTO(this.saveCategory(courseDraft));
    }

    public CourseDraftDTO toDTO(CourseDraft entity) {
        if (entity == null) return null;
        return CourseDraftDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .parentId(entity.getParentId())
                .level(entity.getLevel())
                .children(new ArrayList<>())
                .createdBy(entity.getCreatedBy())
                .createAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt() != null ? entity.getUpdatedAt() : null)
                .isDeleted(entity.getIsDeleted() != null ? entity.getIsDeleted() : false)
                .build();
    }

    public List<CourseDraftDTO> toDTOs(List<CourseDraft> entities) {
        if (entities.isNullOrEmpty()) return null;
        List<CourseDraftDTO> categoryDTOS = new ArrayList<>();
        for (CourseDraft entity : entities) {
            categoryDTOS.add(toDTO(entity));
        }
        return categoryDTOS;
    }

    private CourseDraft saveCategory(CourseDraft courseDraft) {
        if (courseDraft.getLevel()>3 || courseDraft.getLevel()<1){
            throw new ServiceException("Cấp khóa học phải từ 1 đến 3");
        }
        if (null == courseDraft.getId()) {
            courseDraft.setId(sequenceValueItemRepository.getSequence(CourseDraft.class));
        } else {
            courseDraft.setUpdatedAt(new Date());
        }
        courseDraft.setSlug(StringUtils.getSlug(courseDraft.getName()) + "-" + courseDraft.getId());
        courseDraftRepository.save(courseDraft);

//        if (courseDraft.getLevel() == 1) {
//            List<CourseDraft> courseDrafts = courseDraftRepository.findAll();
//            List<CourseDraft> childs = new ArrayList<>();
//            buildCourseDraftChild(courseDraft.getId(), courseDrafts, childs);
//
//            if (!childs.isEmpty()) {
//                courseDraftRepository.saveAll(childs);
//            }
//        }
        return courseDraft;
    }

    private void buildCourseDraftChild(String id, List<CourseDraft> courseDrafts, List<CourseDraft> childs) {
    }

    private CourseDraft buildEntity(CourseDraftDTO inputDTO) {
        CourseDraft courseDraft = CourseDraft.builder()
                .name(inputDTO.getName())
                .nameMode(StringUtils.stripAccents(inputDTO.getName()))
                .createdBy(inputDTO.getCreatedBy())
                .createdAt(new Date())
                .build();
        if (!inputDTO.getId().isBlankOrNull()) {
            courseDraft.setId(inputDTO.getId());
            courseDraft.setUpdatedAt(inputDTO.getUpdatedAt() != null ? inputDTO.getUpdatedAt() : null);
        }
        if (!inputDTO.getParentId().isBlankOrNull()) {
            Optional<CourseDraft> parent = courseDraftRepository.findById(inputDTO.getParentId());
            if (parent.isEmpty()) {
                throw new ServiceException("Không tìm thấy danh mục cha trong hệ thống!");
            }
            courseDraft.setParentId(inputDTO.getParentId());
            courseDraft.setLevel(parent.get().getLevel() + 1);
        } else {
            courseDraft.setLevel(1);
        }
        return courseDraft;
    }
}
