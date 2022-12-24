package com.example.socialnetwork.customExceptions;

public class ValidatorException extends RuntimeException{
    public ValidatorException(String msg) {
        super(msg);
    }
}
