鉱石採掘を簡単に、ポイントを獲得するゲームでいつの間にか鉱石が手に入ります！<br>
（マインクラフトのSpigotサーバー用のプラグインです）<br>
<br>
遊び方<br>
１，お好きなところで「/mininggame」と入力すると採掘ゲームが開始されます<br>
（「/mininggame end」と入力するとゲームを終了できます）<br>
２，制限時間内に地面を掘って鉱石を集めてください<br>
（ダイヤピッケルと暗視効果が付与されます）<br>
３，制限時間を過ぎると獲得したスコアが表示されます、高得点をめざしましょう！<br>
（データーベースにスコアが登録されます、「/mininggame rank」と入力するとランキングが表示されます）<br>
<br>
対応するバージョン<br>
Spigot 1.20.4<br>
Mincraft 1.20.4<br>

注：ランキング機能を動作させる場合は、MySqlをローカルホストで動作させる必要があります<br>

MySqlの設定<br>
ユーザー名 root<br>
パスワード rootroot<br>
データーベース名　spigot_server<br>
テーブル名 mininggame_score<br>
URL mysql://localhost:3306/spigot_server

テーブル　id(int) | player_name(varchar) | score(int) | registered_at(datetime)<br>
↑　MySql上で下記のコマンドで設定してください<br>
// Macの場合<br>
CREATE TABLE mininggame_score(id int auto_increment, player_name varchar(100), score int, registered_at datetime, primary key(id));<br>

// Windowsの場合<br>
CREATE TABLE mininggame_score(id int auto_increment, player_name varchar(100), score int, registered_at datetime, primary key(id)) DEFAULT CHARSET=utf8;<br>
