package com.example.socialnetwork.controllers;

import com.example.socialnetwork.domain.DTOs.FriendshipDTO;
import com.example.socialnetwork.domain.User;
import com.example.socialnetwork.services.UserService;
import com.example.socialnetwork.utils.observer.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

public class UserController implements Observer
{
    private UserService srv;
    private User user;
    private ObservableList<FriendshipDTO> modelFriendships = FXCollections.observableArrayList();
    private ObservableList<FriendshipDTO> modelFriendRequests = FXCollections.observableArrayList();
    private ObservableList<User> modelUsers = FXCollections.observableArrayList();

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
