package com.example.socialnetwork.validators;

import com.example.socialnetwork.customExceptions.ValidatorException;
import com.example.socialnetwork.domain.User;

public class UserValidator {
    public void validateUser(User user) {
        String errMsg = "";

        if(user.getFirstName() == null || "".equals(user.getFirstName()))
            errMsg += "first name can't be empty!\n";

        if(user.getLastName() == null || "".equals(user.getLastName()))
            errMsg += "last name can't be empty!\n";

        if(user.getPassword() == null || "".equals(user.getPassword()))
            errMsg += "password can't be empty!\n";

        if(errMsg != "")
            throw new ValidatorException(errMsg);
    }
}
