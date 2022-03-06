package cat.nyaa.ecommands.commands;

import cat.nyaa.ecommands.SpigotLoader;
import cat.nyaa.ecommands.utils.Payment;
import land.melon.lab.simplelanguageloader.utils.Pair;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

public class PayCommand implements TabExecutor {
    private final SpigotLoader pluginInstance;
    private final Map<UUID, Payment> waitingPayments = new HashMap<>();
    private final BaseComponent confirmButton;
    private final BaseComponent cancelButton;
    List<String> operations = List.of("cancel", "confirm", "show");

    public PayCommand(SpigotLoader pluginInstance) {
        this.pluginInstance = Objects.requireNonNull(pluginInstance);

        confirmButton = new TextComponent(TextComponent.fromLegacyText(pluginInstance.getMainLang().payCommand.confirmButtonText.produce()));
        confirmButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(TextComponent.fromLegacyText(pluginInstance.getMainLang().payCommand.confirmButtonHoverText.produce()))));
        confirmButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/pay confirm"));
        cancelButton = new TextComponent(TextComponent.fromLegacyText(pluginInstance.getMainLang().payCommand.cancelButtonText.produce()));
        cancelButton.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(TextComponent.fromLegacyText(pluginInstance.getMainLang().payCommand.cancelButtonHoverText.produce()))));
        cancelButton.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/pay cancel"));
    }

    // Usage:
    // /pay <amount|confirm|cancel|show> [player...]
    @Override
    public boolean onCommand(@Nonnull CommandSender commandSender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args) {
        if (!(commandSender instanceof Player player)) {
            commandSender.sendMessage("Only players can use this command.");
            return true;
        }
        if (args.length == 1) {
            switch (args[0].toLowerCase()) {
                case "help" -> {
                    player.sendMessage(pluginInstance.getMainLang().payCommand.help.produce());
                    return true;
                }
                case "confirm" -> {
                    var payment = waitingPayments.remove(player.getUniqueId());
                    if (payment != null) {
                        var result = payment.confirm();
                        if (result.isSuccess()) {
                            var receivers = result.getReceipt().getReceiver().stream()
                                    .map(uuid -> Bukkit.getOfflinePlayer(uuid).getName()).collect(Collectors.joining(", "));
                            player.sendMessage(pluginInstance.getMainLang().payCommand.transferSuccess.produce(
                                    Pair.of("totallyCost", result.getReceipt().getAmountTotally()),
                                    Pair.of("amount", result.getReceipt().getAmountPerTransaction()),
                                    Pair.of("receivers", receivers),
                                    Pair.of("serviceFee", result.getReceipt().getFeeTotally()),
                                    Pair.of("serviceFeePercent", result.getReceipt().getFeeRate() * 100),
                                    Pair.of("currencyUnit", pluginInstance.getEconomyCore().currencyNamePlural()),
                                    Pair.of("receiptId", Long.toHexString(result.getReceipt().getId()))
                            ));
                            result.getReceipt().getReceiver().forEach(uuid -> {
                                var receiver = Bukkit.getPlayer(uuid);
                                if (receiver != null) {
                                    receiver.sendMessage(
                                            pluginInstance.getMainLang().payCommand.transferReceived.produce(
                                                    Pair.of("amount", result.getReceipt().getAmountPerTransaction()),
                                                    Pair.of("sender", player.getName()),
                                                    Pair.of("receiptId", Long.toHexString(result.getReceipt().getId())),
                                                    Pair.of("serviceFee", result.getReceipt().getFeePerTransaction()),
                                                    Pair.of("serviceFeePercent", result.getReceipt().getFeeRate() * 100),
                                                    Pair.of("currencyUnit", pluginInstance.getEconomyCore().currencyNamePlural()),
                                                    Pair.of("amountArrive", result.getReceipt().getAmountArrivePerTransaction())
                                            )
                                    );
                                }
                            });
                            waitingPayments.remove(player.getUniqueId());
                        } else {
                            player.sendMessage(pluginInstance.getMainLang().payCommand.transferFailed.produce());
                        }
                    } else {
                        player.sendMessage(pluginInstance.getMainLang().payCommand.noWaitingForConfirmTransfer.produce());
                    }
                    return true;
                }
                case "cancel" -> {
                    if (!waitingPayments.containsKey(player.getUniqueId())) {
                        player.sendMessage(pluginInstance.getMainLang().payCommand.noWaitingForConfirmTransfer.produce());
                    } else {
                        waitingPayments.remove(player.getUniqueId());
                        player.sendMessage(pluginInstance.getMainLang().payCommand.transferCancelled.produce());
                    }
                    return true;
                }
                case "show" -> {
                    if (!waitingPayments.containsKey(player.getUniqueId())) {
                        player.sendMessage(pluginInstance.getMainLang().payCommand.noWaitingForConfirmTransfer.produce());
                    } else {
                        var payment = waitingPayments.get(player.getUniqueId());
                        sendPaymentCheckMessage(payment, player);
                    }
                    return true;
                }
                default -> {
                    return false;
                }
            }

        } else if (args.length >= 2) {
            double amount;
            try {
                amount = Double.parseDouble(args[0]);
                if (amount <= 0)
                    throw new IllegalArgumentException("Amount must be positive and non 0.");
            } catch (IllegalArgumentException e) {
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

            var totallyCost = amount * targets.size();
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
                    amount, pluginInstance.getEconomyCore());
            waitingPayments.put(player.getUniqueId(), payment);
            sendPaymentCheckMessage(payment, player);
            return true;
        } else {
            return false;
        }
    }

    private void sendPaymentCheckMessage(Payment payment, Player player) {
        var receivers = payment.to().stream().map(uuid -> Bukkit.getOfflinePlayer(uuid).getName())
                .collect(Collectors.joining(", "));
        var amount = payment.amount();
        var serviceFee = payment.amount() * pluginInstance.getEconomyCore().getTransferFeeRate();
        var estimateArrive = payment.amount() - serviceFee;
        var amountTotal = payment.amount() * payment.to().size();
        player.sendMessage(pluginInstance.getMainLang().payCommand.transferConfirm.produce(
                Pair.of("amount", amount),
                Pair.of("receivers", receivers),
                Pair.of("totallyCost", amountTotal),
                Pair.of("estimateArrive", estimateArrive),
                Pair.of("serviceFee", serviceFee),
                Pair.of("serviceFeePercent", pluginInstance.getEconomyCore().getTransferFeeRate() * 100),
                Pair.of("balanceAfterTransfer", pluginInstance.getEconomyCore().getPlayerBalance(payment.from()) - amountTotal),
                Pair.of("currencyUnit", pluginInstance.getEconomyCore().currencyNamePlural())
        ));
        player.spigot().sendMessage(
                pluginInstance.getMainLang().payCommand.transferConfirmButton.produceWithBaseComponent(
                        Pair.of("confirmButton", confirmButton),
                        Pair.of("cancelButton", cancelButton)
                ));
    }

    @Override
    public List<String> onTabComplete(@Nonnull CommandSender commandSender, @Nonnull Command command, @Nonnull String s, @Nonnull String[] strings) {
        if (!(commandSender instanceof Player senderPlayer)) {
            return null;
        }
        var hasWaitingPayment = waitingPayments.containsKey(senderPlayer.getUniqueId());
        var items = new ArrayList<String>();
        if (strings.length == 1) {
            items.add("<amount>");
            if (hasWaitingPayment) {
                items.addAll(operations);
            }
            return items;
        } else if (strings.length > 1 && !operations.contains(strings[0])) {
            Bukkit.getOnlinePlayers().forEach((player) -> items.add(player.getName()));
            return items;
        } else {
            return null;
        }
    }
}
