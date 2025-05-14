package com.example.learn.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserJDBCRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void updateUser(String name,String email,String password){
        String sql = "update users set name = ? where email = ? and password =?";
        jdbcTemplate.update(sql,name,email,password);
        System.out.println("this is the PR");
    }
}
