package cat.nyaa.ecommands.commands;

import cat.nyaa.ecommands.SpigotLoader;
import cat.nyaa.ecommands.lang.MainLang;
import cat.nyaa.ecommands.utils.Payment;
import cat.nyaa.ecore.EconomyCore;
import land.melon.lab.simplelanguageloader.utils.Pair;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

public class PayCommand implements CommandExecutor {
    private final SpigotLoader pluginInstance;
    private final Map<UUID, Payment> waitingPayments = new HashMap<>();

    public PayCommand(SpigotLoader pluginInstance) {
        this.pluginInstance = Objects.requireNonNull(pluginInstance);
    }

    // Usage:
    // /pay <amount|confirm|cancel> [player...]
    @Override
    public boolean onCommand(@Nonnull CommandSender commandSender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage("Only players can use this command.");
            return true;
        }
        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("help")) {
                player.sendMessage(pluginInstance.getMainLang().payCommand.help.produce());
            } else if (args[0].equalsIgnoreCase("confirm")) {
                if (!waitingPayments.containsKey(player.getUniqueId())) {
                    player.sendMessage(pluginInstance.getMainLang().payCommand.noWaitingForConfirmTransfer.produce());
                    return true;
                } else {
                    var result = waitingPayments.get(player.getUniqueId()).confirm();
                    if (result.isSuccess()) {
                        var receivers = result.getReceipt().getReceiver().stream()
                                .map(uuid -> Bukkit.getOfflinePlayer(uuid).getName()).collect(Collectors.joining(", "));
                        player.sendMessage(pluginInstance.getMainLang().payCommand.transferSuccess.produce(
                                Pair.of("totallyCost", result.getReceipt().getCostTotally()),
                                Pair.of("amount", result.getReceipt().getAmountPerTransaction()),
                                Pair.of("receiver", receivers),
                                Pair.of("serviceFee", result.getReceipt().getFeeTotally()),
                                Pair.of("serviceFeePercent", result.getReceipt().getFeeRate() * 100),
                                Pair.of("receiptId", Long.toHexString(result.getReceipt().getId()))
                        ));
                    } else {
                        player.sendMessage(pluginInstance.getMainLang().payCommand.transferFailed.produce());
                    }

                }
            } else if (args[0].equalsIgnoreCase("cancel")) {
                if (!waitingPayments.containsKey(player.getUniqueId())) {
                    player.sendMessage(pluginInstance.getMainLang().payCommand.noWaitingForConfirmTransfer.produce());
                } else {
                    waitingPayments.remove(player.getUniqueId());
                    player.sendMessage(pluginInstance.getMainLang().payCommand.transferCancelled.produce());
                }
                return true;
            } else {
                return false;
            }
        } else if (args.length >= 2) {
            double amount;
            try {
                amount = Double.parseDouble(args[0]);
            } catch (NumberFormatException e) {
                player.sendMessage(pluginInstance.getMainLang().payCommand.invalidAmount.produce(
                        Pair.of("amount", args[0])
                ));
                return true;
            }
            if (amount <= 0) {
                player.sendMessage(pluginInstance.getMainLang().payCommand.invalidAmount.produce(
                        Pair.of("amount", args[0])
                ));
                return true;
            }

            List<UUID> targets = new ArrayList<>();
            List<String> targetNotOnline = new ArrayList<>();
            for (int i = 1; i < args.length; i++) {
                var target = pluginInstance.getServer().getPlayerExact(args[i]);
                if (target == null)
                    targetNotOnline.add(args[i]);
                else
                    targets.add(target.getUniqueId());
            }
            if (targetNotOnline.size() > 0) {
                player.sendMessage(pluginInstance.getMainLang().payCommand.transferReceiverOffline.produce(
                        Pair.of("receivers", String.join(", ", targetNotOnline))
                ));
                return true;
            }

            var totallyCost = amount * (1 + pluginInstance.getEconomyCore().getTransferFeeRate() * targets.size());
            var receivers = targets.stream().map(uuid -> Bukkit.getOfflinePlayer(uuid).getName())
                    .collect(Collectors.joining(", "));
            if (pluginInstance.getEconomyCore().getPlayerBalance(player.getUniqueId()) < totallyCost) {
                player.sendMessage(pluginInstance.getMainLang().payCommand.insufficientBalance.produce(
                        Pair.of("totallyCost", totallyCost),
                        Pair.of("amount", amount),
                        Pair.of("receivers", receivers)
                ));
                return true;
            }

            var payment = new Payment(player.getUniqueId(), targets,
                    amount * (1 + pluginInstance.getEconomyCore().getTransferFeeRate()), pluginInstance.getEconomyCore());
            waitingPayments.put(player.getUniqueId(), payment);
            player.sendMessage(pluginInstance.getMainLang().payCommand.transferConfirm.produce(
                    Pair.of("amount", amount),
                    Pair.of("receivers", receivers),
                    Pair.of("totallyCost", totallyCost),
                    Pair.of("serviceFee", payment.amount() - amount),
                    Pair.of("serviceFeePercent", pluginInstance.getEconomyCore().getTransferFeeRate() * 100),
                    Pair.of("balanceAfterTransfer", pluginInstance.getEconomyCore().getPlayerBalance(player.getUniqueId()) - totallyCost)
            ));
            return true;
        }
        return false;
    }

}