package command;

import java.util.List;
import java.util.Objects;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class MiningGame implements CommandExecutor, Listener {

  private Player player;

  @Override
  public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
    if (commandSender instanceof Player player) {
      this.player = player;
      PlayerInventory inventory = player.getInventory();

      player.setHealth(20);
      player.setFoodLevel(20);
      inventory.setItemInMainHand(new ItemStack(Material.DIAMOND_PICKAXE));
    }
    return false;
  }

  @EventHandler
  public void onDropItem(BlockDropItemEvent e) {
    if (Objects.isNull(player)) {
      return;
    }

    List<Item> items = e.getItems();
    for (int i = 0; i < items.size(); i++) {
      Item item = items.get(i);
      player.sendMessage(item.getName());
    }
  }


}
