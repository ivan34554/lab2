package com.example.lab2.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User extends BaseEntity {
    private String username;
    private String email;
}