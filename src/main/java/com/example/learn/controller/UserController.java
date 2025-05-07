package com.example.learn.controller;

import com.example.learn.model.User;
import com.example.learn.model.request.UserData;
import com.example.learn.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("create")
    public ResponseEntity<User> createUser(@RequestBody User user) throws RuntimeException {
        if (user == null || user.getPassword() == null || user.getEmail() == null || user.getName() == null) {
            throw new RuntimeException();
        }
        User response = userService.createUser(user);
        return response != null ? ResponseEntity.status(HttpStatus.CREATED).body(response) 
                              : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @GetMapping("get/{id}")
    public ResponseEntity<User> getuserById(@PathVariable long id) {
        User user = userService.findUserById(id);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @GetMapping("get/email/{email}")
    public ResponseEntity<User> getuserByEmail(@PathVariable String email) {
        User user = userService.findUserByEmail(email);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @PutMapping("update/{email}")
    public ResponseEntity<String> updateUser(@PathVariable String email, @RequestBody UserData userdata) {
        return userService.updateNameByEmailAndPassword(email, userdata.getPassword(), userdata.getName());
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> deleteUserById(@PathVariable long id) {
        return userService.deleteUserById(id);
    }

    @DeleteMapping("delete/email/{email}")
    public ResponseEntity<String> deleteUserByEmail(@PathVariable String email) throws Exception {
        if(email.isEmpty()) throw new Exception("Email cannot be empty");
        return userService.deleteUserByEmail(email);
    }

    @GetMapping("get/all")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> list = userService.findAllUser();
        return list != null ? ResponseEntity.ok(list) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("delete/all")
    public ResponseEntity<String> deleteAllUsers() {
        return userService.deleteAllUsers();
    }

}
