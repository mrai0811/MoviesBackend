package com.rai.movieapi.exception;

public class MovieNotFoundException extends RuntimeException{
    public MovieNotFoundException(String message){
        super(message);
    }
}
