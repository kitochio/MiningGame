package plugin.mininggamek;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import plugin.mininggamek.command.MiningGameCommand;

public final class Main extends JavaPlugin {

  @Override
  public void onEnable() {
    MiningGameCommand miningGameCommand = new MiningGameCommand(this);
    Bukkit.getPluginManager().registerEvents(miningGameCommand, this);
    getCommand("MiningGame").setExecutor(miningGameCommand);
  }
}
