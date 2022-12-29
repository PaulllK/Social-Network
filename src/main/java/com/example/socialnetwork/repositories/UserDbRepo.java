package com.example.socialnetwork.repositories;

import com.example.socialnetwork.customExceptions.RepoException;
import com.example.socialnetwork.domain.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDbRepo {

    private String url;
    private String userName;
    private String password;
    private Connection conn;

    public UserDbRepo(String url, String userName, String password) {
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

//    private void loadFromDb() {
//        try {
//            PreparedStatement statement = conn.prepareStatement("SELECT * FROM users");
//            ResultSet resultSet = statement.executeQuery();
//
//            while (resultSet.next()) {
//                int id = resultSet.getInt("id");
//                String first_name = resultSet.getString("first_name");
//                String last_name = resultSet.getString("last_name");
//                String password = resultSet.getString("password");
//
//                User user = new User(first_name, last_name, password);
//                user.setId(id);
//                allUsers.add(user);
//            }
//        } catch (SQLException e){
//            e.printStackTrace();
//        }
//    }
    public void add(User user) {
        String sql = "SELECT * FROM users WHERE first_name=? AND last_name=?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());

            ResultSet rs = ps.executeQuery();

            if(rs.next()) {
                throw new RepoException("this user already has an account");
            }

            String sqlAdd = "INSERT INTO users (first_name, last_name, password) VALUES (?, ?, ?)";

            PreparedStatement statement = conn.prepareStatement(sqlAdd);

            statement.setString(1, user.getFirstName());
            statement.setString(2, user.getLastName());
            statement.setString(3, user.getPassword());

            statement.executeQuery();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM users WHERE id=?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, id);
            ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public User find(User user) {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE first_name=? AND last_name=? AND password=?");

            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setString(3, user.getPassword());

            ResultSet resultSet = ps.executeQuery();

            if (!resultSet.next()) {
                throw new RepoException("user is nonexistent or password is incorrect!");
            }

            int id = resultSet.getInt("id");
            user.setId(id);

            return user;
        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    public List<User> getAllUsers() {

        List<User> allUsers = new ArrayList<>();

        try {
            PreparedStatement statement = conn.prepareStatement("SELECT * FROM users");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String first_name = resultSet.getString("first_name");
                String last_name = resultSet.getString("last_name");
                String password = resultSet.getString("password");

                User user = new User(first_name, last_name, password);
                user.setId(id);
                allUsers.add(user);
            }

        } catch (SQLException e){
            e.printStackTrace();
        }

        return allUsers;

    }
}
