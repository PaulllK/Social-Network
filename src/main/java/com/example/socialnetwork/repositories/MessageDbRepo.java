package com.example.socialnetwork.repositories;

import com.example.socialnetwork.customExceptions.RepoException;
import com.example.socialnetwork.domain.DTOs.FriendshipDTO;
import com.example.socialnetwork.domain.Message;
import com.example.socialnetwork.domain.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MessageDbRepo extends RepositoryDb<Message>{

    public MessageDbRepo(String url, String userName, String password) {
        super(url, userName, password);
        //loadFromDB()
    }

    @Override
    public void add(Message msg) {
        String sql = "INSERT INTO messages (sender_id, receiver_id, content) VALUES (?, ?, ?)";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, msg.getSender().getId());
            ps.setInt(2, msg.getReceiver().getId());
            ps.setString(3, msg.getContent());

            ResultSet results = ps.executeQuery();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public List<Message> getMessagesOfUsers(User user, User friend) {

        List<Message> msgs = new ArrayList<>();

        try {
            String sql = "SELECT * FROM messages WHERE sender_id=? AND receiver_id=? OR sender_id=? AND receiver_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, user.getId());
            ps.setInt(2, friend.getId());
            ps.setInt(3, friend.getId());
            ps.setInt(4, user.getId());

            ResultSet results = ps.executeQuery();

            while(results.next()) {
                int id = results.getInt("id");
                int senderId = results.getInt("sender_id");
                //int receiverId = results.getInt("receiver_id");
                String content = results.getString("content");

                Message m;

                if(user.getId() == senderId)
                    m = new Message(user, friend, content);
                else
                    m = new Message(friend, user, content);

                m.setId(id);

                msgs.add(m);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

        return msgs;
    }
}
