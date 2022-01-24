package cat.nyaa.ecommands.commands;

import cat.nyaa.ecommands.SpigotLoader;
import land.melon.lab.simplelanguageloader.utils.Pair;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public class EcoCommand implements CommandExecutor {
    private final SpigotLoader pluginInstance;

    public EcoCommand(SpigotLoader pluginInstance) {
        this.pluginInstance = pluginInstance;
    }

    @Override
    public boolean onCommand(@Nonnull CommandSender commandSender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args) {
        if (args.length < 1) {
            return false;
        } else switch (args[0]) {
            case "help" -> {
                commandSender.sendMessage(pluginInstance.getMainLang().ecoCommand.help.produce());
                return true;
            }
            case "player" -> {
                if (args.length < 4)
                    return false;
                double amount;
                try {
                    amount = Double.parseDouble(args[2]);
                } catch (NumberFormatException e) {
                    commandSender.sendMessage(pluginInstance.getMainLang().ecoCommand.invalidAmount.produce(
                            Pair.of("amount", args[2])
                    ));
                    return false;
                }
                try {
                    ecoPlayerTask(amount, TaskType.valueOf(args[1].toUpperCase()), commandSender, args);
                } catch (IllegalArgumentException e) {
                    commandSender.sendMessage(pluginInstance.getMainLang().ecoCommand.operationInvalid.produce(
                            Pair.of("operation", args[1]),
                            Pair.of("Operations", String.join(", ", Arrays.stream(TaskType.values()).map(item -> item.name().toLowerCase()).toArray(String[]::new)))
                    ));
                    return false;
                }
                commandSender.sendMessage(pluginInstance.getMainLang().ecoCommand.tasksDone.produce());
                return true;
            }
            case "system" -> {
                if (args.length < 2) {
                    return false;
                }
                if (args[1].equalsIgnoreCase("balance")) {
                    commandSender.sendMessage(
                            pluginInstance.getMainLang().ecoCommand.systemBalance.produce(
                                    Pair.of("amount", pluginInstance.getEconomyCore().getSystemBalance())
                            )
                    );
                } else {
                    if (args.length < 3)
                        return false;
                    double amount;
                    try {
                        amount = Double.parseDouble(args[2]);
                    } catch (NumberFormatException e) {
                        commandSender.sendMessage(pluginInstance.getMainLang().ecoCommand.invalidAmount.produce(
                                Pair.of("amount", args[2])
                        ));
                        return false;
                    }
                    try {
                        ecoSystemTask(amount, TaskType.valueOf(args[1].toUpperCase()), commandSender);
                    } catch (IllegalArgumentException e) {
                        commandSender.sendMessage(pluginInstance.getMainLang().ecoCommand.operationInvalid.produce(
                                Pair.of("operation", args[1]),
                                Pair.of("operations", String.join(", ", Arrays.stream(TaskType.values()).map(item -> item.name().toLowerCase()).toArray(String[]::new)))
                        ));
                        return false;
                    }
                }
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    private void ecoPlayerTask(double amount, TaskType taskType, CommandSender commandSender, String[] args) {
        for (int i = 3; i < args.length; i++) {
            OfflinePlayer player = getPlayer(args[i]);
            if (!player.hasPlayedBefore()) {
                commandSender.sendMessage(pluginInstance.getMainLang().ecoCommand.playerNotExistAbort.produce(
                        Pair.of("player", args[i])
                ));
                continue;
            }
            switch (taskType) {
                case SET -> {
                    if (pluginInstance.getEconomyCore().setPlayerBalance(player.getUniqueId(), amount))
                        commandSender.sendMessage(pluginInstance.getMainLang().ecoCommand.setPlayerBalanceSuccess.produce(
                                Pair.of("player", player.getName()),
                                Pair.of("amount", amount)
                        ));
                    else
                        commandSender.sendMessage(pluginInstance.getMainLang().ecoCommand.operationFailure.produce());
                }
                case DEPOSIT -> {
                    if (pluginInstance.getEconomyCore().depositPlayer(player.getUniqueId(), amount))
                        commandSender.sendMessage(pluginInstance.getMainLang().ecoCommand.depositPlayerSuccess.produce(
                                Pair.of("player", player.getName()),
                                Pair.of("amount", amount)
                        ));
                    else
                        commandSender.sendMessage(pluginInstance.getMainLang().ecoCommand.operationFailure.produce());
                }
                case WITHDRAW -> {
                    if (pluginInstance.getEconomyCore().withdrawPlayer(player.getUniqueId(), amount))
                        commandSender.sendMessage(pluginInstance.getMainLang().ecoCommand.withdrawPlayerSuccess.produce(
                                Pair.of("player", player.getName()),
                                Pair.of("amount", amount)
                        ));
                    else
                        commandSender.sendMessage(pluginInstance.getMainLang().ecoCommand.operationFailure.produce());
                }
            }
        }
    }

    private void ecoSystemTask(double amount, TaskType taskType, CommandSender commandSender) {
        switch (taskType) {
            case SET -> {
                if (!(commandSender instanceof ConsoleCommandSender)) {
                    commandSender.sendMessage(pluginInstance.getMainLang().ecoCommand.consoleOnly.produce());
                } else {
                    if (pluginInstance.getEconomyCore().setSystemBalance(amount))
                        commandSender.sendMessage(pluginInstance.getMainLang().ecoCommand.setSystemBalanceSuccess.produce(
                                Pair.of("amount", amount)
                        ));
                    else
                        commandSender.sendMessage(pluginInstance.getMainLang().ecoCommand.operationFailure.produce());
                }
            }
            case DEPOSIT -> {
                if (pluginInstance.getEconomyCore().depositSystemVault(amount))
                    commandSender.sendMessage(pluginInstance.getMainLang().ecoCommand.depositSystemSuccess.produce(
                            Pair.of("amount", amount)
                    ));
                else
                    commandSender.sendMessage(pluginInstance.getMainLang().ecoCommand.operationFailure.produce());
            }
            case WITHDRAW -> {
                if (pluginInstance.getEconomyCore().withdrawSystemVault(amount))
                    commandSender.sendMessage(pluginInstance.getMainLang().ecoCommand.withdrawSystemSuccess.produce(
                            Pair.of("amount", amount)
                    ));
                else
                    commandSender.sendMessage(pluginInstance.getMainLang().ecoCommand.operationFailure.produce());
            }
        }
    }

    enum TaskType {
        SET, DEPOSIT, WITHDRAW
    }

    private OfflinePlayer getPlayer(UUID uuid) {
        return Bukkit.getOfflinePlayer(uuid);
    }

    @SuppressWarnings("deprecation")
    private OfflinePlayer getPlayer(String name) {
        return Objects.requireNonNullElseGet(Bukkit.getPlayer(name), () -> Bukkit.getOfflinePlayer(name));
        //inevitable deprecated api call
    }
}
