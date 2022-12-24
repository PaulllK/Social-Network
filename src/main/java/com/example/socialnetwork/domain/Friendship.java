package com.example.socialnetwork.domain;

import java.time.LocalDateTime;

public class Friendship extends Entity{

    private int id1, id2;
    private LocalDateTime friendsSince;

    public Friendship(int id1, int id2, LocalDateTime date) {
        this.id1 = id1;
        this.id2 = id2;
        friendsSince = date;
    }

    public Friendship(int id1, int id2) {
        this(id1, id2, LocalDateTime.now()); // calls above constructor
    }

    public int getId1() {
        return id1;
    }

    public int getId2() {
        return id2;
    }

    public LocalDateTime getFriendsSince() {
        return friendsSince;
    }
}
