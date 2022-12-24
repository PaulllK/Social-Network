package com.example.socialnetwork.validators;

import com.example.socialnetwork.customExceptions.ValidatorException;
import com.example.socialnetwork.domain.Friendship;

public class FriendshipValidator {
    public void validateFriendship(Friendship frnd) {
        if(frnd.getId1() == frnd.getId2())
            throw new ValidatorException("a user can't make friends with themselves!\n");
    }
}
