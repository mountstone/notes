package com.notes.request;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@JsonRootName("user")
@NoArgsConstructor
public class LoginParam {

    @NotBlank(message = "can't be empty")
    private String username;

    @NotBlank(message = "can't be empty")
    private String password;

    public String getUsername(){
        return this.username;
    }

    public String getPassword(){
        return this.password;
    }
}
