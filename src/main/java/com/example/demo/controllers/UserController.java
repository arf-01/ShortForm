package com.example.demo.controllers;

import com.example.demo.model.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class UserController {

    private final List<User> users = new ArrayList<>(List.of(
            new User("Alice", "NID-111"),
            new User("Bob", "NID-222")
    ));

    // GET /users → list everyone
    @GetMapping("/users")
    public List<User> getUsers() {
        return users;
    }

    // GET /users/1 → just Alice (path variable, 0-based index for now)
    @GetMapping("/users/{index}")
    public User getUser(@PathVariable int index) {
        return users.get(index);
    }

    // POST /users with JSON body:
    // { "name": "Charlie", "nationalId": "NID-333" }
    @PostMapping("/users")
    public User createUser(@RequestBody User user) {
        users.add(user);
        return user;   // echoes back what was saved
    }
}
