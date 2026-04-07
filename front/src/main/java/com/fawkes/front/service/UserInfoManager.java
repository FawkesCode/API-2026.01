package com.fawkes.front.service;

/**
 * Singleton manager for storing user information after login
 */
public class UserInfoManager {
    private static UserInfoManager instance;
    
    private String userId;
    private String userName;
    private String userEmail;
    private String userRole;
    
    private UserInfoManager() {
    }
    
    public static synchronized UserInfoManager getInstance() {
        if (instance == null) {
            instance = new UserInfoManager();
        }
        return instance;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getUserName() {
        return userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    
    public String getUserEmail() {
        return userEmail;
    }
    
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
    
    public String getUserRole() {
        return userRole;
    }
    
    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }
    
    public void clearUserInfo() {
        this.userId = null;
        this.userName = null;
        this.userEmail = null;
        this.userRole = null;
    }
}
