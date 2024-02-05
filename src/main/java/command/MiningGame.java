package command;

import java.util.List;
import java.util.Objects;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class MiningGame implements CommandExecutor, Listener {

  private Player player;

  @Override
  public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
    if (commandSender instanceof Player player) {
      this.player = player;

      initPlayerStatus(player);
    }
    return false;
  }

  @EventHandler
  public void onDropItem(BlockDropItemEvent e) {
    if (Objects.isNull(player)) {
      return;
    }

    if (e.getPlayer().getName().equals(player.getName())) {
      List<Item> items = e.getItems();
      for (int i = 0; i < items.size(); i++) {
        Item item = items.get(i);
        player.sendMessage(item.getName());
      }
    }


  }

  private static void initPlayerStatus(Player player) {
    PlayerInventory inventory = player.getInventory();
    player.setHealth(20);
    player.setFoodLevel(20);
    inventory.setItemInMainHand(enchantDiaPic());
    player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 6000, 0));
  }

  private static ItemStack enchantDiaPic() {
    ItemStack item = new ItemStack(Material.DIAMOND_PICKAXE);
    ItemMeta meta = item.getItemMeta();
    if (Objects.isNull(meta)) {
      return null;
    }
    meta.addEnchant(Enchantment.DIG_SPEED, 5, true); //効率強化V
    meta.addEnchant(Enchantment.DURABILITY, 3, true); //耐久力III
    meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 3, true); //幸運III
    item.setItemMeta(meta);
    return item;
  }

}
