DROP TABLE IF EXISTS mininggame_score;

CREATE TABLE mininggame_score (
  id int unsigned AUTO_INCREMENT,
  player_name VARCHAR(100) NOT NULL,
  score int,
  registered_at datetime NOT NULL,
  PRIMARY KEY(id)
);

INSERT INTO mininggame_score (player_name, score, registered_at) VALUES ("TestUser", 1000, now());
