package cat.nyaa.ecommands.utils;

import land.melon.lab.simplelanguageloader.components.Text;

public class Lang {
    public Text help = Text.of(
            "&7/pay: transfer money to other player(s).",
            "&7 Usage: &f/pay <amount> <player...>"
    );
    public Text transferSuccess = Text.of(
            "Done! Totally cost: {totallyCost}",
            "Successfully transferred {amount} to {receivers}, charged service fee {serviceFee}({serviceFeePercent}%). Receipt id 0x{receiptId}"
    );
    public Text transferConfirm = Text.of(
            "&7Are you sure you want to transfer {amount} to {receivers}?",
            "&7Totally cost: {totallyCost}(Charged service fee {service_fee}({serviceFeePercent}%))",
            "balance after transfer: {balanceAfterTransfer}",
            "&7Type &b/pay confirm&7 to confirm.",
            "&7Type &c/pay cancel&7 to cancel."
    );
    public Text transferCancelled = Text.of("&7Transfer cancelled.");
    public Text invalidAmount = Text.of("&c{amount} is not a valid amount.");
    public Text transferFailed = Text.of("&7Transfer failed. It may caused by insufficient balance. Or try again later if you have enough balance.");
    public Text noWaitingForConfirmTransfer = Text.of("&7You have no transfer waiting for your confirm.");
    public Text transferReceiverOffline = Text.of("&7Receiver {receivers} is not online, or you have typed the name incorrectly.");
    public Text insufficientBalance = Text.of("&cInsufficient balance. You need at least {totallyCost} to transfer {amount} to {receivers}.");
}
