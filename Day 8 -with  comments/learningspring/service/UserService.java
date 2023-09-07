package com.prodapt.learningspring.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.prodapt.learningspring.entity.User;
import com.prodapt.learningspring.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository usersRepository;

    public Optional<User> authenticate(String username, String password) {
        Optional<User> optUser = usersRepository.findByName(username);
        if (!optUser.get().getPassword().equals(password)) {
            return Optional.empty();
        }
        return optUser;
    }

    public User create(User user) {
        return usersRepository.save(user);
    }

}