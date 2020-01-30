package com.notes.repository;

import com.notes.model.User;
//import org.springframework.data.repository.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    void delete(User user);

    List<User> findAll();

    User save(User user);

    Optional<User> findByUsername(String username);

    Optional<User> findById(String id);
}