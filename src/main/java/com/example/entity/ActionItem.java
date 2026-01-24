package com.example.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

/**
 * アクションアイテムエンティティ
 */
@Entity
@Table(name = "action_items")
public class ActionItem extends PanacheEntity {

    @Column(nullable = false, length = 1000)
    public String description;

    @Column
    public String assignee;

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

    public void updateStatus(ActionItemStatus newStatus) {
        this.status = newStatus;
    }
}
