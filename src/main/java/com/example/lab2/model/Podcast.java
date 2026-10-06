package com.example.lab2.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Podcast extends BaseEntity {
    public enum Status { ACTIVE, ARCHIVED }

    private String title;
    private Long authorId;
    private Status status;
}