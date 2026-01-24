package com.example.repository;

import com.example.entity.ActionItem;
import com.example.entity.ActionItemStatus;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

/**
 * アクションアイテムリポジトリ
 */
@ApplicationScoped
public class ActionItemRepository implements PanacheRepository<ActionItem> {

    /**
     * レトロスペクティブIDでアクションアイテムを検索
     */
    public List<ActionItem> findByRetrospectiveId(Long retrospectiveId) {
        return list("retrospective.id", retrospectiveId);
    }

    /**
     * ステータスでアクションアイテムを検索
     */
    public List<ActionItem> findByStatus(ActionItemStatus status) {
        return list("status", status);
    }

    /**
     * 担当者でアクションアイテムを検索
     */
    public List<ActionItem> findByAssignee(String assignee) {
        return list("assignee", assignee);
    }

    /**
     * レトロスペクティブIDとステータスでアクションアイテムを検索
     */
    public List<ActionItem> findByRetrospectiveIdAndStatus(Long retrospectiveId, ActionItemStatus status) {
        return list("retrospective.id = ?1 and status = ?2", retrospectiveId, status);
    }
}
