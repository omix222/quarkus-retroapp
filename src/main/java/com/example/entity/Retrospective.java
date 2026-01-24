package com.example.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * レトロスペクティブ（ふりかえり）エンティティ
 */
@Entity
@Table(name = "retrospectives")
public class Retrospective extends PanacheEntity {

    @Column(nullable = false)
    public String title;

    @Column(nullable = false)
    public LocalDate date;

    @Column(length = 1000)
    public String description;

    @OneToMany(mappedBy = "retrospective", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<Card> cards = new ArrayList<>();

    @OneToMany(mappedBy = "retrospective", cascade = CascadeType.ALL, orphanRemoval = true)
    public List<ActionItem> actionItems = new ArrayList<>();

    public Retrospective() {
    }

    public Retrospective(String title, LocalDate date) {
        this.title = title;
        this.date = date;
    }

    public void addCard(Card card) {
        cards.add(card);
        card.retrospective = this;
    }

    public void addActionItem(ActionItem actionItem) {
        actionItems.add(actionItem);
        actionItem.retrospective = this;
    }
}
