package com.elearning.models.wrapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListWrapper<T> {

    private long total;

    private long currentPage;

    private long maxResult;

    private long totalPage;

    private List<T> data;

}
