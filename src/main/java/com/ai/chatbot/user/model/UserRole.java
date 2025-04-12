package com.ai.chatbot.user.model;

public enum UserRole {
    ADMIN("admin"),
    MEMBER("member");

    private final String value;

    UserRole(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static UserRole fromAdminFlag(boolean isAdmin) {
        return isAdmin ? ADMIN : MEMBER;
    }
}