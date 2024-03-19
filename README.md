## 鉱石採掘を簡単に、ポイントを獲得するゲームでいつの間にか鉱石が手に入ります！
（マインクラフトのSpigotサーバー用のプラグインです）

### 遊び方
1. お好きなところで「/mininggame」と入力すると採掘ゲームが開始されます
   - 「/mininggame end」と入力するとゲームを終了できます
1. 制限時間内に地面を掘って鉱石を集めてください
   - ダイヤピッケルと暗視効果が付与されます
1. 制限時間を過ぎると獲得したスコアが表示されます、高得点をめざしましょう！
   - データーベースにスコアが登録されます、「/mininggame rank」と入力するとランキングが表示されます

### 対応するバージョン
- Spigot 1.20.4
- Mincraft 1.20.4

### MySQLの設定
___注：ランキング機能を動作させる場合は、MySQLをローカルホストで動作させる必要があります___

Dockerがインストールされている環境でしたら、DockerによるMySQLの構築が可能です。
1. ターミナルでdocker-mysql-mining-gameフォルダに移動
2. 「docker compose up -d」コマンドでコンテナを起動
3. 「docker compose down」で終了することができます

（Windows環境の方はターミナルとしてGit BashやWSLをご使用ください）

__DockerによるMySQLの構築は下記のハンズオンを参考にさせていただきました__  
https://github.com/yoshi-koyama/docker-mysql-hands-on

### MySQL設定値
- ユーザー名 root
- パスワード rootroot
- データーベース名　spigot_server
- テーブル名 mininggame_score
- URL mysql://localhost:3307/spigot_server
- テーブル  

| Field         | Type         | Null | Key | Default | Extra          |
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

