package com.prodapt.learningspring.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.prodapt.learningspring.entity.User;



public interface UserRepository extends CrudRepository<User, Integer> {
    public Optional<User> findByName(String name);

}
