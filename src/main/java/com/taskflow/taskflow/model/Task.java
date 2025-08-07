package com.taskflow.taskflow.model;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Data
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String title;

    private String description;
    private boolean completed=false;



    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

}
