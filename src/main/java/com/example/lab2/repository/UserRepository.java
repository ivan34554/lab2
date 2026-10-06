package com.example.lab2.repository;

import org.springframework.stereotype.Repository;
import com.example.lab2.model.User;

@Repository
public class UserRepository extends InMemoryRepository<User> {
}