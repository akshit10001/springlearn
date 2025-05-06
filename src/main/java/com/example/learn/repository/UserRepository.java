package com.example.learn.repository;

import com.example.learn.model.User;
import javax.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

//    @Transactional
//    @Modifying
//    @Query("DELETE FROM User u WHERE u.email = ?1")
//    void deleteByEmail(String email);

    @Transactional
    boolean deleteByEmail(String email);

}
