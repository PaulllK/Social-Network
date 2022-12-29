package com.example.socialnetwork.repositories;

import com.example.socialnetwork.customExceptions.RepoException;
import com.example.socialnetwork.domain.DTOs.FriendshipDTO;
import com.example.socialnetwork.domain.Friendship;
import com.example.socialnetwork.domain.User;

import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FriendshipDbRepo{

    // friendship entity in database will have 5 fields:
    // id(int), sender_id(int), receiver_id(int), friends_since(timestamp) and accepted(boolean)

    private String url;
    private String userName;
    private String password;
    private Connection conn;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    public FriendshipDbRepo(String url, String userName, String password) {
        this.url = url;
        this.userName = userName;
        this.password = password;
        try {
            conn = DriverManager.getConnection(url, userName, password);
        }catch (SQLException e){
            e.printStackTrace();
        }
        //loadFromDb();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Connection getConn() {
        return conn;
    }

    public DateTimeFormatter getDateFormatter() {
        return dateFormatter;
    }

    //    private void loadFromDb() {
//        try {
//            PreparedStatement statement = conn.prepareStatement("SELECT * FROM friendships");
//            ResultSet resultSet = statement.executeQuery();
//
//            while (resultSet.next()) {
//                int id = resultSet.getInt("id");
//                int id1 = resultSet.getInt("id1");
//                int id2 = resultSet.getInt("id2");
//                Timestamp friendsSince = resultSet.getTimestamp("friends_since");
//
//                Friendship f = new Friendship(id1, id2, friendsSince.toLocalDateTime());
//                f.setId(id);
//            }
//        } catch (SQLException e){
//            e.printStackTrace();
//        }
//    }

    public void add(Friendship friendship) {
        String sql = "SELECT * FROM friendships WHERE sender_id=? AND receiver_id=? OR sender_id=? AND receiver_id=?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, friendship.getSender().getId());
            ps.setInt(2, friendship.getReceiver().getId());
            ps.setInt(3, friendship.getReceiver().getId());
            ps.setInt(4, friendship.getSender().getId());

            ResultSet results = ps.executeQuery();

            if(results.next()) {
                throw new RepoException("friend request/friend already exists");
            }

            String sqlInsert = "INSERT INTO friendships (sender_id, receiver_id, accepted) VALUES (?, ?, false)";
            PreparedStatement statement = conn.prepareStatement(sqlInsert);

            statement.setInt(1, friendship.getSender().getId());
            statement.setInt(2, friendship.getReceiver().getId());

            statement.executeQuery();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

//    public void delete(int id) {
//        String sql = "DELETE FROM friendships WHERE id=?";
//        try {
//            PreparedStatement ps = conn.prepareStatement(sql);
//
//            ps.setInt(1, id);
//            ps.executeUpdate();
//        }catch (SQLException e){
//            e.printStackTrace();
//        }
//    }

//    public void deleteWithId(int id) {
//        // TO DO: delete friendships that contain users with given id
//        String sql = "DELETE FROM friendships WHERE id1=? OR id2=?";
//        try {
//            PreparedStatement ps = conn.prepareStatement(sql);
//
//            ps.setInt(1, id);
//            ps.setInt(2, id);
//            ps.executeUpdate();
//        }catch (SQLException e){
//            e.printStackTrace();
//        }
//    }

    public List<FriendshipDTO> getFriendsAndSentRequests(User user) {

        List<FriendshipDTO> frnds = new ArrayList<>();

        try {
            String sql = "SELECT * FROM friendships WHERE accepted=true AND (sender_id=? OR receiver_id=?) OR accepted=false AND sender_id=?";
            PreparedStatement statement = conn.prepareStatement(sql);

            statement.setInt(1, user.getId());
            statement.setInt(2, user.getId());
            statement.setInt(3, user.getId());

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int sender_id = resultSet.getInt("sender_id");
                int receiver_id = resultSet.getInt("receiver_id");
                Timestamp friendsSince = resultSet.getTimestamp("friends_since");
                boolean accepted = resultSet.getBoolean("accepted");

                // search for friend's data
                int idToSearch;

                if(user.getId() == sender_id)
                    idToSearch = receiver_id;
                else
                    idToSearch = sender_id;

                String findFriendSql = "SELECT * FROM users WHERE id=?";
                PreparedStatement ps = conn.prepareStatement(findFriendSql);

                ps.setInt(1, idToSearch);

                ResultSet results = ps.executeQuery();

                // friend should be found, so friendship is created and added to the list
                if(results.next())
                {
                    int friendId = results.getInt("id");
                    String first_name = results.getString("first_name");
                    String last_name = results.getString("last_name");

                    String status = (accepted ? "accepted" : "pending");

                    FriendshipDTO f = new FriendshipDTO(friendId, first_name, last_name, friendsSince.toLocalDateTime().format(dateFormatter), status);

                    frnds.add(f);
                }
                else
                    throw new SQLException();
            }
        } catch (SQLException e){
            e.printStackTrace();
        }

        return frnds;
    }

    public List<FriendshipDTO> getReceivedFriendRequests(User user) {
        List<FriendshipDTO> frnds = new ArrayList<>();

        try {
            String sql = "SELECT * FROM friendships WHERE accepted=false AND receiver_id=?";
            PreparedStatement statement = conn.prepareStatement(sql);

            statement.setInt(1, user.getId());

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int sender_id = resultSet.getInt("sender_id");
                int receiver_id = resultSet.getInt("receiver_id");
                Timestamp friendsSince = resultSet.getTimestamp("friends_since");

                // search for friend's data
                int idToSearch;

                if(user.getId() == sender_id)
                    idToSearch = receiver_id;
                else
                    idToSearch = sender_id;


                String findFriendSql = "SELECT * FROM users WHERE id=?";
                PreparedStatement ps = conn.prepareStatement(findFriendSql);

                ps.setInt(1, idToSearch);

                ResultSet results = ps.executeQuery();

                // friend should be found, so friendship is created and added to the list
                if(results.next())
                {
                    int friendId = results.getInt("id");
                    String first_name = results.getString("first_name");
                    String last_name = results.getString("last_name");

                    FriendshipDTO f = new FriendshipDTO(friendId, first_name, last_name, friendsSince.toLocalDateTime().format(dateFormatter), "pending");

                    frnds.add(f);
                }
                else
                    throw new SQLException();
            }
        } catch (SQLException e){
            e.printStackTrace();
        }

        return frnds;
    }

    public void acceptFriendRequest(int friendId, int currentUserId) {
        // should change timestamp and accepted columns
        try {
            String sql = "UPDATE friendships\n" +
                         "SET friends_since=CURRENT_TIMESTAMP, accepted=true\n" +
                         "WHERE sender_id=? AND receiver_id=?;";

            PreparedStatement statement = conn.prepareStatement(sql);

            statement.setInt(1, friendId);
            statement.setInt(2, currentUserId);

            statement.executeQuery();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void deleteFriendRequest(int friendId, int currentUserId) {
        // deletes friendship (friend request)
        try {
            PreparedStatement statement = conn.prepareStatement("DELETE FROM friendships WHERE sender_id=? AND receiver_id=?");

            statement.setInt(1, friendId);
            statement.setInt(2, currentUserId);

            statement.executeQuery();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void deleteFriendOrRequest(int currentUserId, int friendId) {
        try {
            String sql = "DELETE FROM friendships WHERE accepted=true AND (sender_id=? AND receiver_id=? OR sender_id=? AND receiver_id=?) OR accepted=false AND sender_id=? AND receiver_id=?";
            PreparedStatement statement = conn.prepareStatement(sql);

            statement.setInt(1, currentUserId);
            statement.setInt(2, friendId);
            statement.setInt(3, friendId);
            statement.setInt(4, currentUserId);
            statement.setInt(5, currentUserId);
            statement.setInt(6, friendId);

            statement.executeQuery();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
}
