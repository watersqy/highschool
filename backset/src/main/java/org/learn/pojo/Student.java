package org.learn.pojo;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class Student {
    private String oldId;
    @NotEmpty
    private String id;
    @NotEmpty
    private String name;
    private String department;
    private String spe;
    private String classIn;
    private Integer state;
}
