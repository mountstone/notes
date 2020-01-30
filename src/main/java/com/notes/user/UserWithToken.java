package com.notes.user;

import com.notes.model.User;
import lombok.Getter;

@Getter
public class UserWithToken {

    private String id;

    private String username;

    private String token;

    public UserWithToken(User user, String token){
        this.id=user.getId();
        this.username=user.getUsername();
        this.token=token;
    }
}
