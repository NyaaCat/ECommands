package cat.nyaa.ecommands.commands;

import cat.nyaa.ecommands.SpigotLoader;
import cat.nyaa.ecommands.utils.Vault;
import land.melon.lab.simplelanguageloader.utils.Pair;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class BalanceCommand implements TabExecutor {
    private final SpigotLoader pluginInstance;
    private final String PERMISSION_BALANCE_OTHERS = "ecommands.balance.others";

    public BalanceCommand(SpigotLoader pluginInstance) {
        this.pluginInstance = pluginInstance;
    }

    @Override
    public boolean onCommand(@Nonnull CommandSender commandSender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args) {
        if (args.length == 0) {
            if (commandSender instanceof ConsoleCommandSender) {
                commandSender.sendMessage(
                        pluginInstance.getMainLang().balanceCommand.consoleWithParametersOnly.produce(
                                Pair.of("systemVaultName", pluginInstance.getEconomyCore().systemVaultName())
                        )
                );
            } else {
                commandSender.sendMessage(pluginInstance.getMainLang().balanceCommand.selfBalance.produce(
                        Pair.of("balance", pluginInstance.getEconomyCore().getPlayerBalance(((Player) commandSender).getUniqueId())),
                        Pair.of("currencyUnit", pluginInstance.getEconomyCore().currencyNamePlural()))
                );
            }
        } else {
            if (args[0].equalsIgnoreCase(commandSender.getName()) && !commandSender.hasPermission(PERMISSION_BALANCE_OTHERS)) {
                commandSender.sendMessage(pluginInstance.getMainLang().balanceCommand.insufficientPermission.produce());
                return true;
            }
            Vault vault;
            try {
                vault = Vault.of(args[0], pluginInstance.getEconomyCore());
            } catch (Exception e) {
                commandSender.sendMessage(pluginInstance.getMainLang().balanceCommand.playerNotExistAbort.produce(
                        Pair.of("player", args[0])
                ));
                return true;
            }
            commandSender.sendMessage(pluginInstance.getMainLang().balanceCommand.otherBalance.produce(
                    Pair.of("player", vault.name),
                    Pair.of("balance", vault.balance()),
                    Pair.of("currencyUnit", pluginInstance.getEconomyCore().currencyNamePlural())
            ));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@Nonnull CommandSender commandSender, @Nonnull Command command, @Nonnull String s, @Nonnull String[] strings) {
        if (strings.length == 0) {
            var list = new ArrayList<String>();
            if (commandSender instanceof ConsoleCommandSender || (commandSender instanceof Player && commandSender.hasPermission(PERMISSION_BALANCE_OTHERS))) {
                Bukkit.getOnlinePlayers().forEach(player -> list.add(player.getName()));
                list.add("$system");
            } else {
                list.add(commandSender.getName());
            }
            return list;
        } else {
            return null;
        }
    }
}
