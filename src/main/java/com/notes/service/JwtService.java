package com.notes.service;

import com.notes.model.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface JwtService {
    String toToken(User user);

    Optional<String> getSubjectFromToken(String token);
}
