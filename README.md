# レトロスペクティブアプリ

QuarkusフレームワークとH2データベースを使用したスクラムのレトロスペクティブ（ふりかえり）実施アプリケーションです。

## 技術スタック

- **フレームワーク**: Quarkus 3.28.3
- **ビルドツール**: Maven
- **Javaバージョン**: 17
- **データベース**: H2（インメモリ）
- **ORM**: Hibernate ORM with Panache
- **REST API**: RESTEasy Reactive with Jackson
- **API Documentation**: SmallRye OpenAPI (Swagger)

## 機能

### 1. レトロスペクティブ管理
- ふりかえりイベントの作成、更新、削除
- 日付とタイトルでの検索
- 最新のふりかえり一覧表示

### 2. カード管理（KPT形式）
- **Keep**: 良かったこと
- **Problem**: 問題・課題
- **Try**: 試したいこと
- カードの作成、更新、削除
- カードへの投票機能
- 投票数の多いカード表示

### 3. アクションアイテム管理
- アクションアイテムの作成、更新、削除
- ステータス管理（未着手、進行中、完了）
- 担当者の割り当て

### 4. UIインターフェース
- レスポンシブなWebインターフェース
- ふりかえりの一覧と詳細表示
- リアルタイムでのカード追加と投票
- アクションアイテムのステータス更新

## プロジェクト構造

```
src/
├── main/
│   ├── java/com/example/
│   │   ├── entity/          # エンティティクラス
│   │   │   ├── Retrospective.java
│   │   │   ├── Card.java
│   │   │   ├── CardType.java
│   │   │   ├── ActionItem.java
│   │   │   └── ActionItemStatus.java
│   │   ├── repository/      # リポジトリクラス
│   │   │   ├── RetrospectiveRepository.java
│   │   │   ├── CardRepository.java
│   │   │   └── ActionItemRepository.java
│   │   └── resource/        # REST APIエンドポイント
│   │       ├── RetrospectiveResource.java
│   │       ├── CardResource.java
│   │       └── ActionItemResource.java
│   └── resources/
│       ├── META-INF/resources/
│       │   ├── index.html   # フロントエンドUI
│       │   └── app.js       # JavaScriptロジック
│       ├── application.properties
│       └── import.sql       # サンプルデータ
```

## セットアップと実行

### 前提条件
- JDK 17以上
- Maven 3.8以上

### 開発モードで実行
```bash
mvn quarkus:dev
```

アプリケーションは http://localhost:8080 で起動します。

### プロダクションビルド
```bash
mvn clean package
java -jar target/quarkus-app/quarkus-run.jar
```

## REST API エンドポイント

### レトロスペクティブ
- `GET /api/retrospectives` - すべてのふりかえりを取得
- `GET /api/retrospectives/{id}` - 特定のふりかえりを取得
- `GET /api/retrospectives/recent?limit=10` - 最新のふりかえりを取得
- `POST /api/retrospectives` - ふりかえりを作成
- `PUT /api/retrospectives/{id}` - ふりかえりを更新
- `DELETE /api/retrospectives/{id}` - ふりかえりを削除

### カード
- `GET /api/retrospectives/{id}/cards` - レトロスペクティブのカードを取得
- `GET /api/retrospectives/{id}/cards/type/{type}` - タイプ別にカードを取得
- `GET /api/retrospectives/{id}/cards/top-voted?limit=5` - 投票数上位のカードを取得
- `POST /api/retrospectives/{id}/cards` - カードを追加
- `PUT /api/cards/{id}` - カードを更新
- `POST /api/cards/{id}/vote` - カードに投票
- `POST /api/cards/{id}/unvote` - 投票を取り消し
- `DELETE /api/cards/{id}` - カードを削除

### アクションアイテム
- `GET /api/retrospectives/{id}/action-items` - レトロスペクティブのアクションアイテムを取得
- `GET /api/retrospectives/{id}/action-items/status/{status}` - ステータス別に取得
- `GET /api/action-items/assignee/{assignee}` - 担当者別に取得
- `POST /api/retrospectives/{id}/action-items` - アクションアイテムを追加
- `PUT /api/action-items/{id}` - アクションアイテムを更新
- `PATCH /api/action-items/{id}/status?status={status}` - ステータスを更新
- `DELETE /api/action-items/{id}` - アクションアイテムを削除

## Swagger UI
API仕様は以下のURLで確認できます：
http://localhost:8080/swagger-ui/

## サンプルデータ
アプリケーション起動時に`import.sql`からサンプルデータが投入されます：
- 2つのレトロスペクティブ
- 各種カード（Keep/Problem/Try）
- 複数のアクションアイテム

## データベース
H2インメモリデータベースを使用しています。アプリケーション再起動時にデータはリセットされます。

## 設定ファイル（application.properties）
主要な設定：
- データベース接続設定
- Hibernateの自動DDL生成（drop-and-create）
- SQLログ出力
- CORS設定
- Swagger UI有効化

## 開発のポイント
- Panache Repositoryパターンによる簡潔なデータアクセス
- REST APIの適切なHTTPメソッドとステータスコード使用
- トランザクション管理（@Transactional）
- エンティティ間の適切な関連付け（@OneToMany, @ManyToOne）

## 今後の拡張案
- ユーザー認証・認可機能
- リアルタイム更新（WebSocket）
- ファイル添付機能
- データのエクスポート（CSV、PDF）
- PostgreSQLなど永続化データベースへの移行
- チームメンバー管理機能

## ライセンス
このプロジェクトはサンプルとして作成されています。
