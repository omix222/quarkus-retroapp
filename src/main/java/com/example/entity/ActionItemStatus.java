package com.example.entity;

/**
 * アクションアイテムのステータスを表すEnum
 */
public enum ActionItemStatus {
    TODO("未着手"),
    IN_PROGRESS("進行中"),
    DONE("完了");

    private final String displayName;

    ActionItemStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
