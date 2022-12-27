package com.example.socialnetwork.repositories;

import com.example.socialnetwork.customExceptions.RepoException;
import com.example.socialnetwork.domain.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDbRepo implements Repository<User> {

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

    @Override
    public void add(User user) {
        String sql = "INSERT INTO users (first_name, last_name, password) VALUES (?, ?, ?)";
        try {
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setString(3, user.getPassword());

//            ps.executeUpdate();
//            ResultSet rs = ps.getGeneratedKeys();
//            // setting our user id in program memory list of users
//            if(rs.next()) {
//                int pk = rs.getInt("id");
//                user.setId(pk);
//            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
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

    @Override
    public User find(User user) {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM users WHERE first_name=? AND last_name=? AND password=?");

            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setString(3, user.getPassword());

            ResultSet resultSet = ps.executeQuery();

            if (!resultSet.next()) {
                throw new RepoException("user or password incorrect!");
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
        try {
            PreparedStatement statement = conn.prepareStatement("SELECT * FROM users");
            ResultSet resultSet = statement.executeQuery();

            List<User> allUsers = new ArrayList<>();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String first_name = resultSet.getString("first_name");
                String last_name = resultSet.getString("last_name");
                String password = resultSet.getString("password");

                User user = new User(first_name, last_name, password);
                user.setId(id);
                allUsers.add(user);
            }

            return allUsers;
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }
}
