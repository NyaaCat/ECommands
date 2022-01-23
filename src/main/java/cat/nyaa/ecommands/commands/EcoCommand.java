package cat.nyaa.ecommands.commands;

import cat.nyaa.ecommands.lang.MainLang;
import cat.nyaa.ecore.EconomyCore;
import land.melon.lab.simplelanguageloader.utils.Pair;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;

public class EcoCommand implements CommandExecutor {
    private final MainLang languageProvider;
    private final JavaPlugin pluginInstance;
    private final EconomyCore economyProvider;

    public EcoCommand(MainLang languageProvider, JavaPlugin pluginInstance, EconomyCore economyProvider) {
        this.languageProvider = languageProvider;
        this.pluginInstance = pluginInstance;
        this.economyProvider = economyProvider;
    }

    @Override
    public boolean onCommand(@Nonnull CommandSender commandSender, @Nonnull Command command, @Nonnull String label, String[] args) {
        if (args.length < 1) {
            return false;
        } else switch (args[0]) {

            case "help" -> {
                commandSender.sendMessage(languageProvider.ecoCommand.help.produce());
                return true;
            }
            case "player" -> {
                if (args.length < 4)
                    return false;
                double amount;
                try {
                    amount = Double.parseDouble(args[3]);
                } catch (NumberFormatException e) {
                    commandSender.sendMessage(languageProvider.ecoCommand.invalidAmount.produce(
                            Pair.of("{amount}", args[3])
                    ));
                    return false;
                }
                try {
                    ecoPlayerTask(amount, TaskType.valueOf(args[1].toUpperCase()), commandSender, args);
                } catch (IllegalArgumentException e) {
                    commandSender.sendMessage(languageProvider.ecoCommand.operationInvalid.produce(
                            Pair.of("{operation}", args[1]),
                            Pair.of("{Operations}", String.join(", ", Arrays.stream(TaskType.values()).map(item -> item.name().toLowerCase()).toArray(String[]::new)))
                    ));
                    return false;
                }
                commandSender.sendMessage(languageProvider.ecoCommand.tasksDone.produce());
                return true;
            }
            case "system" -> {
                if (args.length < 3)
                    return false;
                double amount;
                try {
                    amount = Double.parseDouble(args[3]);
                } catch (NumberFormatException e) {
                    commandSender.sendMessage(languageProvider.ecoCommand.invalidAmount.produce(
                            Pair.of("{amount}", args[3])
                    ));
                    return false;
                }
                ecoSystemTask(amount, TaskType.valueOf(args[1].toUpperCase()), commandSender);
                return true;
            }
            default -> {
                return false;
            }
        }
    }

    private void ecoPlayerTask(double amount, TaskType taskType, CommandSender commandSender, String[] args) {
        for (int i = 2; i < args.length; i++) {
            OfflinePlayer player = getPlayer(args[i]);
            if (!player.hasPlayedBefore()) {
                commandSender.sendMessage(languageProvider.ecoCommand.playerNotExistAbort.produce(
                        Pair.of("{player}", args[i])
                ));
                continue;
            }
            switch (taskType) {
                case SET -> {
                    if (economyProvider.setPlayerBalance(player.getUniqueId(), amount))
                        commandSender.sendMessage(languageProvider.ecoCommand.setPlayerBalanceSuccess.produce(
                                Pair.of("{player}", player.getName()),
                                Pair.of("{amount}", amount)
                        ));
                    else
                        commandSender.sendMessage(languageProvider.ecoCommand.operationFailure.produce());
                }
                case DEPOSIT -> {
                    if (economyProvider.depositPlayer(player.getUniqueId(), amount))
                        commandSender.sendMessage(languageProvider.ecoCommand.depositPlayerSuccess.produce(
                                Pair.of("{player}", player.getName()),
                                Pair.of("{amount}", amount)
                        ));
                    else
                        commandSender.sendMessage(languageProvider.ecoCommand.operationFailure.produce());
                }
                case WITHDRAW -> {
                    if (economyProvider.withdrawPlayer(player.getUniqueId(), amount))
                        commandSender.sendMessage(languageProvider.ecoCommand.withdrawPlayerSuccess.produce(
                                Pair.of("{player}", player.getName()),
                                Pair.of("{amount}", amount)
                        ));
                    else
                        commandSender.sendMessage(languageProvider.ecoCommand.operationFailure.produce());
                }
            }
        }
    }

    private void ecoSystemTask(double amount, TaskType taskType, CommandSender commandSender) {
        switch (taskType) {
            case SET -> {
                if (!(commandSender instanceof ConsoleCommandSender)) {
                    commandSender.sendMessage(languageProvider.ecoCommand.consoleOnly.produce());
                } else {
                    if (economyProvider.setSystemBalance(amount))
                        commandSender.sendMessage(languageProvider.ecoCommand.setSystemBalanceSuccess.produce(
                                Pair.of("{amount}", amount)
                        ));
                    else
                        commandSender.sendMessage(languageProvider.ecoCommand.operationFailure.produce());
                }
            }
            case DEPOSIT -> {
                if (economyProvider.depositSystemVault(amount))
                    commandSender.sendMessage(languageProvider.ecoCommand.depositSystemSuccess.produce(
                            Pair.of("{amount}", amount)
                    ));
                else
                    commandSender.sendMessage(languageProvider.ecoCommand.operationFailure.produce());
            }
            case WITHDRAW -> {
                if (economyProvider.withdrawSystemVault(amount))
                    commandSender.sendMessage(languageProvider.ecoCommand.withdrawSystemSuccess.produce(
                            Pair.of("{amount}", amount)
                    ));
                else
                    commandSender.sendMessage(languageProvider.ecoCommand.operationFailure.produce());
            }
        }
    }

    enum TaskType {
        SET, DEPOSIT, WITHDRAW
    }

    private OfflinePlayer getPlayer(UUID uuid) {
        return Bukkit.getOfflinePlayer(uuid);
    }

    private OfflinePlayer getPlayer(String name) {
        return Objects.requireNonNullElseGet(Bukkit.getPlayer(name), () -> Bukkit.getOfflinePlayer(name));
        //inevitable deprecated api call
    }
}
