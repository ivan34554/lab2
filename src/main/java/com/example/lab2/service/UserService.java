package com.example.lab2.service;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import com.example.lab2.exception.NotFoundException;
import com.example.lab2.model.User;
import com.example.lab2.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository users;
    private final Clock clock;

    public UserService(UserRepository users, Clock clock) {
        this.users = users;
        this.clock = clock;
    }

    public List<User> findAll() {
        return users.findAll();
    }

    public User findById(Long id) {
        return users.findById(id)
                .orElseThrow(() -> new NotFoundException("User " + id + " not found"));
    }

    public User create(String username, String email) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setCreatedAt(Instant.now(clock));
        return users.save(user);
    }

    public User update(Long id, String username, String email) {
        User user = findById(id);
        if (username != null) user.setUsername(username);
        if (email != null) user.setEmail(email);
        return users.save(user);
    }

    public void delete(Long id) {
        findById(id);
        users.deleteById(id);
    }
}