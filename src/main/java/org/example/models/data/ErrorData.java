package org.example.models.data;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ErrorData<T> {
    private Integer id;
    private List<T> errorList;

}
