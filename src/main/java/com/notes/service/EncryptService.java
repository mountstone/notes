package com.notes.service;

public interface EncryptService {

    String encrypt(String password);

    boolean check(String checkPassword, String realPassword);
}
