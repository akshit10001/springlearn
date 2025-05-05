package com.example.learn.controller;

import com.example.learn.model.User;
import com.example.learn.model.request.UserData;
import com.example.learn.repository.UserRepository;
import com.example.learn.services.UserService;
import org.checkerframework.checker.units.qual.A;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class UserControllerTest {

    @Autowired
    private UserController userController;

//    @Autowired
//    private UserService userService;

    @MockitoBean
    private UserRepository userRepository;

//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.initMocks(this);
//    }


    @ParameterizedTest
    @ArgumentsSource(DeleteUserByIdArguments.class)
    void checkUserCreationMethod(User user) {
        when(userRepository.save(any())).thenReturn(user);
        assertNotNull(userController.createUser(user).getBody());
    }

    @Test
    void getuserByIdTest()  {
        long id = 15;
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        User user = userController.getuserById(id).getBody();
        System.out.println(user);
        assertNotNull(user);
    }

    @Test
    void getuserByEmailTest() {
        String email = "john.doe@example.com";
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(new User()));
        User user = userController.getuserByEmail(email).getBody();
        System.out.println(user);
        assertNotNull(user);
    }

    @Test
    void updateUserTest() {
        UserData userData = new UserData();
        userData.setName("akshit");
        userData.setPassword("password123");
        String email = "john.doe@example.com";
        User user = new User(userData.getName(), email ,userData.getPassword());
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);
        assertEquals("User Updated Successfully",userController.updateUser(email, userData).getBody());
    }

    @ParameterizedTest
    @CsvSource({
            "1","2","3"
    })
    void deleteUserByIdTest(int id) {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(new User()));
        assertEquals("User deleted successfully",userController.deleteUserById(id).getBody());
    }

    @Test
    void deleteUserByEmailTest() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(new User()));
        when(userRepository.deleteByEmail(anyString())).thenReturn(Boolean.TRUE);
        assertEquals("User deleted successfully",userController.deleteUserByEmail("jane.smith@example.com").getBody());
    }


    @Test
    void deleteAllUserTest(){
        assertEquals("All users deleted successfully",userController.deleteAllUsers().getBody());
    }


}