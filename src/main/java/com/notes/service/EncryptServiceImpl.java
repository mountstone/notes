package com.notes.service;

import org.springframework.stereotype.Service;

@Service
public class EncryptServiceImpl implements EncryptService {

    @Override
    public String encrypt(String password){
        return password;
    }

    @Override
    public boolean check(String checkPassword, String realPassword){

        return checkPassword.equals(realPassword);
    }

}
