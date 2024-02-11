package plugin.mininggamek.command;

import java.util.List;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Monster;
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
import plugin.mininggamek.main;

public class MiningGame implements CommandExecutor, Listener {

  private main main;
  private PlayerData playerData = new PlayerData();
  private int gameTime;

  public MiningGame(main main) {
    this.main = main;
  }

  @Override
  public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
    if (commandSender instanceof Player player) {
      playerData.setName(player.getName());
      playerData.setLocationX(player.getLocation().getX());
      playerData.setLocationY(player.getLocation().getY());
      playerData.setLocationZ(player.getLocation().getZ());
      gameTime = 60;
      aroundEnemyKill(player, 10);
      initPlayerStatus(player);
      player.sendMessage("GameStart！");

      Bukkit.getScheduler().runTaskTimer(main, Runnable -> {
        if (gameTime <= 0) {
          Runnable.cancel();
          player.sendMessage("ゲームが終了しました");
          returnTeleport(player);
          return;
        }
        gameTime -= 1;
        player.sendMessage("残り時間" + gameTime);
      }, 0, 20);
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
      for (Item item : items) {
        player.sendMessage("採掘したブロック：" + item.getName());
      }
    }
  }

  /**
   * ゲーム開始時のプレイヤーの状態を設定します
   * 体力と満腹度を全回復、ダイヤピッケルと暗視効果を付与
   *
   * @param player コマンドを実行したプレイヤー
   */
  private static void initPlayerStatus(Player player) {
    World world = player.getWorld();
    PlayerInventory inventory = player.getInventory();
    player.setHealth(20);
    player.setFoodLevel(20);
    world.dropItem(player.getLocation(), enchantDiaPic());
    player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 6000, 0));
    player.sendMessage("ダイヤのピッケルと暗視効果を付与しました");
  }

  /**
   * エンチャント付与したダイヤピッケルを返します
   *
   * @return ダイヤのピッケル
   */
  private static ItemStack enchantDiaPic() {
    ItemStack item = new ItemStack(Material.DIAMOND_PICKAXE);
    ItemMeta meta = item.getItemMeta();

    meta.addEnchant(Enchantment.DIG_SPEED, 5, true); //効率強化V
    meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 3, true); //幸運III
    item.setItemMeta(meta);
    return item;
  }

  /**
   * プレイヤーをデータオブジェクトに保存された座標にテレポートさせます
   *
   * @param player コマンドを実行したプレイヤー
   */
  private void returnTeleport(Player player) {
    World world = player.getWorld();
    double x = playerData.getLocationX();
    double y = playerData.getLocationY();
    double z = playerData.getLocationZ();
    Location returnLocation = new Location(world, x, y, z);
    player.teleport(returnLocation);
  }

  /**
   * プレイヤーの周囲の敵対モブを消去します。
   *
   * @param player コマンドを実行したプレイヤー
   * @param radius プレイヤーの周囲とする半径
   */
  private void aroundEnemyKill(Player player, double radius) {
    List<Entity> nearbyEntities = player.getNearbyEntities(radius, radius, radius);
    for (Entity entity : nearbyEntities) {
      if (entity instanceof Monster) {
        entity.remove();
      }
    }

  }
}
