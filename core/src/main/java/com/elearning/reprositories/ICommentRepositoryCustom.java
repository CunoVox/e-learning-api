package com.elearning.reprositories;

import com.elearning.entities.Comment;
import com.elearning.models.searchs.ParameterSearchComment;
import com.elearning.models.wrapper.ListWrapper;

public interface ICommentRepositoryCustom {
    ListWrapper<Comment> searchComments(ParameterSearchComment parameterSearchComment);
}
