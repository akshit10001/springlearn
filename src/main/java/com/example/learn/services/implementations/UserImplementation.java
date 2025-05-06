package com.example.learn.services.implementations;

import com.example.learn.model.User;
import com.example.learn.repository.UserRepository;
import com.example.learn.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserImplementation implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User createUser(User user) {
        try {
            return userRepository.save(user);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @Override
    public User findUserById(long id) {
        try {
            Optional<User> user = userRepository.findById(id);
            return user.orElse(null);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @Override
    public User findUserByEmail(String email) {
        try {
            Optional<User> user = userRepository.findByEmail(email);
            return user.orElse(null);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @Override
    public ResponseEntity<String> deleteUserById(long id) {
        try {
            User user = findUserById(id);
            if (user != null) {
                userRepository.delete(user);
                return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<String> deleteUserByEmail(String email) {
        try {
            User user = findUserByEmail(email);
            if (user != null) {
                userRepository.deleteByEmail(email);
                return new ResponseEntity<>("User deleted successfully", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<String> updateNameByEmailAndPassword(String email, String password, String name) {
        try {
            User user = findUserByEmail(email);
            if (user == null) {
                return new ResponseEntity<>("User Not Found", HttpStatus.NOT_FOUND);
            }
            if (user.checkPassword(password)) {
                user.setName(name);
                User updatedUser = createUser(user);
                return new ResponseEntity<>(updatedUser != null ? "User Updated Successfully" : "Error In Creating User", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Invalid Password", HttpStatus.UNAUTHORIZED);
            }
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<User> findAllUser() {
        return userRepository.findAll();
    }

    @Override
    public ResponseEntity<String> deleteAllUsers() {
        try {
            userRepository.deleteAll();
            return new ResponseEntity<>("All users deleted successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
