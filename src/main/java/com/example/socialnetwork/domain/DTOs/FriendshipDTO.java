package com.example.socialnetwork.domain.DTOs;

public class FriendshipDTO {

    private int friendId;
    private String friendFirstName;
    private String friendLastName;
    private String friendshipSince;
    private String status;

    public FriendshipDTO(int friendId, String friendFirstName, String friendLastName, String friendshipSince, String status) {
        this.friendId = friendId;
        this.friendFirstName = friendFirstName;
        this.friendLastName = friendLastName;
        this.friendshipSince = friendshipSince;
        this.status = status;
    }

    public int getFriendId() {
        return friendId;
    }

    public String getFriendFirstName() {
        return friendFirstName;
    }

    public void setFriendFirstName(String friendFirstName) {
        this.friendFirstName = friendFirstName;
    }

    public String getFriendLastName() {
        return friendLastName;
    }

    public void setFriendLastName(String friendLastName) {
        this.friendLastName = friendLastName;
    }

    public String getFriendshipSince() {
        return friendshipSince;
    }

    public void setFriendshipSince(String friendshipSince) {
        this.friendshipSince = friendshipSince;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
