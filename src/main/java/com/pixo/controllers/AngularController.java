package com.pixo.controllers;

import com.pixo.models.Image;
import com.pixo.models.User;
import com.pixo.repositories.CommentRepository;
import com.pixo.repositories.ImageRepository;
import com.pixo.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
public class AngularController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    ImageRepository imageRepository;
    @Autowired
    CommentRepository commentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @RequestMapping("/like2/{id}")
    public boolean like2 (@PathVariable("id") long id) {
        Image image = imageRepository.findOne(id);
        User user =  getLoggedIn();
        if(isLiked(image)){
            image.getLikes().remove(user);
            imageRepository.save(image);
            user.getLikes().remove(image);
            userRepository.save(user);
            return false;
        }
        else{
            image.getLikes().add(user);
            imageRepository.save(image);
            user.getLikes().add(image);
            userRepository.save(user);
            return true;
        }
    }

    @RequestMapping("/likeCount/{id}")
    public int likeCount (@PathVariable("id") long id) {
        Image image = imageRepository.findOne(id);
        return image.likesCount();
    }
    @RequestMapping("/isLiked/{id}")
    public boolean isLiked (@PathVariable("id") long id) {
        Image image = imageRepository.findOne(id);
       return isLiked(image);
    }

    private boolean isLiked(Image image){
        boolean likes = false;
        User user =  getLoggedIn();
        for(User u: image.getLikes()){
            likes = likes || (u.getId()==user.getId());
        }
        return likes;
    }

    private boolean isFollower(User uploader){
        boolean follow = false;
        User user =  getLoggedIn();
        for(User u: user.getFollowsList()){
            follow = follow || (u.getId()==uploader.getId());
        }
        return follow;
    }

    private User getLoggedIn(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = auth.getName();
        return userRepository.findByName(name);

    }
}
