package plugin.mininggamek.command;

import java.util.List;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import plugin.mininggamek.Main;
import plugin.mininggamek.data.PlayerData;

public class MiningGame extends BaseCommand implements Listener {

  private Main main;
  private PlayerData playerData = new PlayerData();

  public MiningGame(Main main) {
    this.main = main;
  }

  @Override
  protected boolean onExecutePlayerCommand(Player player, Command command, String s, String[] strings) {
    if (strings.length == 1) {
      if (Objects.equals(strings[0], "end")) {
        playerData.setGameTime(0);
        return false;
      }
    }
    initPlayerStatus(player);
    player.sendTitle("GameStart!", "制限時間300秒、終了したらこの場所にもどるよ。", 0, 70, 10);
    gameStart(player);
    return true;
  }

  @Override
  protected boolean onExecuteNPCCommand(CommandSender commandSender, Command command, String s,
      String[] strings) {
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
          case "Raw Copper" -> 15;
          case "Raw Gold" -> 50;
          case "Redstone Dust" -> 35;
          case "Emerald" -> 80;
          case "Lapis Lazuli" -> 100;
          case "Diamond" -> 1000;
          default -> 0;
        };
        if (point > 0) {
          playerData.setScore(playerData.getScore() + point);
          player.sendMessage(item.getName() + "を採掘した！ +"
              + point + "点 " + "/ 合計" + playerData.getScore() + "点");
        }
      }
    }
  }

  @EventHandler
  public void onPlayerRespawn(PlayerRespawnEvent e) {
    Player player = e.getPlayer();
    if (playerData.getGameTime() == 0) {
      return;
    }
    e.setRespawnLocation(getReturnLocation(player));
    Bukkit.getScheduler().runTaskLater(main, run -> {
      player.getInventory().setItemInMainHand(enchantDiaPic());
      player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION
          , playerData.getGameTime() * 20, 0));
    }, 20);
    player.sendMessage("再スタート、終了したい場合はコマンドの引数にendをいれてね");
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
    player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION
        , playerData.getGameTime() * 20, 0));
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
    player.sendMessage("GameStart！");

    Bukkit.getScheduler().runTaskTimer(main, Runnable -> {
      if (playerData.getGameTime() <= 0) {
        Runnable.cancel();
        player.sendMessage("ゲームを終了しました");
        player.sendTitle("ゲームが終了しました"
            , "今回のスコア合計は" + playerData.getScore() + "点です", 0, 70, 10);
        player.teleport(getReturnLocation(player));
        return;
      }
      //10秒に一回残り時間を表示します
      if (playerData.getGameTime() % 10 == 0) {
        player.sendMessage("残り " + playerData.getGameTime() + "秒");
      }
      playerData.setGameTime(playerData.getGameTime() - 1);
    }, 0, 20);
  }

  /**
   * プレイヤーをデータオブジェクトに保存された座標にテレポートさせます
   *
   * @param player コマンドを実行したプレイヤー
   */

  private Location getReturnLocation(Player player) {
    double x = playerData.getLocationX();
    double y = playerData.getLocationY();
    double z = playerData.getLocationZ();
    return new Location(player.getWorld(), x, y, z);
  }
}