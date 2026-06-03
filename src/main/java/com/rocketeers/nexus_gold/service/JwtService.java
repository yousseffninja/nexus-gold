package com.rocketeers.nexus_gold.service;

import com.rocketeers.nexus_gold.model.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.HashMap;

public interface JwtService {

    String extractUserName(String token);

    String extractUserNameFromExpiredToken(String token);

    String generateToken(UserDetails userDetails);

    boolean isTokenValid(String token, UserDetails userDetails);

    String generateRefreshToken(HashMap<String, Object> objectObjectHashMap, UserDetails userDetails);
}
