package com.example.socialnetwork.controllers;

import com.example.socialnetwork.customExceptions.RepoException;
import com.example.socialnetwork.domain.DTOs.FriendshipDTO;
import com.example.socialnetwork.domain.Friendship;
import com.example.socialnetwork.domain.User;
import com.example.socialnetwork.services.UserService;
import com.example.socialnetwork.utils.observer.Observer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.awt.Toolkit;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class UserController implements Observer
{
    private UserService srv;
    private User user;
    private ObservableList<FriendshipDTO> modelFriendships = FXCollections.observableArrayList();
    private ObservableList<FriendshipDTO> modelFriendRequests = FXCollections.observableArrayList();
    private ObservableList<User> modelUsers = FXCollections.observableArrayList();

    @FXML
    private TableView<FriendshipDTO> tableViewFriends;
    @FXML
    private TableColumn<FriendshipDTO, String> tableColumnFriendsFirstName;
    @FXML
    private TableColumn<FriendshipDTO, String> tableColumnFriendsLastName;
    @FXML
    private TableColumn<FriendshipDTO, String> tableColumnFriendsSince;
    @FXML
    private TableColumn<FriendshipDTO, String> tableColumnFriendsStatus;

    @FXML
    private TableView<FriendshipDTO> tableViewFriendRequests;
    @FXML
    private TableColumn<FriendshipDTO, String> tableColumnRequestFirstName;
    @FXML
    private TableColumn<FriendshipDTO, String> tableColumnRequestLastName;
    @FXML
    private TableColumn<FriendshipDTO, String> tableColumnRequestSentAt;

    @FXML
    private TableView<User> tableViewUsers;
    @FXML
    private TableColumn<User, String> tableColumnUsersFirstName;
    @FXML
    private TableColumn<User, String> tableColumnUsersLastName;

    @FXML
    private TextField textFieldSearchUser;

    public void setData(UserService srv, User user)
    {
        this.srv = srv;
        this.user = user;
        srv.addObserver(this);
        initModel();
    }

    @Override
    public void update() {
        initModel();
    }

    @FXML
    private void initModel() {
        modelFriendships.setAll(srv.getFriendsAndSentRequests(user));

        modelFriendRequests.setAll(srv.getReceivedFriendRequests(user));

        // all users except the one logged in
        modelUsers.setAll(srv.getAllUsers().stream().filter(u -> !u.equals(user)).collect(Collectors.toList()));

//        int friendRequestCount = networkService.getFriendRequests(user).size();
//        labelFriendRequestCount.setText("You have " + friendRequestCount + " friend request(s)!");
    }

    @FXML
    public void initialize() {
        tableColumnFriendsFirstName.setCellValueFactory(new PropertyValueFactory<FriendshipDTO, String>("friendFirstName"));
        tableColumnFriendsLastName.setCellValueFactory(new PropertyValueFactory<FriendshipDTO, String>("friendLastName"));
        tableColumnFriendsSince.setCellValueFactory(new PropertyValueFactory<FriendshipDTO, String>("friendshipSince"));
        tableColumnFriendsStatus.setCellValueFactory(new PropertyValueFactory<FriendshipDTO, String>("status"));

        tableViewFriends.setItems(modelFriendships);

        tableColumnRequestFirstName.setCellValueFactory(new PropertyValueFactory<FriendshipDTO, String>("friendFirstName"));
        tableColumnRequestLastName.setCellValueFactory(new PropertyValueFactory<FriendshipDTO, String>("friendLastName"));
        tableColumnRequestSentAt.setCellValueFactory(new PropertyValueFactory<FriendshipDTO, String>("friendshipSince"));

        tableViewFriendRequests.setItems(modelFriendRequests);

        tableColumnUsersFirstName.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        tableColumnUsersLastName.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));

        tableViewUsers.setItems(modelUsers);

        textFieldSearchUser.textProperty().addListener(observable -> findByNames());
    }

    @FXML
    private void findByNames() {
        Predicate<User> byNames = u -> (
                u.getFirstName().startsWith(textFieldSearchUser.getText()) ||
                u.getLastName().startsWith(textFieldSearchUser.getText()) ||
                (u.getFirstName() + " " + u.getLastName()).startsWith(textFieldSearchUser.getText()) ||
                (u.getLastName() + " " + u.getFirstName()).startsWith(textFieldSearchUser.getText())
        );

        // get all users except the one logged in
        List<User> users = srv.getAllUsers();
        users = users.stream().filter(u -> !u.equals(user)).collect(Collectors.toList());

        modelUsers.setAll(users.stream().filter(byNames).collect(Collectors.toList()));
    }

    @FXML
    public void acceptFriendRequest(ActionEvent actionEvent) {

        FriendshipDTO f = tableViewFriendRequests.getSelectionModel().getSelectedItem();

        if(f == null)
        {
            Toolkit.getDefaultToolkit().beep(); // error sound
            PopUpMessage.showErrorMessage("no user selected");
        }
        else
        {
            srv.acceptFriendRequest(f.getFriendId(), user.getId());
            PopUpMessage.showInformationMessage("friend request accepted!");
        }
    }

    @FXML
    public void rejectFriendRequest(ActionEvent actionEvent) {
        FriendshipDTO f = tableViewFriendRequests.getSelectionModel().getSelectedItem();

        if(f == null)
        {
            Toolkit.getDefaultToolkit().beep(); // error sound
            PopUpMessage.showErrorMessage("no user selected");
        }
        else
        {
            srv.rejectFriendRequest(f.getFriendId(), user.getId());
            PopUpMessage.showInformationMessage("friend request rejected!");
        }
    }

    @FXML
    public void removeFriendOrRequest(ActionEvent actionEvent) {
        FriendshipDTO f = tableViewFriends.getSelectionModel().getSelectedItem();

        if(f == null)
        {
            Toolkit.getDefaultToolkit().beep(); // error sound
            PopUpMessage.showErrorMessage("no user selected");
        }
        else
        {
            srv.removeFriendOrRequest(user.getId(), f.getFriendId());
            PopUpMessage.showInformationMessage("friend/request removed");
        }
    }

    @FXML
    public void sendFriendRequest(ActionEvent actionEvent) {
        User u = tableViewUsers.getSelectionModel().getSelectedItem();

        if(u == null)
        {
            Toolkit.getDefaultToolkit().beep(); // error sound
            PopUpMessage.showErrorMessage("no user selected");
        }
        else
        {
            try
            {
                srv.sendFriendRequest(user, u);
                PopUpMessage.showInformationMessage("friend request sent");
            }
            catch (RepoException e){
                PopUpMessage.showErrorMessage(e.getMessage());
            }
        }
    }
}
