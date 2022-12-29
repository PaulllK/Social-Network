package com.example.socialnetwork.domain;

import java.time.LocalDateTime;

public class Friendship extends Entity{

    private User sender, receiver;
    private LocalDateTime friendsSince;
    private boolean accepted = false;

    public Friendship(User sender, User receiver, LocalDateTime friendsSince) {
        this.sender = sender;
        this.receiver = receiver;
        this.friendsSince = friendsSince;
    }

    public Friendship(User sender, User receiver) {
        this(sender, receiver, LocalDateTime.now()); // calls above constructor
    }

    public User getSender() {
        return sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public LocalDateTime getFriendsSince() {
        return friendsSince;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }
}
