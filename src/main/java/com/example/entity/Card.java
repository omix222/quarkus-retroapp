package com.example.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

/**
 * カードエンティティ（Keep/Problem/Try）
 */
@Entity
@Table(name = "cards")
public class Card extends PanacheEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    public CardType type;

    @Column(nullable = false, length = 1000)
    public String content;

    @Column(nullable = false)
    public Integer votes = 0;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "retrospective_id")
    public Retrospective retrospective;

    public Card() {
    }

    public Card(CardType type, String content) {
        this.type = type;
        this.content = content;
        this.votes = 0;
    }

    public void incrementVote() {
        this.votes++;
    }

    public void decrementVote() {
        if (this.votes > 0) {
            this.votes--;
        }
    }
}
