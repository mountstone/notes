package com.notes.user;

import com.notes.model.User;
import lombok.Getter;

@Getter
public class UserData {
    private String id;
    private String username;

    public UserData(User user){
        this.id=user.getId();
        this.username=user.getUsername();
    }

    public String getUsername() {
        return username;
    }

    public String getId() {
        return id;
    }
}
