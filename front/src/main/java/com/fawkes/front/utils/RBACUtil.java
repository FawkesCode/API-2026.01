package com.fawkes.front.utils;

import com.fawkes.front.service.UserInfoManager;

/**
 * Utility class for Role-Based Access Control (RBAC) checks
 */
public class RBACUtil {
    
    public enum Role {
        DIRECTOR, MANAGER, OPERATIONAL
    }
    
    /**
     * Check if current user has a specific role
     */
    public static boolean hasRole(Role role) {
        String userRole = UserInfoManager.getInstance().getUserRole();
        return userRole != null && userRole.equalsIgnoreCase(role.name());
    }
    
    /**
     * Check if current user has ANY of the specified roles
     */
    public static boolean hasAnyRole(Role... roles) {
        String userRole = UserInfoManager.getInstance().getUserRole();
        if (userRole == null) return false;
        
        for (Role role : roles) {
            if (userRole.equalsIgnoreCase(role.name())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Check if current user is DIRECTOR
     */
    public static boolean isDirector() {
        return hasRole(Role.DIRECTOR);
    }
    
    /**
     * Check if current user is MANAGER or higher
     */
    public static boolean isManager() {
        return hasAnyRole(Role.MANAGER, Role.DIRECTOR);
    }
    
    /**
     * Check if current user is OPERATIONAL
     */
    public static boolean isOperational() {
        return hasRole(Role.OPERATIONAL);
    }
    
    /**
     * Check if current user can manage employees (DIRECTOR or MANAGER only)
     */
    public static boolean canManageEmployees() {
        return hasAnyRole(Role.DIRECTOR, Role.MANAGER);
    }
    
    /**
     * Check if current user can manage suppliers (DIRECTOR or MANAGER only)
     */
    public static boolean canManageSuppliers() {
        return hasAnyRole(Role.DIRECTOR, Role.MANAGER);
    }
    
    /**
     * Check if current user can manage products (DIRECTOR or MANAGER only)
     */
    public static boolean canManageProducts() {
        return hasAnyRole(Role.DIRECTOR, Role.MANAGER);
    }
    
    /**
     * Check if current user can view stock (all roles)
     */
    public static boolean canViewStock() {
        return UserInfoManager.getInstance().getUserRole() != null;
    }
    
    /**
     * Check if current user can register output (all roles)
     */
    public static boolean canRegisterOutput() {
        return UserInfoManager.getInstance().getUserRole() != null;
    }
}
