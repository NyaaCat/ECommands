package cat.nyaa.ecommands.lang;

import land.melon.lab.simplelanguageloader.components.Text;

public class PayCommandLang {
    public Text help = Text.of(
            "Usage:",
            "  /pay <amount> <player...>",
            "    For example:",
            "      cat and dog are two players,",
            "      &n/pay 100 cat&r will pay 100 to player cat (cost 100 + service fee)",
            "      &n/pay 100 cat dog&r will pay 100 to player cat and 100 to player dog (cost 200 + service fee totally)"
    );
    public Text transferSuccess = Text.of(
            "Done! Totally cost: {totallyCost}",
            "Successfully transferred {amount} to {receivers}, charged service fee {serviceFee}({serviceFeePercent}%). Receipt id 0x{receiptId}"
    );
    public Text transferConfirm = Text.of(
            "&7Are you sure you want to transfer {amount} to {receivers}?",
            "&7Totally cost: {totallyCost}(Charged service fee {service_fee}({serviceFeePercent}%))",
            "&7balance after transfer: {balanceAfterTransfer}",
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
