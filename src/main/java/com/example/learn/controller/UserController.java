package com.example.learn.controller;


import com.example.learn.model.User;
import com.example.learn.model.request.UserData;
import com.example.learn.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users/")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("create")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        if (user == null || user.getPassword() == null || user.getEmail() == null || user.getName() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        User response = userService.createUser(user);
        if (response != null) {
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        }
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("get/{id}")
    public ResponseEntity<User> getuserById(@PathVariable long id) {
        User user = userService.findUserById(id);
        if (user == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @GetMapping("get/email/{email}")
    public ResponseEntity<User> getuserByEmail(@PathVariable String email) {
        User user = userService.findUserByEmail(email);
        if (user == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(user, HttpStatus.OK);
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
        if(email.isEmpty() || email.length() == 0) throw new Exception();
        return userService.deleteUserByEmail(email);
    }


    @GetMapping("get/all")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> list = userService.findAllUser();

        if (list == null) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @DeleteMapping("delete/all")
    public ResponseEntity<String> deleteAllUsers() {
        return userService.deleteAllUsers();
    }

}
