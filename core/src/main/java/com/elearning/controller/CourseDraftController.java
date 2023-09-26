package com.elearning.controller;

import com.elearning.entities.CourseDraft;
import com.elearning.handler.ServiceException;
import com.elearning.models.dtos.CourseDraftDTO;
import com.elearning.models.searchs.ParameterSearchCourseDraft;
import com.elearning.reprositories.ICourseDraftRepository;
import com.elearning.reprositories.ISequenceValueItemRepository;
import com.elearning.utils.Extensions;
import com.elearning.utils.StringUtils;
import com.elearning.utils.enumAttribute.EnumCategoryBuildType;
import lombok.experimental.ExtensionMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
//@AllArgsConstructor
@ExtensionMethod(Extensions.class)
public class CourseDraftController extends BaseController{
    @Autowired
    private ICourseDraftRepository courseDraftRepository;
    @Autowired
    private ISequenceValueItemRepository sequenceValueItemRepository;
    @Transactional
    public CourseDraftDTO createCourseDraft(CourseDraftDTO dto){
        String userId = this.getUserIdFromContext();
        if (userId != null){
            dto.setCreatedBy(userId);
        }
        dto.setId(null);
        CourseDraft courseDraft = buildEntity(dto);
//        return this.toDTOs(Collections.singletonList(this.saveCategory(courseDraft))).get(0);
        return this.toDTO(this.saveCategory(courseDraft));
    }
    public CourseDraftDTO getCourseDraftById(String courseDraftId) {
        Optional<CourseDraft> courseDraft = courseDraftRepository.findById(courseDraftId);
        if (courseDraft.isEmpty()) {
            throw new ServiceException("Không tìm thấy danh mục");
        }
        CourseDraft cD = courseDraft.get();
        ParameterSearchCourseDraft searchCategory = ParameterSearchCourseDraft
                .builder().level(cD.getLevel()).build();
        return buildCourseDraftTree(Collections.singletonList(toDTO(cD)), searchCategory).get(0);
    }
    public List<CourseDraftDTO> searchCourseDraftDTOS(ParameterSearchCourseDraft parameterSearchCourseDraft) {
        if (parameterSearchCourseDraft.getLevel() == null && !parameterSearchCourseDraft.getBuildType().isBlankOrNull()
                && parameterSearchCourseDraft.getBuildType().equals(EnumCategoryBuildType.TREE.name())) {
            parameterSearchCourseDraft.setLevel(1);
        }
        List<CourseDraft> courseDraftsEntities = courseDraftRepository.searchCourseDraft(parameterSearchCourseDraft);
        List<CourseDraftDTO> courseDraftDTOS = toDTOs(courseDraftsEntities);
        if(courseDraftDTOS.isNullOrEmpty()){
            return new ArrayList<>();
        }
        if (parameterSearchCourseDraft.getBuildType() == null ||
                parameterSearchCourseDraft.getBuildType().equals(EnumCategoryBuildType.TREE.name())) {
            return buildCourseDraftTree(courseDraftDTOS, parameterSearchCourseDraft);
        }
        return courseDraftDTOS;
    }

    private List<CourseDraftDTO> buildCourseDraftTree(List<CourseDraftDTO> courseDraftDTOS,
                                                      ParameterSearchCourseDraft parameterSearchCourseDraft) {
        if (courseDraftDTOS.isEmpty()) return new ArrayList<>();
        List<CourseDraftDTO> list = new ArrayList<>();
        if (parameterSearchCourseDraft.getLevel() == null) {
            parameterSearchCourseDraft.setLevel(1);
        }
        try {
            ParameterSearchCourseDraft parameterSearchCourseDraftGt = parameterSearchCourseDraft.clone();
            parameterSearchCourseDraftGt.setLevel(null);
            List<CourseDraftDTO> courseDraftGT = toDTOs(courseDraftRepository.searchCourseDraft(parameterSearchCourseDraftGt));
            Stack<CourseDraftDTO> stack = new Stack<>();
            for (CourseDraftDTO courseDraftDTO : courseDraftDTOS) {
                stack.push(courseDraftDTO);
                while (!stack.empty()) {
                    CourseDraftDTO categoryParent = stack.pop();
                    List<CourseDraftDTO> categoryChild = courseDraftGT.stream().filter(cate -> (
                            null != cate.getParentId() &&
                                    cate.getParentId().equals(categoryParent.getId()) &&
                                    cate.getLevel() == categoryParent.getLevel() + 1
                    )).collect(Collectors.toList());
                    if (!categoryChild.isEmpty()) {
                        categoryParent.setChildren(categoryChild);
                        categoryChild.forEach(stack::push);
                    }
                }
                list.add(courseDraftDTO);
            }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return list;
    }

    private CourseDraft buildEntity(CourseDraftDTO inputDTO) {
        if (inputDTO.getName().isBlankOrNull()){
            throw new ServiceException("Tiêu đề không được để trống!");
        }
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
    public CourseDraftDTO toDTO(CourseDraft entity) {
        if (entity == null) return null;
        return CourseDraftDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .slug(entity.getSlug())
                .nameMode(entity.getNameMode())
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
    @Transactional(rollbackFor = {NullPointerException.class, ServiceException.class})
    public CourseDraft saveCategory(CourseDraft courseDraft) {
        if (courseDraft.getLevel()>3 || courseDraft.getLevel()<1){
            throw new ServiceException("Cấp khóa học phải từ 1 đến 3");
        }
        if (null == courseDraft.getId()) {
            courseDraft.setId(sequenceValueItemRepository.getSequence(CourseDraft.class));
        } else {
            courseDraft.setUpdatedAt(new Date());
        }
        courseDraft.setSlug(StringUtils.getSlug(courseDraft.getName()) + "-" + courseDraft.getId());
        courseDraft = courseDraftRepository.save(courseDraft);

//        if (courseDraft.getLevel() == 1) {
//            List<CourseDraft> courseDrafts = courseDraftRepository.findAll();
//            List<CourseDraft> children = new ArrayList<>();
//            buildCourseDraftChild(courseDraft.getId(), courseDrafts, children);
//
//            if (!children.isEmpty()) {
//                courseDraftRepository.saveAll(children);
//            }
//        }
        return courseDraft;
    }

    private void buildCourseDraftChild(String id, List<CourseDraft> courseDrafts, List<CourseDraft> childs) {
    }


}
