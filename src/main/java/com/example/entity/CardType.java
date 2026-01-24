package com.example.entity;

/**
 * カードの種類を表すEnum
 */
public enum CardType {
    KEEP("良かったこと"),
    PROBLEM("問題・課題"),
    TRY("試したいこと");

    private final String displayName;

    CardType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
