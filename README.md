# レトロスペクティブアプリ

QuarkusフレームワークとH2データベースを使用したスクラムのレトロスペクティブ（ふりかえり）実施アプリケーションです。

## 技術スタック

### バックエンド
- **フレームワーク**: Quarkus 3.28.3
- **ビルドツール**: Maven
- **Javaバージョン**: 17
- **データベース**: H2（ファイルベース）
- **ORM**: Hibernate ORM with Panache
- **REST API**: RESTEasy Reactive with Jackson
- **API Documentation**: SmallRye OpenAPI (Swagger)

### フロントエンド
- **ライブラリ**: React 19
- **ビルドツール**: Vite 6
- **状態管理**: React Hooks (useState, useEffect)
- **API通信**: Fetch API

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
frontend/                          # React SPA (Vite)
├── package.json
├── vite.config.js
├── index.html
└── src/
    ├── main.jsx                   # エントリーポイント
    ├── App.jsx                    # ルートコンポーネント（画面切り替え）
    ├── App.css                    # グローバルスタイル
    ├── api.js                     # REST APIクライアント
    └── components/
        ├── RetrospectiveList.jsx  # ふりかえり一覧（カードグリッド）
        ├── RetrospectiveForm.jsx  # ふりかえり作成フォーム
        ├── RetrospectiveDetail.jsx # 詳細画面（KPTカラム＋アクションアイテム）
        ├── CardColumn.jsx         # KPTカラム（KEEP/PROBLEM/TRY）
        ├── CardForm.jsx           # カード追加フォーム
        ├── ActionItemList.jsx     # アクションアイテム一覧＋ステータス操作
        └── ActionItemForm.jsx     # アクションアイテム追加フォーム

src/
├── main/
│   ├── java/com/example/
│   │   ├── entity/                # エンティティクラス
│   │   │   ├── Retrospective.java
│   │   │   ├── Card.java
│   │   │   ├── CardType.java
│   │   │   ├── ActionItem.java
│   │   │   └── ActionItemStatus.java
│   │   ├── repository/            # リポジトリクラス
│   │   │   ├── RetrospectiveRepository.java
│   │   │   ├── CardRepository.java
│   │   │   └── ActionItemRepository.java
│   │   └── resource/              # REST APIエンドポイント
│   │       ├── RetrospectiveResource.java
│   │       ├── CardResource.java
│   │       └── ActionItemResource.java
│   └── resources/
│       ├── META-INF/resources/    # Viteビルド出力先（自動生成）
│       ├── application.properties
│       └── import.sql             # サンプルデータ
```

## セットアップと実行

### 前提条件
- JDK 17以上
- Maven 3.8以上
- Node.js 18以上

### フロントエンドの初期設定
```bash
cd frontend
npm install
```

### 開発モードで実行

ターミナルを2つ開いて、それぞれ以下を実行します。

```bash
# ターミナル1: バックエンド（ポート8080）
mvn quarkus:dev

# ターミナル2: フロントエンド開発サーバー（ポート5173）
cd frontend
npm run dev
```

開発時は http://localhost:5173 にアクセスします。Vite開発サーバーが `/api` へのリクエストをQuarkus（:8080）にプロキシします。

### プロダクションビルド
```bash
# フロントエンドをビルド（出力先: src/main/resources/META-INF/resources/）
cd frontend
npm run build

# バックエンドをパッケージング（ビルド済みフロントエンドを含む）
mvn clean package
java -jar target/quarkus-app/quarkus-run.jar
```

プロダクションモードでは http://localhost:8080 でフロントエンドとバックエンドの両方が配信されます。

## テスト

### ユニットテスト・統合テスト
```bash
# すべてのテストを実行
mvn test

# 特定のテストクラスを実行
mvn test -Dtest=com.example.resource.RetrospectiveResourceTest

# 統合テストを含めて実行
mvn verify
```

### ミューテーションテスト（PITest）
コードの品質を検証するためのミューテーションテストを実行できます。

```bash
# ミューテーションテストを実行
mvn test-compile org.pitest:pitest-maven:mutationCoverage

# レポートを表示（macOS）
open target/pit-reports/index.html

# レポートを表示（Linux）
xdg-open target/pit-reports/index.html
```

**注意**: PITestはエンティティクラス（`com.example.entity.*`）のみを対象としています。Repository/ResourceテストはQuarkusのクラスローダーとの互換性の問題により除外されています。

### テスト構成
```
src/test/java/com/example/
├── entity/           # エンティティの単体テスト（PITest対象）
├── repository/       # リポジトリの統合テスト（@QuarkusTest）
└── resource/         # REST APIの統合テスト（@QuarkusTest）
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
- `GET /api/cards/retrospectives/{id}` - レトロスペクティブのカードを取得
- `GET /api/cards/retrospectives/{id}/type/{type}` - タイプ別にカードを取得
- `GET /api/cards/retrospectives/{id}/top-voted?limit=5` - 投票数上位のカードを取得
- `POST /api/cards/retrospectives/{id}` - カードを追加
- `PUT /api/cards/{id}` - カードを更新
- `POST /api/cards/{id}/vote` - カードに投票
- `POST /api/cards/{id}/unvote` - 投票を取り消し
- `DELETE /api/cards/{id}` - カードを削除

### アクションアイテム
- `GET /api/action-items/retrospectives/{id}` - レトロスペクティブのアクションアイテムを取得
- `GET /api/action-items/retrospectives/{id}/status/{status}` - ステータス別に取得
- `GET /api/action-items/assignee/{assignee}` - 担当者別に取得
- `POST /api/action-items/retrospectives/{id}` - アクションアイテムを追加
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
H2ファイルベースデータベースを使用しています（`./data/retrospective`）。データはアプリケーション再起動後も保持されます。

## 設定ファイル（application.properties）
主要な設定：
- データベース接続設定
- Hibernateの自動DDL生成（drop-and-create）
- SQLログ出力
- CORS設定
- Swagger UI有効化

## アーキテクチャ

### バックエンド（3層構造）
- **Entity** → **Repository** → **Resource（RESTコントローラー）**
- DTOは使わず、エンティティを直接JSONにシリアライズ
- Panache Repositoryパターンによる簡潔なデータアクセス
- トランザクション管理（@Transactional）
- エンティティ間の関連付け（@OneToMany, @ManyToOne、カスケード削除）

### フロントエンド（React SPA）
- Viteでビルドし、Quarkusの静的リソースディレクトリへ出力
- コンポーネント単位でデータ取得・ローカルステート管理
- 外部状態管理ライブラリは不使用（React Hooksのみ）
- 開発時はVite開発サーバーがAPIリクエストをバックエンドにプロキシ

## 今後の拡張案
- ユーザー認証・認可機能
- リアルタイム更新（WebSocket）
- ファイル添付機能
- データのエクスポート（CSV、PDF）
- PostgreSQLなど永続化データベースへの移行
- チームメンバー管理機能

## ライセンス
このプロジェクトはサンプルとして作成されています。
