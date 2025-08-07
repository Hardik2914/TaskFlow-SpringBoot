package com.taskflow.taskflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TaskDTO {
    private Long id;
    @NotBlank(message="Title cannot be blank")
    @Size(min=3,max=100,message = "Title must be between 3 and 100 letters")
    private String title;
    @NotBlank(message = "description cannot be blank")
    @Size(max=500,message = "The description cannot be more than 500 letters")
    private String description;
    private boolean complete;
}
