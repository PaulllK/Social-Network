package com.example.socialnetwork.repositories;

import com.example.socialnetwork.customExceptions.RepoException;
import com.example.socialnetwork.domain.User;

import java.util.ArrayList;
import java.util.List;

public class UserRepo implements Repository<User>{

    protected List<User> allUsers = new ArrayList<>();

    private int userPos(int id) {
        for(int i = 0; i < allUsers.size(); i++)
            if(allUsers.get(i).getId() == id)
                return i;

        return -1;
    }

    public boolean userExists(int id) {
        if(userPos(id) == -1)
            return false;

        return true;
    }

    public List<User> getAllUsers() {
        return allUsers;
    }

    @Override
    public void add(User user) {

        if(userPos(user.getId()) != -1)
            throw new RepoException("user exists already (same ID) !\n");

        allUsers.add(user);
    }

    @Override
    public User find(int id) {
        int pos = userPos(id);

        if(pos == -1)
            throw new RepoException("user non-existent!\n");

        return allUsers.get(pos);
    }

    @Override
    public void delete(int id) {
        int pos = userPos(id);

        if(pos == -1)
            throw new RepoException("user non-existent!\n");

        allUsers.remove(pos);
    }
}
