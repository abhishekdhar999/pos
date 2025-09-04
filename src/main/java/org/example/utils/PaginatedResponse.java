package org.example.utils;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PaginatedResponse<T> {
    private List<T> data;
    private Integer page;
    private Integer size;
    private Long totalPages;
}
