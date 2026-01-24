package com.example.repository;

import com.example.entity.Retrospective;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;
import java.util.List;

/**
 * レトロスペクティブリポジトリ
 */
@ApplicationScoped
public class RetrospectiveRepository implements PanacheRepository<Retrospective> {

    /**
     * 日付でレトロスペクティブを検索
     */
    public List<Retrospective> findByDate(LocalDate date) {
        return list("date", date);
    }

    /**
     * タイトルでレトロスペクティブを検索（部分一致）
     */
    public List<Retrospective> findByTitleLike(String title) {
        return list("LOWER(title) LIKE LOWER(?1)", "%" + title + "%");
    }

    /**
     * 最新のレトロスペクティブを取得
     */
    public List<Retrospective> findRecent(int limit) {
        return find("ORDER BY date DESC").page(0, limit).list();
    }
}
