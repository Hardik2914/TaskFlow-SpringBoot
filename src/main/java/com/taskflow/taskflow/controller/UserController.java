package com.taskflow.taskflow.controller;

import com.taskflow.taskflow.SessionUser;
import com.taskflow.taskflow.model.User;
import com.taskflow.taskflow.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserRepository userRepo;

    @PostMapping("/register")
    public String register(@RequestBody User user){
        Optional<User> optionalUser = userRepo.findByUsername(user.getUsername());
        if(optionalUser.isPresent()){
            return "User already exists";
        }
        else {
            userRepo.save(user);
            return "User registered successfully";
        }
    }

    @PostMapping("/login")
    public String login(@RequestBody User user){
        Optional<User> optionalUser = userRepo.findByUsername(user.getUsername());
        if(optionalUser.isPresent()){
            User user1 = optionalUser.get();
            if(user1.getPassword().equals(user.getPassword())){
                SessionUser.login((long) user1.getId());
                return "Logged in successfully";
            }
            else
                return "Wrong password";
        }
        else
            return "Wrong username or password";
    }

    @PostMapping("/logout")
    public String logout(){
        if(SessionUser.isLoggedin()){
            SessionUser.logout();
            return "Logged out Sucessfully";
        }
        else {
            return "User is not logged in";
        }
    }

}
