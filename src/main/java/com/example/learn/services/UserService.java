package com.example.learn.services;

import com.example.learn.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {

    User createUser(User user);

    User findUserById(long id);
    User findUserByEmail(String email);

    ResponseEntity<String> deleteUserById(long id);
    ResponseEntity<String> deleteUserByEmail(String email);

    ResponseEntity<String> updateNameByEmailAndPassword(String email, String password, String name);

    List<User> findAllUser();
    ResponseEntity<String> deleteAllUsers();
}
