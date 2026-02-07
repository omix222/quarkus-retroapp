package com.example.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * アクションアイテムエンティティ
 */
@Entity
@Table(name = "action_items")
public class ActionItem extends PanacheEntity {

    @NotBlank(message = "説明は必須です")
    @Size(max = 1000, message = "説明は1000文字以内で入力してください")
    @Column(nullable = false, length = 1000)
    public String description;

    @Size(max = 100, message = "担当者名は100文字以内で入力してください")
    @Column(length = 100)
    public String assignee;

    /**
     * Jiraチケット番号（例: PROJECT-123, RETRO-1）
     * 形式: 大文字英字で始まり、大文字英数字が続き、ハイフンの後に数字
     */
    @Pattern(regexp = "^$|^[A-Z][A-Z0-9]*-[0-9]+$", message = "Jiraチケット番号の形式が不正です（例: PROJECT-123）")
    @Size(max = 50, message = "Jiraチケット番号は50文字以内で入力してください")
    @Column(name = "jira_ticket", length = 50)
    public String jiraTicket;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public ActionItemStatus status = ActionItemStatus.TODO;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "retrospective_id")
    public Retrospective retrospective;

    public ActionItem() {
    }

    public ActionItem(String description, String assignee) {
        this.description = description;
        this.assignee = assignee;
        this.status = ActionItemStatus.TODO;
    }

    public ActionItem(String description, String assignee, String jiraTicket) {
        this.description = description;
        this.assignee = assignee;
        this.jiraTicket = jiraTicket;
        this.status = ActionItemStatus.TODO;
    }

    public void updateStatus(ActionItemStatus newStatus) {
        this.status = newStatus;
    }
}
