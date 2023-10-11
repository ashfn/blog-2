package com.ashfn.blog.tags.exception;

public class InvalidTagNameException extends Exception {
    public InvalidTagNameException(String errorMessage){
        super(errorMessage);
    }
}