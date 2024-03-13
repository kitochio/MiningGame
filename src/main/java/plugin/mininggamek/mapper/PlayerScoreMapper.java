package plugin.mininggamek.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import plugin.mininggamek.mapper.data.PlayerScore;

public interface PlayerScoreMapper {

  @Select("SELECT * FROM mininggame_score ORDER BY score DESC LIMIT 5")
  List<PlayerScore> selectList();

  @Insert("INSERT mininggame_score(player_name, score, registered_at) VALUES (#{playerName}, #{score}, now())")
  int insert(PlayerScore playerScore);
}
