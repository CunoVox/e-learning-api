package com.elearning.apis;

import com.elearning.controller.CommentController;
import com.elearning.models.dtos.CommentDTO;
import com.elearning.models.searchs.ParameterSearchComment;
import com.elearning.models.wrapper.ListWrapper;
import com.elearning.utils.Extensions;
import com.elearning.utils.enumAttribute.EnumCommentType;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
@Tag(name = "Comment", description = "Comment API")
@ExtensionMethod(Extensions.class)
public class CommentAPI {
    @Autowired
    private CommentController commentController;

    @PostMapping("")
    public CommentDTO createComment(@RequestBody CommentDTO commentDTO) {
        return commentController.createComment(commentDTO);
    }

    @GetMapping
    public ListWrapper<CommentDTO> getComments(@RequestParam(value = "level", required = false) Integer level,
//                                               @RequestParam(value = "build_type") EnumCategoryBuildType buildType,
                                               @RequestParam(value = "is_deleted", required = false) Boolean isDeleted,
                                               @RequestParam(value = "reference_id", required = false) String referenceId,
                                               @RequestParam(value = "type", required = false) EnumCommentType type,
                                               @RequestParam(value = "current_page", required = false) @Min(value = 1, message = "currentPage phải lớn hơn 0") @Parameter(description = "Default: 1") Integer currentPage,
                                               @RequestParam(value = "max_result", required = false) @Min(value = 1, message = "maxResult phải lớn hơn 0") @Max(value = 100, message = "maxResult phải bé hơn 101") @Parameter(description = "Default: 20; Size range: 1-100") Integer maxResult) {
        if (currentPage == null || currentPage == 0) {
            currentPage = 1;
        }
        if (maxResult == null || maxResult == 0) {
            maxResult = 20;
        }
        Long startIndex = ((long) (currentPage - 1) * maxResult);

        ParameterSearchComment parameterSearchComment = new ParameterSearchComment();
        if (level != null) {
            parameterSearchComment.setLevel(level);
        }
        if (isDeleted != null) {
            parameterSearchComment.setIsDeleted(isDeleted);
        }
        if (referenceId != null) {
            parameterSearchComment.setReferenceId(referenceId);
        }
        if (type != null) {
            parameterSearchComment.setType(type);
        }

        return commentController.searchCommentDTOs(parameterSearchComment);
    }
//    @GetMapping("/{referenceId}")

}
