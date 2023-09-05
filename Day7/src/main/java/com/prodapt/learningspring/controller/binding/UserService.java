package com.prodapt.learningspring.controller.binding;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.prodapt.learningspring.controller.exception.ForumUserNotFoundException;
import com.prodapt.learningspring.entity.Login;
import com.prodapt.learningspring.repository.LoginUserRepository;

@Service
public class UserService {

    @Autowired
    private LoginUserRepository loginUserRepository;

    public Optional<Login> authenticate(String username, String password) {
        Optional<Login> optUser = loginUserRepository.findByName(username);
        if (optUser.isEmpty()) {
            throw new ForumUserNotFoundException("User not found");
        }
        if (!optUser.get().getPassword().equals(password)) {
            return Optional.empty();
        }
        return optUser;
    }

    public Login create(Login user) {
        return loginUserRepository.save(user);
    }
    
}

