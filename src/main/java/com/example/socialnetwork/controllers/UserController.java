package com.example.socialnetwork.controllers;

import com.example.socialnetwork.customExceptions.RepoException;
import com.example.socialnetwork.domain.DTOs.FriendshipDTO;
import com.example.socialnetwork.domain.Friendship;
import com.example.socialnetwork.domain.Message;
import com.example.socialnetwork.domain.User;
import com.example.socialnetwork.services.UserService;
import com.example.socialnetwork.utils.observer.Observer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
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
    private ObservableList<User> modelOnlyFriends = FXCollections.observableArrayList();

    // friends table
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

    // friend requests table
    @FXML
    private TableView<FriendshipDTO> tableViewFriendRequests;
    @FXML
    private TableColumn<FriendshipDTO, String> tableColumnRequestFirstName;
    @FXML
    private TableColumn<FriendshipDTO, String> tableColumnRequestLastName;
    @FXML
    private TableColumn<FriendshipDTO, String> tableColumnRequestSentAt;

    // users tab
    @FXML
    private TableView<User> tableViewUsers;
    @FXML
    private TableColumn<User, String> tableColumnUsersFirstName;
    @FXML
    private TableColumn<User, String> tableColumnUsersLastName;
    @FXML
    private TextField textFieldSearchUser;

    // messages tab
    @FXML
    private ListView listViewFriends;
    @FXML
    private VBox messagesVbox;
    @FXML
    private ScrollPane messagesSp;
    @FXML
    private TextField textFieldMessage;
    @FXML
    private Label labelNamesOfSelectedFriend;
    @FXML
    private HBox messageHBox;

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

        // resetting the list will deselect user, so we have to reselect them

        User selectedFriend = (User) listViewFriends.getSelectionModel().getSelectedItem();
        modelOnlyFriends.setAll(srv.getFriends(user));

        if(!modelOnlyFriends.contains(selectedFriend)){
            hideConversation();
        }
        else if(selectedFriend != null) { //setting conversation title to names of first user in list if not empty
            listViewFriends.getSelectionModel().select(selectedFriend);
            //labelNamesOfSelectedFriend.setText(listViewFriends.getSelectionModel().getSelectedItem().toString());
        }

        //loadMessagesOnScreen();

//        int friendRequestCount = networkService.getFriendRequests(user).size();
//        labelFriendRequestCount.setText("You have " + friendRequestCount + " friend request(s)!");
    }

    private void hideConversation(){
        messageHBox.setVisible(false);
        messagesVbox.getChildren().clear();
        labelNamesOfSelectedFriend.setText("");
    }

    private void initializeFriendRequestTable(){
        tableColumnFriendsFirstName.setCellValueFactory(new PropertyValueFactory<FriendshipDTO, String>("friendFirstName"));
        tableColumnFriendsLastName.setCellValueFactory(new PropertyValueFactory<FriendshipDTO, String>("friendLastName"));
        tableColumnFriendsSince.setCellValueFactory(new PropertyValueFactory<FriendshipDTO, String>("friendshipSince"));
        tableColumnFriendsStatus.setCellValueFactory(new PropertyValueFactory<FriendshipDTO, String>("status"));

        tableViewFriends.setItems(modelFriendships);
    }

    private void initializeFriendshipsTable(){
        tableColumnRequestFirstName.setCellValueFactory(new PropertyValueFactory<FriendshipDTO, String>("friendFirstName"));
        tableColumnRequestLastName.setCellValueFactory(new PropertyValueFactory<FriendshipDTO, String>("friendLastName"));
        tableColumnRequestSentAt.setCellValueFactory(new PropertyValueFactory<FriendshipDTO, String>("friendshipSince"));

        tableViewFriendRequests.setItems(modelFriendRequests);
    }

    private void initializeUsersTable(){
        tableColumnUsersFirstName.setCellValueFactory(new PropertyValueFactory<User, String>("firstName"));
        tableColumnUsersLastName.setCellValueFactory(new PropertyValueFactory<User, String>("lastName"));

        tableViewUsers.setItems(modelUsers);

        textFieldSearchUser.textProperty().addListener(observable -> findByNames());
    }

    private void initializeMessagesTab(){
        listViewFriends.setItems(modelOnlyFriends);

        messageHBox.setVisible(false);

        listViewFriends.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                labelNamesOfSelectedFriend.setText(newValue.toString());
                messageHBox.setVisible(true);
                loadMessagesOnScreen();
            }
        });

        // automatically scrolling down in the messages scroll pane when new message is received
        messagesVbox.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                messagesSp.setVvalue((Double) newValue);
            }
        });
    }

    @FXML
    public void initialize() {
        initializeFriendRequestTable();
        initializeFriendshipsTable();
        initializeUsersTable();
        initializeMessagesTab();
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

    private void loadMessagesOnScreen() {

        User friend = (User) listViewFriends.getSelectionModel().getSelectedItem();
        messagesVbox.getChildren().clear();

        if(friend != null){
            List<Message> msgs = srv.getMessages(user, friend);


            for(Message m : msgs){

                HBox messageBox = new HBox();
                messageBox.setPadding(new Insets(5, 5, 5, 10));

                // used to split message on multiple lines if it is too big
                Text text = new Text(m.getContent());
                TextFlow messageWrapping = new TextFlow(text);

                if(user.getId() == m.getSender().getId()){ // current logged-in user is the sender of this message
                    messageBox.setAlignment(Pos.CENTER_RIGHT);

                    // styling for wrapper
                    messageWrapping.setStyle(
                            "-fx-color: rgb(239, 242, 255);" +
                                    "-fx-background-color: rgb(15, 125, 242);" +
                                    "-fx-background-radius: 20px"
                    );
                    text.setFill(Color.color(0.934, 0.945, 0.996));
                }else {
                    messageBox.setAlignment(Pos.CENTER_LEFT);

                    // styling for wrapper
                    messageWrapping.setStyle(
                            "-fx-color: rgb(239, 242, 255);" +
                                    "-fx-background-color: rgb(233, 233, 235);" +
                                    "-fx-background-radius: 20px"
                    );
                }

                messageWrapping.setPadding(new Insets(5, 10, 5, 10));

                // add the message to the scroll pane
                messageBox.getChildren().add(messageWrapping);
                messagesVbox.getChildren().add(messageBox);
            }
        }
    }

    @FXML
    public void sendMessage(ActionEvent actionEvent) {

        String content = textFieldMessage.getText();
        User friend = (User) listViewFriends.getSelectionModel().getSelectedItem();

        if(!content.isEmpty()){
            srv.addMessageToDb(user, friend, content);
            textFieldMessage.setText("");
        }

    }
}
