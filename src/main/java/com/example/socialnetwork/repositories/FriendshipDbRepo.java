package com.example.socialnetwork.repositories;

import com.example.socialnetwork.domain.Friendship;

import java.sql.*;

public class FriendshipDbRepo implements Repository<Friendship>{
    private String url;
    private String userName;
    private String password;
    private Connection conn;

    public FriendshipDbRepo(String url, String userName, String password) {
        this.url = url;
        this.userName = userName;
        this.password = password;
        try {
            conn = DriverManager.getConnection(url, userName, password);
        }catch (SQLException e){
            e.printStackTrace();
        }
        loadFromDb();
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

    private void loadFromDb() {
        try {
            PreparedStatement statement = conn.prepareStatement("SELECT * FROM friendships");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                int id1 = resultSet.getInt("id1");
                int id2 = resultSet.getInt("id2");
                Timestamp friendsSince = resultSet.getTimestamp("friends_since");

                Friendship f = new Friendship(id1, id2, friendsSince.toLocalDateTime());
                f.setId(id);
                allFriendships.add(f);
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void add(Friendship friendship) {
        String sql = "INSERT INTO friendships (id1, id2, friends_since) VALUES (?, ?, ?)";
        try {
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            ps.setInt(1, friendship.getId1());
            ps.setInt(2, friendship.getId2());
            ps.setTimestamp(3, Timestamp.valueOf(friendship.getFriendsSince()));

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            // setting our user id in program memory list of users
            if(rs.next()) {
                int pk = rs.getInt("id");
                friendship.setId(pk);

                super.add(friendship);
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        super.delete(id);
        String sql = "DELETE FROM friendships WHERE id=?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, id);
            ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    @Override
    public void deleteWithId(int id) {
        super.deleteWithId(id);
        // TO DO: delete friendships that contain users with given id
        String sql = "DELETE FROM friendships WHERE id1=? OR id2=?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, id);
            ps.setInt(2, id);
            ps.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
