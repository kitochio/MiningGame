package plugin.mininggamek.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerData {

  private String name;
  private int score;
  private long gameTime;
  private double locationX;
  private double locationY;
  private double locationZ;
}
