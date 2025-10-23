package com.example.springauth.repository;

import com.example.springauth.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username); //메서드 이름 기반으로 쿼리 생성

    Optional<User> findByEmail(String email);


}
