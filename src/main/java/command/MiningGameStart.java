package command;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class MiningGameStart implements CommandExecutor {

  @Override
  public boolean onCommand(CommandSender commandSender, Command command, String s,
      String[] strings) {
    if (commandSender instanceof Player player) {
      PlayerInventory inventory = player.getInventory();

      player.setHealth(20);
      player.setFoodLevel(20);
      inventory.setItemInMainHand(new ItemStack(Material.DIAMOND_PICKAXE));

    }
    return false;
  }
}
