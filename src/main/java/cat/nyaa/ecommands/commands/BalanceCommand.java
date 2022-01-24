package cat.nyaa.ecommands.commands;

import cat.nyaa.ecommands.SpigotLoader;
import land.melon.lab.simplelanguageloader.utils.Pair;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Objects;

public class BalanceCommand implements CommandExecutor {
    private final SpigotLoader pluginInstance;

    public BalanceCommand(SpigotLoader pluginInstance) {
        this.pluginInstance = pluginInstance;
    }

    @Override
    public boolean onCommand(@Nonnull CommandSender commandSender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args) {
        if (commandSender instanceof ConsoleCommandSender) {
            commandSender.sendMessage("You can't use this command in console.");
            return true;
        }
        var player = (Player) commandSender;
        if (args.length == 0) {
            commandSender.sendMessage(pluginInstance.getMainLang().balanceCommand.selfBalance.produce(
                    Pair.of("balance", pluginInstance.getEconomyCore().getPlayerBalance(player.getUniqueId())
                    )));
        } else {
            var offlinePlayer = getPlayer(args[0]);
            if (offlinePlayer.getUniqueId() != player.getUniqueId() && !player.hasPermission("ecommands.balance.other")) {
                commandSender.sendMessage(pluginInstance.getMainLang().balanceCommand.insufficientPermission.produce());
                return true;
            }
            if (!offlinePlayer.hasPlayedBefore()) {
                commandSender.sendMessage(pluginInstance.getMainLang().balanceCommand.playerNotExistAbort.produce(
                        Pair.of("player", offlinePlayer.getName())
                ));
                return true;
            }
            commandSender.sendMessage(pluginInstance.getMainLang().balanceCommand.otherBalance.produce(
                    Pair.of("player", offlinePlayer.getName()),
                    Pair.of("balance", pluginInstance.getEconomyCore().getPlayerBalance(offlinePlayer.getUniqueId()))
            ));
        }
        return true;
    }

    @SuppressWarnings("deprecation")
    private OfflinePlayer getPlayer(String name) {
        return Objects.requireNonNullElseGet(Bukkit.getPlayer(name), () -> Bukkit.getOfflinePlayer(name));
        //inevitable deprecated api call
    }
}
