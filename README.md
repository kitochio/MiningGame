## 鉱石採掘を簡単に、ポイントを獲得するゲームでいつの間にか鉱石が手に入ります！
（マインクラフトのSpigotサーバー用のプラグインです）

https://github.com/user-attachments/assets/9e8f8b8b-c775-4aeb-8472-cddcefab366f

### 遊び方
1. お好きなところで「/mininggame」と入力すると採掘ゲームが開始されます
   - 「/mininggame end」と入力するとゲームを終了できます
1. 制限時間内に地面を掘って鉱石を集めてください
   - ダイヤピッケルと暗視効果が付与されます
1. 制限時間を過ぎると獲得したスコアが表示されます、高得点をめざしましょう！
   - データーベースにスコアが登録されます、「/mininggame rank」と入力するとランキングが表示されます

### 対応するバージョン
- ビルド対象API: Spigot 1.20.4 (Java 17)
- 動作確認済みサーバ: Spigot 1.20.4 / PaperMC 1.21.11
- Minecraft Java Edition 1.20.4 以降

※ Bukkit 標準 API のみ使用しているため、Spigot 1.20.4 でビルドした jar が
PaperMC の新しいバージョンでもそのまま動作します。
PaperMC 1.20.5 以降をご利用の場合、サーバ実行に Java 21 が必要です。

### MySQLの設定
___注：ランキング機能を動作させる場合は、MySQLをローカルホストで動作させる必要があります___

Dockerがインストールされている環境でしたら、DockerによるMySQLの構築が可能です。
1. ターミナルでdocker-mysql-mining-gameフォルダに移動
2. 「docker compose up -d」コマンドでコンテナを起動
3. 「docker compose down」で終了することができます

（Windows環境の方はターミナルとしてGit BashやWSLをご使用ください）

※ Apple Silicon Mac (M シリーズ) でも追加設定なしでネイティブ動作します。

__DockerによるMySQLの構築は下記のハンズオンを参考にさせていただきました__  
https://github.com/yoshi-koyama/docker-mysql-hands-on

### MySQL設定値
- ユーザー名 root
- パスワード rootroot
- データーベース名　spigot_server
- テーブル名 mininggame_score
- URL mysql://localhost:3307/spigot_server
- テーブル  

| Field         | Type         | Nullable | Key | Default | Extra          |
| ---- | ---- | ---- | ---- | ---- | ---- |
| id            | int unsigned | NO   | PRI | NULL    | auto_increment |
| player_name   | varchar(100) | NO   |     | NULL    |                |
| score         | int          | NO   |     | NULL    |                |
| registered_at | datetime     | NO   |     | NULL    |                |

テーブルは、MySQL上で下記のコマンドを実行すると作成できます（Docker使用時は不要です）
- Macの場合  
```sql
CREATE TABLE mininggame_score(id int auto_increment, player_name varchar(100), score int, registered_at datetime, primary key(id));
```

- Windowsの場合  
```sql
CREATE TABLE mininggame_score(id int auto_increment, player_name varchar(100), score int, registered_at datetime, primary key(id)) DEFAULT CHARSET=utf8;
```

### ビルド方法
ソースからプラグインをビルドする場合の手順です（リリース済みのjarを使う場合は不要です）。

必要環境:
- JDK 21 (推奨。最小は JDK 17)

ビルド:
```bash
./gradlew shadowJar
```

生成物 `build/libs/miningGameK-1.0-SNAPSHOT-all.jar` を、
Spigot / Paper サーバの `plugins/` フォルダにコピーして再起動してください。

※ MyBatis と MySQL JDBC ドライバを同梱する必要があるため、通常の `build` ではなく
`shadowJar` タスクで生成された `-all.jar` を使用してください。

