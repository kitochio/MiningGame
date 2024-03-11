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

___注：ランキング機能を動作させる場合は、MySqlをローカルホストで動作させる必要があります___

### MySqlの設定
- ユーザー名 root
- パスワード rootroot
- データーベース名　spigot_server
- テーブル名 mininggame_score
- URL mysql://localhost:3306/spigot_server
- テーブル　id(int) | player_name(varchar) | score(int) | registered_at(datetime)

___テーブルの作成は　MySql上で下記のコマンドで設定してください___
- Macの場合  
`CREATE TABLE mininggame_score(id int auto_increment, player_name varchar(100), score int, registered_at datetime, primary key(id));`

- Windowsの場合  
`CREATE TABLE mininggame_score(id int auto_increment, player_name varchar(100), score int, registered_at datetime, primary key(id)) DEFAULT CHARSET=utf8;`

