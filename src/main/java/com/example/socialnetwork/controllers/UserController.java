package com.example.socialnetwork.controllers;

import com.example.socialnetwork.domain.User;
import com.example.socialnetwork.services.UserService;
import com.example.socialnetwork.utils.observer.Observer;

public class UserController implements Observer
{
    private UserService srv;

    private User user;

    public void setData(UserService srv, User user)
    {
        this.srv = srv;
        this.user = user;
    }

    @Override
    public void update()
    {
        System.out.println("updated");
    }

}
