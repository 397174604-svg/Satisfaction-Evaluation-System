package com.example.gbmzcpb.jpa;

import com.example.gbmzcpb.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

    public User findByUsernameAndPassword(String username, String password);
    boolean existsByUsernameAndPassword(String username, String password);
    boolean existsByUsername(String username);
    public User findByUsername(String username);
}
