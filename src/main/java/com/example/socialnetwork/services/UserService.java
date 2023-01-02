package com.example.socialnetwork.services;

import com.example.socialnetwork.domain.DTOs.FriendshipDTO;
import com.example.socialnetwork.domain.Friendship;
import com.example.socialnetwork.domain.Message;
import com.example.socialnetwork.domain.User;
import com.example.socialnetwork.repositories.FriendshipDbRepo;
import com.example.socialnetwork.repositories.MessageDbRepo;
import com.example.socialnetwork.repositories.UserDbRepo;
import com.example.socialnetwork.validators.FriendshipValidator;
import com.example.socialnetwork.validators.UserValidator;
import com.example.socialnetwork.utils.observer.Observable;

import java.util.*;

public class UserService extends Observable{
    private UserDbRepo userRepo;
    private FriendshipDbRepo frndRepo;
    private MessageDbRepo msgRepo;
    private UserValidator val;
    private FriendshipValidator fVal;

    public UserService(UserDbRepo userRepo, FriendshipDbRepo frndRepo, MessageDbRepo msgRepo, UserValidator val, FriendshipValidator fVal) {
        this.userRepo = userRepo;
        this.frndRepo = frndRepo;
        this.msgRepo = msgRepo;
        this.val = val;
        this.fVal = fVal;
    }

    public UserDbRepo getUserRepo() {
        return userRepo;
    }

    public FriendshipDbRepo getFrndRepo() {
        return frndRepo;
    }

    public UserValidator getVal() {
        return val;
    }

    public FriendshipValidator getfVal() {
        return fVal;
    }

    public void addTheUser(String firstName, String lastName, String password) {
        User user = new User(firstName, lastName, password);
        val.validateUser(user);
        userRepo.add(user);
    }

//    private void dfs(List<Integer> viz, int vf, Map<Integer, List<Integer>> network) {
//        viz.add(vf);
//
//        List<Integer> frIds = network.get(vf);
//
//        for(int i = 0; i < frIds.size(); i++)
//            if (!viz.contains(frIds.get(i)))
//                dfs(viz, frIds.get(i), network);
//    }

//    public int communities() {
//        List<User> users = userRepo.getAll();
//        int size = users.size();
//
//        // no users means 0 communities
//        if(size == 0)
//            return 0;
//
//        // we will use neighbours (friends' id's) lists for each node (user)
//        // ex. 2: 5 7 4
//        //     5: 2 8
//        //     7: 2
//        //     4: 2 3
//        //     3: 4
//        Map<Integer, List<Integer>> network = new HashMap<Integer, List<Integer>>();
//
//        // building friends' id's list for every user, in the hash map
//        users.forEach(u -> {
//            int i = 0, id = u.getId();
//            List<Friendship> friendships = frndRepo.getAll();
//
//            network.put(id, new ArrayList<Integer>()); // every user has its friends list (empty or not)
//
//            while(i < friendships.size()) {
//
//                int id1 = friendships.get(i).getId1(), id2 = friendships.get(i).getId2();
//
//                if (id1 == u.getId()) {
//                    // add id2
//                    network.get(id).add(id2);
//                }
//                else if(id2 == u.getId()) {
//                    // add id1
//                    network.get(id).add(id1);
//                }
//
//                i++;
//            }
//        });
//
//        List<Integer> viz = new ArrayList<Integer>(); // list of all visited nodes (users)
//
//        // visit all graph vertices
//        int nr = 0;
//
//        for(User u : users) {
//            if (!viz.contains(u.getId())) {
//                nr++;
//                dfs(viz, u.getId(), network);
//            }
//        }
//
//        return nr;
//
//    }

    public User findUserByData(String firstName, String lastName, String password) {
        // validate provided data before doing database search
        User u = new User(firstName, lastName, password);
        val.validateUser(u);

        return userRepo.find(u);
    }

    public List<FriendshipDTO> getFriendsAndSentRequests(User user) {
        return frndRepo.getFriendsAndSentRequests(user);
    }

    public List<FriendshipDTO> getReceivedFriendRequests(User user) {
        return frndRepo.getReceivedFriendRequests(user);
    }

    public void acceptFriendRequest(int friendId, int currentUserId) {
        frndRepo.acceptFriendRequest(friendId, currentUserId);
        notifyAllObservers();
    }

    public void rejectFriendRequest(int friendId, int currentUserId) {
        frndRepo.deleteFriendRequest(friendId, currentUserId);
        notifyAllObservers();
    }

    public void removeFriendOrRequest(int currentUserId, int friendId) {
        frndRepo.deleteFriendOrRequest(currentUserId, friendId);
        notifyAllObservers();
    }

    public List<User> getAllUsers() {
        return userRepo.getAllUsers();
    }

    public void sendFriendRequest(User currentUser, User userToBecomeFriend) {
        Friendship f = new Friendship(currentUser, userToBecomeFriend);
        frndRepo.add(f);
        notifyAllObservers();
    }

    public void registerUser(String firstName, String lastName, String password) {
        User u = new User(firstName, lastName, password);
        val.validateUser(u);
        userRepo.add(u);
        notifyAllObservers();
    }

    public List<User> getFriends(User user) {
        return frndRepo.getFriends(user);
    }

    public List<Message> getMessages(User user, User friend) {
        return msgRepo.getMessagesOfUsers(user, friend);
    }

    public void addMessageToDb(User sender, User receiver, String content) {
        Message m = new Message(sender, receiver, content);
        msgRepo.add(m);
        notifyAllObservers();
    }
}
