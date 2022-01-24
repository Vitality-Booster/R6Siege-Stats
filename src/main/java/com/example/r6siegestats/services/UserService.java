package com.example.r6siegestats.services;

import com.example.r6siegestats.models.UserSignInRequest;
import com.example.r6siegestats.r6sstatsapi.external.UbiApiCommunicator;
import com.example.r6siegestats.r6sstatsapi.session.UbiApiSessionManager;
import com.example.r6siegestats.services.interaces.IUserService;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class UserService implements IUserService {
    private final UbiApiSessionManager sessionManager;

    public UserService(UbiApiSessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public CompletableFuture<Void> signInUser(UserSignInRequest model) throws Exception{
        sessionManager.addSession(model.getEmail(), model.getPassword());
        sessionManager.storeSessions();

        return CompletableFuture.runAsync(() -> {});
    }
}
