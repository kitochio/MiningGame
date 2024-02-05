package plugin.mininggamek;

import command.MiningGame;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class main extends JavaPlugin {

  @Override
  public void onEnable() {
    MiningGame miningGame = new MiningGame();
    Bukkit.getPluginManager().registerEvents(miningGame, this);
    getCommand("MiningGame").setExecutor(miningGame);
  }
}
