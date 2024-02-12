package plugin.mininggamek.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * コマンドを実行して動かすプラグインの基底クラスです。
 */
public abstract class BaseCommand implements CommandExecutor {

  @Override
  public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
    if (commandSender instanceof Player player) {
      return onExecutePlayerCommand(player);
    } else {
      return onExecuteNPCCommand(commandSender);
    }
  }

  /**
   * コマンド実行者がプレイヤーだった場合に実行します
   *
   * @param player コマンドを実行したプレイヤー
   * @return 処理の実行有無
   */
  protected abstract boolean onExecutePlayerCommand(Player player);

  /**
   * コマンド実行者がプレイヤー以外だった場合に実行します
   *
   * @param commandSender コマンド実行者
   * @return 処理の実行有無
   */
  protected abstract boolean onExecuteNPCCommand(CommandSender commandSender);
}
