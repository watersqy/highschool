package org.learn.pojo;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class Teacher {
    private String oldId;
    @NotEmpty
    private String id;
    @NotEmpty
    private String name;
    private String department;
    private Integer state;
}
