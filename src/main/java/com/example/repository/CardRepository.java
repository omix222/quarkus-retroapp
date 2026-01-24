package com.example.repository;

import com.example.entity.Card;
import com.example.entity.CardType;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

/**
 * カードリポジトリ
 */
@ApplicationScoped
public class CardRepository implements PanacheRepository<Card> {

    /**
     * レトロスペクティブIDでカードを検索
     */
    public List<Card> findByRetrospectiveId(Long retrospectiveId) {
        return list("retrospective.id", retrospectiveId);
    }

    /**
     * カードタイプでカードを検索
     */
    public List<Card> findByType(CardType type) {
        return list("type", type);
    }

    /**
     * レトロスペクティブIDとカードタイプでカードを検索
     */
    public List<Card> findByRetrospectiveIdAndType(Long retrospectiveId, CardType type) {
        return list("retrospective.id = ?1 and type = ?2", retrospectiveId, type);
    }

    /**
     * 投票数の多い順にカードを取得
     */
    public List<Card> findTopVoted(Long retrospectiveId, int limit) {
        return find("retrospective.id = ?1 ORDER BY votes DESC", retrospectiveId)
                .page(0, limit)
                .list();
    }
}
