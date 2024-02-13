package plugin.mininggamek.command;

import java.util.List;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
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
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import plugin.mininggamek.data.PlayerData;
import plugin.mininggamek.main;

public class MiningGame extends BaseCommand implements Listener {

  private main main;
  private PlayerData playerData = new PlayerData();
  long displayInterval = 10; //残り時間を減らし、その時間を表示する間隔（秒）

  public MiningGame(main main) {
    this.main = main;
  }

  @Override
  protected boolean onExecutePlayerCommand(Player player) {
    initPlayerStatus(player);
    player.sendMessage("GameStart！");
    gameStart(player);
    return true;
  }

  @Override
  protected boolean onExecuteNPCCommand(CommandSender commandSender) {
    return true;
  }

  @EventHandler
  public void onDropItem(BlockDropItemEvent e) {
    if (Objects.isNull(playerData.getName()) || playerData.getGameTime() <= 0) {
      return;
    }

    Player player = e.getPlayer();
    if (player.getName().equals(playerData.getName())) {
      List<Item> items = e.getItems();
      for (Item item : items) {
        int point = switch (item.getName()) {
          case "Coal" -> 10;
          case "Raw Iron" -> 20;
          case "Raw Copper" -> 30;
          case "Raw Gold" -> 50;
          case "Redstone Dust" -> 35;
          case "Emerald" -> 80;
          case "Lapis Lazuli" -> 100;
          case "Diamond" -> 1000;
          default -> 0;
        };

        if (point > 0) {
          playerData.setScore(playerData.getScore() + point);
          player.sendMessage(
              item.getName() + "を採掘した！ " + point + "点 / 合計" + playerData.getScore()
                  + "点");
        }
      }
    }
  }

  /**
   * ゲーム開始時のプレイヤーの状態を設定します
   * 周囲の敵対モブを消去
   * 名前と現在位置の登録、ゲーム時間とスコアの初期化
   * 体力と満腹度を全回復、ダイヤピッケルと暗視効果を付与
   *
   * @param player コマンドを実行したプレイヤー
   */
  private void initPlayerStatus(Player player) {
    aroundEnemyKill(player, 10);
    playerData.setName(player.getName());
    playerData.setLocationX(player.getLocation().getX());
    playerData.setLocationY(player.getLocation().getY());
    playerData.setLocationZ(player.getLocation().getZ());
    playerData.setGameTime(300);
    playerData.setScore(0);
    player.setHealth(20);
    player.setFoodLevel(20);
    player.getWorld().dropItem(player.getLocation(), enchantDiaPic());
    player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 6000, 0));
    player.sendMessage("ダイヤのピッケルと暗視効果を付与しました");
  }

  /**
   * エンチャント付与したダイヤピッケルを返します。
   * ゲーム中で使い切るぐらいの耐久値にしています。
   *
   * @return ダイヤのピッケル
   */
  private ItemStack enchantDiaPic() {
    ItemStack item = new ItemStack(Material.DIAMOND_PICKAXE);
    ItemMeta meta = item.getItemMeta();

    meta.addEnchant(Enchantment.DIG_SPEED, 5, true); //効率強化V
    meta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 3, true); //幸運III
    item.setItemMeta(meta);

    //耐久値を落とす
    if (meta instanceof Damageable) {
      Damageable damageable = (Damageable) meta;
      damageable.setDamage(1200);
      item.setItemMeta(damageable);
    }
    return item;
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

  /**
   * ゲームを開始して、一定時間が経過したら終了します。
   * 終了の際にスコア合計を表示します。
   *
   * @param player コマンドを実行したプレイヤー
   */
  private void gameStart(Player player) {
    Bukkit.getScheduler().runTaskTimer(main, Runnable -> {
      if (playerData.getGameTime() <= 0) {
        Runnable.cancel();
        player.sendTitle("ゲームが終了しました"
            , "今回のスコア合計は" + playerData.getScore() + "点です", 0, 60, 1);
        returnTeleport(player);
        return;
      }
      player.sendMessage("残り " + playerData.getGameTime() + "秒");
      playerData.setGameTime(playerData.getGameTime() - displayInterval);
    }, 0, 20 * displayInterval);
  }

  /**
   * プレイヤーをデータオブジェクトに保存された座標にテレポートさせます
   *
   * @param player コマンドを実行したプレイヤー
   */
  private void returnTeleport(Player player) {
    double x = playerData.getLocationX();
    double y = playerData.getLocationY();
    double z = playerData.getLocationZ();
    Location returnLocation = new Location(player.getWorld(), x, y, z);
    player.teleport(returnLocation);
  }
}