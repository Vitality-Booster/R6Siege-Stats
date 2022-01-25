package com.example.r6siegestats.services.interaces;

import com.example.r6siegestats.models.UserSignInRequest;

import java.util.concurrent.CompletableFuture;

public interface IUserService {
    CompletableFuture<Void> signInUser(UserSignInRequest model) throws Exception;
}
