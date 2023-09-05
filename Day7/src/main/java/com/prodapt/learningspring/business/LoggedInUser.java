package com.prodapt.learningspring.business;

import com.prodapt.learningspring.entity.Login;

import lombok.Data;

@Data
public class LoggedInUser {
    private Login loggedInUser;
}