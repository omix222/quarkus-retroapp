-- サンプルデータの挿入

-- レトロスペクティブ
INSERT INTO retrospectives (id, title, date, description) VALUES (1, 'Sprint 1 ふりかえり', '2025-10-01', '最初のスプリントのふりかえり');
INSERT INTO retrospectives (id, title, date, description) VALUES (2, 'Sprint 2 ふりかえり', '2025-10-15', '2回目のスプリントのふりかえり');

-- カード
INSERT INTO cards (id, type, content, votes, retrospective_id) VALUES (1, 'KEEP', 'チーム内のコミュニケーションが良かった', 5, 1);
INSERT INTO cards (id, type, content, votes, retrospective_id) VALUES (2, 'KEEP', 'ペアプログラミングが効果的だった', 3, 1);
INSERT INTO cards (id, type, content, votes, retrospective_id) VALUES (3, 'PROBLEM', 'テストコードが不足していた', 7, 1);
INSERT INTO cards (id, type, content, votes, retrospective_id) VALUES (4, 'PROBLEM', 'リリース作業に時間がかかった', 4, 1);
INSERT INTO cards (id, type, content, votes, retrospective_id) VALUES (5, 'TRY', 'CI/CDパイプラインを改善する', 6, 1);
INSERT INTO cards (id, type, content, votes, retrospective_id) VALUES (6, 'TRY', 'コードレビューの時間を確保する', 2, 1);

-- アクションアイテム
INSERT INTO action_items (id, description, assignee, status, retrospective_id) VALUES (1, 'CI/CDパイプラインの構築', '田中', 'IN_PROGRESS', 1);
INSERT INTO action_items (id, description, assignee, status, retrospective_id) VALUES (2, 'テストコードのカバレッジを80%以上にする', '佐藤', 'TODO', 1);
INSERT INTO action_items (id, description, assignee, status, retrospective_id) VALUES (3, 'コードレビューチェックリストを作成', '鈴木', 'DONE', 1);

-- シーケンスの設定（H2の場合）
ALTER SEQUENCE retrospectives_seq RESTART WITH 3;
ALTER SEQUENCE cards_seq RESTART WITH 7;
ALTER SEQUENCE action_items_seq RESTART WITH 4;
