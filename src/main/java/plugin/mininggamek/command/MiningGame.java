package plugin.mininggamek.command;

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
import plugin.mininggamek.data.PlayerData;

public class MiningGame implements CommandExecutor, Listener {

  private PlayerData playerData = new PlayerData();

  @Override
  public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
    if (commandSender instanceof Player player) {
      playerData.setName(player.getName());
      initPlayerStatus(player);
      player.sendMessage("GameStart！");
    }
    return false;
  }

  @EventHandler
  public void onDropItem(BlockDropItemEvent e) {
    if (Objects.isNull(playerData.getName())) {
      return;
    }

    Player player = e.getPlayer();
    if (player.getName().equals(playerData.getName())) {
      List<Item> items = e.getItems();
      for (int i = 0; i < items.size(); i++) {
        Item item = items.get(i);
        player.sendMessage("採掘したブロック：" + item.getName());
      }
    }
  }

  private static void initPlayerStatus(Player player) {
    PlayerInventory inventory = player.getInventory();
    player.setHealth(20);
    player.setFoodLevel(20);
    inventory.setItemInMainHand(enchantDiaPic());
    player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 6000, 0));
    player.sendMessage("ダイヤのピッケルと暗視効果を付与しました");
  }

  private static ItemStack enchantDiaPic() {
    ItemStack item = new ItemStack(Material.DIAMOND_PICKAXE);
    ItemMeta meta = item.getItemMeta();

    meta.addEnchant(Enchantment.DIG_SPEED, 5, true); //効率強化V
    meta.addEnchant(Enchantment.DURABILITY, 3, true); //耐久力III
    meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 3, true); //幸運III
    item.setItemMeta(meta);
    return item;
  }
}
