package cat.nyaa.ecommands.commands;

import cat.nyaa.ecommands.SpigotLoader;
import cat.nyaa.ecommands.utils.Vault;
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
        if (args.length == 0) {
            if(commandSender instanceof ConsoleCommandSender){
                commandSender.sendMessage(
                        pluginInstance.getMainLang().balanceCommand.consoleWithParametersOnly.produce(
                                Pair.of("systemVaultName",pluginInstance.getEconomyCore().systemVaultName())
                        )
                );
            }else{
                commandSender.sendMessage(pluginInstance.getMainLang().balanceCommand.selfBalance.produce(
                        Pair.of("balance", pluginInstance.getEconomyCore().getPlayerBalance(((Player)commandSender).getUniqueId())
                        )));
            }
        } else {
            if (args[0].equalsIgnoreCase(commandSender.getName()) && !commandSender.hasPermission("ecommands.balance.other")) {
                commandSender.sendMessage(pluginInstance.getMainLang().balanceCommand.insufficientPermission.produce());
                return true;
            }
            Vault vault;
            try{
                vault = Vault.of(args[0], pluginInstance.getEconomyCore());
            }catch (Exception e){
                commandSender.sendMessage(pluginInstance.getMainLang().balanceCommand.playerNotExistAbort.produce(
                        Pair.of("player", args[0])
                ));
                return true;
            }
            commandSender.sendMessage(pluginInstance.getMainLang().balanceCommand.otherBalance.produce(
                    Pair.of("player", vault.name),
                    Pair.of("balance", vault.balance())
            ));
        }
        return true;
    }
}
