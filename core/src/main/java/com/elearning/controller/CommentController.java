package com.elearning.controller;

import com.elearning.entities.Comment;
import com.elearning.entities.Course;
import com.elearning.entities.User;
import com.elearning.handler.ServiceException;
import com.elearning.models.dtos.CommentDTO;
import com.elearning.models.dtos.FileRelationshipDTO;
import com.elearning.models.dtos.UserDTO;
import com.elearning.models.dtos.UserProfileDTO;
import com.elearning.models.searchs.ParameterSearchComment;
import com.elearning.models.wrapper.ListWrapper;
import com.elearning.reprositories.ICommentRepository;
import com.elearning.reprositories.ICourseRepository;
import com.elearning.reprositories.ISequenceValueItemRepository;
import com.elearning.reprositories.IUserRepository;
import com.elearning.utils.Extensions;
import com.elearning.utils.enumAttribute.EnumCommentType;
import com.elearning.utils.enumAttribute.EnumParentFileType;
import lombok.experimental.ExtensionMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@ExtensionMethod(Extensions.class)
public class CommentController extends BaseController {
    @Autowired
    private ICommentRepository commentRepository;
    @Autowired
    private ISequenceValueItemRepository sequenceValueItemRepository;
    @Autowired
    private ICourseRepository courseRepository;
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private FileRelationshipController fileRelationshipController;
    @Autowired
    private UserController userController;

    public Comment buildEntity(CommentDTO commentDTO) {
        Comment comment = null;
        boolean isUpdate = false;
        if (!commentDTO.getId().isBlankOrNull()) {
            comment = commentRepository.findById(commentDTO.getId()).orElse(null);
            if (comment == null) {
                commentDTO.setId(null);
            } else {
                isUpdate = true;
            }
        }

        if (isUpdate) {
            comment.setContent(commentDTO.getContent());
//            comment.setLevel(commentDTO.getLevel());
//            comment.setParentId(commentDTO.getParentId());
//            comment.setReferenceId(commentDTO.getReferenceId());
//            comment.setType(commentDTO.getType());
//            comment.setUserId(commentDTO.getUserId());
        } else {
            if (commentDTO.getContent().isBlankOrNull()) {
                throw new RuntimeException("Content is invalid");
            }
            if (commentDTO.getContent().isBlankOrNull()) {
                throw new ServiceException("nội dung không được để trống!");
            }
            if (commentDTO.getType() == null) {
                throw new ServiceException("Type không được để trống!");
            }
            if (commentDTO.getReferenceId().isBlankOrNull()) {
                throw new ServiceException("ReferenceId không được để trống!");
            }
            if (!commentDTO.getParentId().isBlankOrNull()) {
                Comment parentComment = commentRepository.findById(commentDTO.getParentId()).orElse(null);
                if (parentComment == null) {
                    throw new ServiceException("Bình luận cha không tồn tại!");
                }
//            if (parentComment.getLevel() == 2) {
//                throw new ServiceException("Bình luận cha không thể là bình luận cấp 2!");
//            }
                commentDTO.setLevel(parentComment.getLevel() + 1);
                commentDTO.setParentId(parentComment.getId());
            } else {
                commentDTO.setLevel(1);
            }
            if (commentDTO.getType().equals(EnumCommentType.COURSE)) {
                Course course = courseRepository.findById(commentDTO.getReferenceId()).orElse(null);
                if (course == null) {
                    throw new ServiceException("Khóa học không tồn tại!");
                }
            } else {
                //TODO: check other type
            }
            comment = Comment.builder()
//                .id(commentDTO.getId())
                    .content(commentDTO.getContent())
                    .level(commentDTO.getLevel())
                    .parentId(commentDTO.getParentId())
                    .referenceId(commentDTO.getReferenceId())
                    .type(commentDTO.getType())
                    .createdAt(new Date())
                    .userId(commentDTO.getUserId())
                    .createdBy(commentDTO.getUserId())
                    .build();
        }
        return comment;
    }

    public CommentDTO createComment(CommentDTO commentDTO) {
        String userId = getUserIdFromContext();

        if (userId == null) throw new RuntimeException("User not found");


        commentDTO.setUserId(userId);
        commentDTO.setCreateBy(userId);
        Comment comment = buildEntity(commentDTO);

        comment = saveComment(comment);

        return toDTO(comment);
    }
    public void deleteComment(String id) {
        Comment comment = commentRepository.findById(id).orElse(null);
        if (comment == null) {
            throw new ServiceException("Bình luận không tồn tại!");
        }
        String userId = getUserIdFromContext();
        if (!userId.equals(comment.getUserId())) {
            throw new ServiceException("Bạn không có quyền xóa bình luận này!");
        }
        commentRepository.delete(comment);
//        comment.setIsDeleted(true);
//        commentRepository.save(comment);
    }
    public Comment saveComment(Comment comment) {
        if (comment.getLevel() > 2 || comment.getLevel() < 1) {
            throw new ServiceException("Cấp bình luận phải từ 1 đến 2");
        }
        if (null == comment.getId()) {
            comment.setId(sequenceValueItemRepository.getSequence(Comment.class));
        } else {
            comment.setUpdatedAt(new Date());
        }
        comment = commentRepository.save(comment);
        return comment;
    }

    public ListWrapper<CommentDTO> searchCommentDTOs(ParameterSearchComment parameterSearchComment) {
        ListWrapper<Comment> listWrapper = commentRepository.searchComments(parameterSearchComment);
        List<CommentDTO> commentDTOS = buildCommentDTO(listWrapper.getData(), parameterSearchComment);
        return ListWrapper.<CommentDTO>builder()
                .currentPage(listWrapper.getCurrentPage())
                .totalPage(listWrapper.getTotalPage())
                .total(listWrapper.getTotal())
                .maxResult(listWrapper.getMaxResult())
                .data(commentDTOS)
                .build();
    }

    private List<CommentDTO> buildCommentDTO(List<Comment> comments, ParameterSearchComment parameterSearchComment) {
        List<CommentDTO> commentDTOS = new ArrayList<>();
        //Chi tiết người tạo comment
        List<String> commentIds = comments.stream().map(Comment::getId).collect(Collectors.toList());
        List<Comment> commentLevel2 = commentRepository.findAllByParentIdInAndIsDeletedNotIn(commentIds, Collections.singletonList(true));
        List<String> level2Ids;
        if (!commentLevel2.isNullOrEmpty()) {
//            level2Ids = commentLevel2.stream().map(Comment::getId).collect(Collectors.toList());
            comments.addAll(commentLevel2);
        }
        List<String> createdUserIds = comments.stream().map(Comment::getUserId).collect(Collectors.toList());
        Map<String, UserDTO> userDTOMap = userController.getUserByIds(createdUserIds);
        for (Comment comment : comments) {
            CommentDTO commentDTO = toDTO(comment);
            commentDTO.setReply(new ArrayList<>());
            commentDTO.setUserDetail(userController.toUserProfileDTO(userDTOMap.get(comment.getUserId())));
            commentDTOS.add(commentDTO);
        }

        List<CommentDTO> commentsWithReplies = new ArrayList<>();

        for (CommentDTO commentDTO : commentDTOS) {
            if (commentDTO.getLevel() == 1) {
                commentsWithReplies.add(commentDTO);
            } else {
                for (CommentDTO commentDTO1 : commentDTOS) {
                    if (commentDTO1.getId().equals(commentDTO.getParentId())) {
                        commentDTO1.getReply().add(commentDTO);
                    }
                }
            }
        }

        return commentsWithReplies;
    }

    private UserProfileDTO getUserProfileDTO(String userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) return null;
        List<FileRelationshipDTO> fileRelationshipDTO = fileRelationshipController.getFileRelationships(Collections.singletonList(user.getId()), EnumParentFileType.USER_AVATAR.name());

        return UserProfileDTO.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .avatar(!fileRelationshipDTO.isNullOrEmpty() ? fileRelationshipDTO.get(fileRelationshipDTO.size() - 1).getPathFile() : null)
                .build();
    }

    public CommentDTO toDTO(Comment comment) {
        if (comment == null) return null;

        return CommentDTO.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .reply(new ArrayList<>())
                .level(comment.getLevel())
                .parentId(comment.getParentId())
                .referenceId(comment.getReferenceId())
                .type(comment.getType())
                .userId(comment.getUserId())
//                .userDetail(getUserProfileDTO(comment.getUserId()))
                .createAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt() != null ? comment.getUpdatedAt() : null)
                .isDeleted(comment.getIsDeleted() != null ? comment.getIsDeleted() : false)
                .build();
    }
}
