package com.prodapt.learningspring.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.prodapt.learningspring.entity.Login;

@Repository
public interface LoginUserRepository extends CrudRepository<Login, Long>{
  public Optional<Login> findByName(String name); 
  
}
