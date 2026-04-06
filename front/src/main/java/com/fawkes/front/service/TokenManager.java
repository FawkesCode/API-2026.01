package com.fawkes.front.service;

/**
 * Manages JWT token storage and retrieval for the front-end application.
 * Stores the token in memory for the duration of the session.
 */
public class TokenManager {
    private static TokenManager instance;
    private String jwtToken;

    private TokenManager() {
    }

    public static TokenManager getInstance() {
        if (instance == null) {
            instance = new TokenManager();
        }
        return instance;
    }

    /**
     * Stores the JWT token received from login endpoint
     */
    public void setToken(String token) {
        this.jwtToken = token;
    }

    /**
     * Retrieves the stored JWT token
     */
    public String getToken() {
        return this.jwtToken;
    }

    /**
     * Checks if a token is stored
     */
    public boolean hasToken() {
        return this.jwtToken != null && !this.jwtToken.isEmpty();
    }

    /**
     * Clears the token (logout)
     */
    public void clearToken() {
        this.jwtToken = null;
    }

    /**
     * Gets the Bearer token string for Authorization header
     */
    public String getBearerToken() {
        if (hasToken()) {
            return "Bearer " + jwtToken;
        }
        return null;
    }
}
