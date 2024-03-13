package plugin.mininggamek.command;

import java.time.format.DateTimeFormatter;
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
import plugin.mininggamek.PlayerScoreData;
import plugin.mininggamek.data.PlayerData;
import plugin.mininggamek.mapper.data.PlayerScore;

public class MiningGameCommand extends BaseCommand implements Listener {

  private final Main main;
  private final PlayerScoreData playerScoreData = new PlayerScoreData();
  private final PlayerData playerData = new PlayerData();

  public MiningGameCommand(Main main) {
    this.main = main;
  }

  @Override
  protected boolean onExecutePlayerCommand(Player player, Command command, String s, String[] strings) {
    if (strings.length == 1) {
      //第一引数にendが入力されたらゲーム終了、rankが入力されたらランキングを表示
      switch (strings[0]) {
        case "end" -> {
          playerData.setGameTime(0);
          return false;
        }
        case "rank" -> {
          sendPlayerScoreList(player);
          return false;
        }
        default -> {
          player.sendMessage("設定のない引数が入力されました\nend:ゲームを終了　rank:ランキングを表示");
          return false;}
      }
    }
    //ゲームスタート
    if (playerData.getGameTime() > 0) {
      player.sendMessage("すでにゲームが実行されています");
      return false;
    }
    initPlayerStatus(player);
    player.sendTitle("GameStart!", "制限時間300秒、終了したらこの場所にもどるよ。",
        0, 70, 10);
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
    addPoints(e);
  }

  @EventHandler
  public void onPlayerRespawn(PlayerRespawnEvent e) {
    if (playerData.getGameTime() <= 0) {
      return;
    }
    gameRestart(e);
  }

  /**
   * 現在登録されているランキングの一覧をメッセージに送る
   *
   * @param player コマンドを実行したプレイヤー
   */
  private void sendPlayerScoreList(Player player) {
    List<PlayerScore> playerScoreList = playerScoreData.selectList();
    int i = 0;
    for (PlayerScore playerScore : playerScoreList) {
      i++;
      player.sendMessage(i + "位 | "
          + playerScore.getPlayerName() + " | "
          + playerScore.getScore() + " | "
          + playerScore.getRegisteredAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }
  }

  /**
   * ブロックから出現したアイテムにより、ポイントを加算して合計値を表示します。
   * 出現したアイテムの名前が、ポイントを指定しているアイテム名と同じ場合に
   * playerDataのscoreにポイントを加算して、合計値を表示します。
   *
   * @param e アイテムドロップイベント
   */
  private void addPoints(BlockDropItemEvent e) {
    if (e.getPlayer().getName().equals(playerData.getName())) {
      List<Item> items = e.getItems();
      for (Item item : items) {
        Material material = item.getItemStack().getType();
        int point = switch (material) {
          case COAL -> 10;
          case RAW_IRON -> 20;
          case RAW_COPPER -> 15;
          case RAW_GOLD -> 120;
          case REDSTONE -> 35;
          case EMERALD -> 80;
          case LAPIS_LAZULI -> 100;
          case DIAMOND -> 1000;
          default -> 0;
        };
        if (point > 0) {
          playerData.setScore(playerData.getScore() + point);
          String message = "%sを採掘した！ +%d点 / 合計%d点".formatted(item.getName(), point, playerData.getScore());
          e.getPlayer().sendMessage(message);
        }
      }
    }
  }

  /**
   * リスポーン位置をコマンド実行時の位置に変更して、アイテムと暗視を付与します。
   * playerDataに保存された位置をリスポーン地点に設定して、リスポーンの1秒後にアイテムと暗視効果を付与します。
   * 暗視の効果時間はゲームの残り時間と同じになります。
   *
   * @param e プレイヤーリスポーンイベント
   */
  private void gameRestart(PlayerRespawnEvent e) {
    e.setRespawnLocation(getReturnLocation(e.getPlayer()));
    Bukkit.getScheduler().runTaskLater(main, run -> {
      e.getPlayer().getInventory().setItemInMainHand(enchantDiaPic());
      e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION
          , playerData.getGameTime() * 20, 0));
    }, 20);
    e.getPlayer().sendMessage("再スタート、終了したい場合はコマンドの引数にendをいれてね");
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
    nearbyEntities.stream().filter(entity -> entity instanceof Monster).forEach(Entity::remove);
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
        player.sendMessage("ゲーム終了 スコア合計%d点".formatted(playerData.getScore()));
        player.sendTitle("ゲームが終了しました"
            , "今回のスコア合計は%d点です".formatted(playerData.getScore()), 0, 70, 10);
        player.teleport(getReturnLocation(player));
        playerScoreData.insert(new PlayerScore(playerData.getName(), playerData.getScore()));
        player.sendMessage("コマンドの引数にrankと入力するとランキングが見れます");
        return;
      }
      //10秒に一回残り時間を表示します
      if (playerData.getGameTime() % 10 == 0) {
        player.sendMessage("残り %d秒".formatted(playerData.getGameTime()));
      }

      playerData.setGameTime(playerData.getGameTime() - 1);
    }, 0, 20);
  }

  /**
   * データオブジェクトからゲーム開始位置を取得します
   *
   * @param player コマンドを実行したプレイヤー
   * @return ゲーム開始位置
   */
  private Location getReturnLocation(Player player) {
    double x = playerData.getLocationX();
    double y = playerData.getLocationY();
    double z = playerData.getLocationZ();
    return new Location(player.getWorld(), x, y, z);
  }
}