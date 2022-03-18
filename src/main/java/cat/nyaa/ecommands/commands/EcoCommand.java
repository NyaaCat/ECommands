package cat.nyaa.ecommands.commands;

import cat.nyaa.ecommands.SpigotLoader;
import cat.nyaa.ecommands.utils.Vault;
import land.melon.lab.simplelanguageloader.utils.Pair;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EcoCommand implements TabExecutor {
    private final SpigotLoader pluginInstance;
    private final List<String> operations = Arrays.stream(Operations.values()).map((element) -> element.name().toLowerCase()).collect(Collectors.toList());
    private final String ECO_PERMISSION_NODE = "ecommands.eco";

    public EcoCommand(SpigotLoader pluginInstance) {
        this.pluginInstance = pluginInstance;
    }

    @Override
    public boolean onCommand(@Nonnull CommandSender commandSender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args) {
        if (!commandSender.hasPermission(ECO_PERMISSION_NODE)) {
            commandSender.sendMessage(pluginInstance.getMainLang().commonLang.permissionDenied.produce());
            return true;
        }
        if (args.length < 3) {
            commandSender.sendMessage(
                    pluginInstance.getMainLang().ecoCommand.help.produce(
                            Pair.of("currencyUnit", pluginInstance.getEconomyCore().currencyNamePlural()),
                            Pair.of("systemVaultName", pluginInstance.getEconomyCore().systemVaultName())
                    )
            );
        } else {
            // /eco <transfer|add|remove|set> <amount> <vault> [targetVault]
            Operations operation;
            double amount;
            Vault vault;
            Vault targetVault = null;

            try {
                operation = Operations.valueOf(args[0].toUpperCase());
            } catch (IllegalArgumentException e) {
                commandSender.sendMessage(
                        pluginInstance.getMainLang().ecoCommand.operationInvalid.produce(
                                Pair.of("operation", args[0]),
                                Pair.of("operations", String.join(", ", Arrays.stream(Operations.values()).map(Operations::name).toArray(String[]::new)))
                        )
                );
                return true;
            }

            try {
                var splited = args[1].split(":");
                if (splited.length == 4) {
                    var percent = Double.parseDouble(splited[0]);
                    var min = Double.parseDouble(splited[1]);
                    var max = Double.parseDouble(splited[2]);
                    var vaultReadOnly = Vault.of(splited[3], pluginInstance.getEconomyCore());
                    var expect = vaultReadOnly.balance() * percent;
                    amount = Math.max(Math.min(max, expect), min);
                } else {
                    amount = Double.parseDouble(args[1]);
                }
            } catch (IllegalArgumentException e) {
                commandSender.sendMessage(
                        pluginInstance.getMainLang().ecoCommand.invalidAmount.produce(
                                Pair.of("amount", args[1])
                        )
                );
                return true;
            }

            try {
                vault = Vault.of(args[2].toUpperCase(), pluginInstance.getEconomyCore());
            } catch (Exception e) {
                commandSender.sendMessage(pluginInstance.getMainLang().ecoCommand.playerNotExistAbort.produce(
                        Pair.of("player", args[2])
                ));
                return true;
            }

            if (operation == Operations.TRANSFER) {
                try {
                    targetVault = Vault.of(args[3].toUpperCase(), pluginInstance.getEconomyCore());
                } catch (Exception e1) {
                    commandSender.sendMessage(pluginInstance.getMainLang().ecoCommand.playerNotExistAbort.produce(
                            Pair.of("player", args[3])
                    ));
                    return true;
                }
            }

            switch (operation) {
                case TRANSFER -> {
                    if (vault.remove(amount)) {
                        var success = targetVault.add(amount);
                        if (!success) {
                            var rollbackSuccess = vault.add(amount);
                            if (!rollbackSuccess) {
                                var exception = new RuntimeException("Rollback failure: failed to pay " + amount + " to " + targetVault.name + ". Already removed " + amount + " from " + vault.name + " but failed to rollback.");
                                commandSender.sendMessage(
                                        pluginInstance.getMainLang().ecoCommand.exceptionOccurred.produce(
                                                Pair.of("exception", exception.toString())
                                        )
                                );
                                throw exception;
                            }
                        }
                        if (!success) {
                            commandSender.sendMessage(
                                    pluginInstance.getMainLang().ecoCommand.operationFailed.produce(
                                            Pair.of("operation", operation.name().toLowerCase()),
                                            Pair.of("player", vault.name)
                                    )
                            );
                        } else {
                            commandSender.sendMessage(
                                    pluginInstance.getMainLang().ecoCommand.transferSuccess.produce(
                                            Pair.of("amount", amount),
                                            Pair.of("payer", vault.name),
                                            Pair.of("payerBalance", vault.balance()),
                                            Pair.of("receiver", targetVault.name),
                                            Pair.of("receiverBalance", targetVault.balance()),
                                            Pair.of("currencyUnit", pluginInstance.getEconomyCore().currencyNamePlural())
                                    )
                            );
                            if (!vault.isSystemVault) {
                                if (vault.player.isOnline()) {
                                    vault.player.getPlayer().sendMessage(pluginInstance.getMainLang().ecoCommand.balanceRemovedNotice.produce(
                                            Pair.of("operator", commandSender.getName()),
                                            Pair.of("amount", amount),
                                            Pair.of("currencyUnit", pluginInstance.getEconomyCore().currencyNamePlural())
                                    ));
                                }
                            }
                            if (!targetVault.isSystemVault) {
                                if (targetVault.player.isOnline()) {
                                    targetVault.player.getPlayer().sendMessage(pluginInstance.getMainLang().ecoCommand.balanceAddedNotice.produce(
                                            Pair.of("operator", commandSender.getName()),
                                            Pair.of("amount", amount),
                                            Pair.of("currencyUnit", pluginInstance.getEconomyCore().currencyNamePlural())
                                    ));
                                }
                            }
                        }
                    } else {
                        commandSender.sendMessage(
                                pluginInstance.getMainLang().ecoCommand.operationFailed.produce(
                                        Pair.of("operation", operation.name().toLowerCase()),
                                        Pair.of("player", vault.name)
                                )
                        );
                    }
                }
                case ADD -> {
                    var success = vault.add(amount);
                    if (success) {
                        commandSender.sendMessage(
                                pluginInstance.getMainLang().ecoCommand.addBalanceSuccess.produce(
                                        Pair.of("amount", amount),
                                        Pair.of("player", vault.name),
                                        Pair.of("balance", vault.balance()),
                                        Pair.of("currencyUnit", pluginInstance.getEconomyCore().currencyNamePlural())
                                )
                        );
                        if (!vault.isSystemVault) {
                            if (vault.player.isOnline()) {
                                vault.player.getPlayer().sendMessage(pluginInstance.getMainLang().ecoCommand.balanceAddedNotice.produce(
                                        Pair.of("operator", commandSender.getName()),
                                        Pair.of("amount", amount),
                                        Pair.of("currencyUnit", pluginInstance.getEconomyCore().currencyNamePlural())
                                ));
                            }
                        }
                    } else {
                        commandSender.sendMessage(
                                pluginInstance.getMainLang().ecoCommand.operationFailed.produce(
                                        Pair.of("operation", operation.name().toLowerCase()),
                                        Pair.of("player", vault.name)
                                )
                        );
                    }
                }
                case REMOVE -> {
                    var success = vault.remove(amount);
                    if (success) {
                        commandSender.sendMessage(
                                pluginInstance.getMainLang().ecoCommand.removeBalanceSuccess.produce(
                                        Pair.of("amount", amount),
                                        Pair.of("player", vault.name),
                                        Pair.of("balance", vault.balance()),
                                        Pair.of("currencyUnit", pluginInstance.getEconomyCore().currencyNamePlural())
                                )
                        );
                        if (!vault.isSystemVault) {
                            if (vault.player.isOnline()) {
                                vault.player.getPlayer().sendMessage(pluginInstance.getMainLang().ecoCommand.balanceRemovedNotice.produce(
                                        Pair.of("operator", commandSender.getName()),
                                        Pair.of("amount", amount),
                                        Pair.of("currencyUnit", pluginInstance.getEconomyCore().currencyNamePlural())
                                ));
                            }
                        }
                    } else {
                        commandSender.sendMessage(
                                pluginInstance.getMainLang().ecoCommand.operationFailed.produce(
                                        Pair.of("operation", operation.name().toLowerCase()),
                                        Pair.of("player", vault.name)
                                )
                        );
                    }
                }
                case SET -> {
                    if (commandSender instanceof Player && vault.isSystemVault) {
                        commandSender.sendMessage(
                                pluginInstance.getMainLang().ecoCommand.consoleOnly.produce()
                        );
                        break;
                    }
                    var success = vault.set(amount);
                    if (success) {
                        commandSender.sendMessage(
                                pluginInstance.getMainLang().ecoCommand.setBalanceSuccess.produce(
                                        Pair.of("amount", amount),
                                        Pair.of("player", vault.name),
                                        Pair.of("currencyUnit", pluginInstance.getEconomyCore().currencyNamePlural())
                                )
                        );
                    } else {
                        commandSender.sendMessage(
                                pluginInstance.getMainLang().ecoCommand.operationFailed.produce(
                                        Pair.of("operation", operation.name().toLowerCase()),
                                        Pair.of("player", vault.name)
                                )
                        );
                    }
                }
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(@Nonnull CommandSender commandSender, @Nonnull Command command, @Nonnull String s, @Nonnull String[] strings) {
        if (!commandSender.hasPermission(ECO_PERMISSION_NODE)) {
            return null;
        }
        if (strings.length < 2) {
            return operations;
        }
        if (!operations.contains(strings[0])) {
            return null;
        }
        if (strings.length == 2) {
            return List.of("<amount>");
        }
        var vaultList = new ArrayList<>(Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).toList());
        vaultList.add("$system");
        if (strings.length == 3) {
            return vaultList.stream().filter(t->t.toLowerCase().startsWith(strings[2].toLowerCase())).toList();
        }
        if (strings.length == 4 && strings[0].equalsIgnoreCase("transfer")) {
            return vaultList.stream().filter(t->t.toLowerCase().startsWith(strings[3].toLowerCase())).toList();
        }
        return null;
    }

    enum Operations {
        TRANSFER, ADD, REMOVE, SET
    }
}
