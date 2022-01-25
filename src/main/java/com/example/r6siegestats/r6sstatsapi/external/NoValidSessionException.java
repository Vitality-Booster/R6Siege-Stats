package com.example.r6siegestats.r6sstatsapi.external;

public class NoValidSessionException extends Exception {
    public NoValidSessionException(int numOfTriedSessions) {
        super("There was no session found that is valid. Tried with '" + numOfTriedSessions + "' different sessions");
    }
}
