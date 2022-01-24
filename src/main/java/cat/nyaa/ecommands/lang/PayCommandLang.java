package cat.nyaa.ecommands.lang;

import land.melon.lab.simplelanguageloader.components.Text;

public class PayCommandLang {
    public Text help = Text.of(
            "&7Usage:",
            "&7  /pay <amount> <player...>",
            "&7    For example:",
            "&7      cat and dog are two players,",
            "&7      &n/pay 100 cat&r will pay 100 to player cat (cost 100 + service fee)",
            "&7      &n/pay 100 cat dog&r will pay 100 to player cat and 100 to player dog (cost 200 + service fee totally)"
    );
    public Text transferSuccess = Text.of(
            "&7Done! Totally cost: {totallyCost}",
            "&7Receipt id: 0x{receiptId}",
            "&7Successfully transferred {amount} to {receivers}, charged service fee {serviceFee} ({serviceFeePercent}%)."
    );
    public Text transferConfirm = Text.of(
            "&7You are transferring {amount} dollars to {receivers}.",
            "&7Estimate arrive: {estimateArrive} Dollars. Charged service fee {serviceFee} Dollars ({serviceFeePercent}%).",
            "&7Totally cost: {totallyCost} Dollars.",
            "&7Your balance after transfer: {balanceAfterTransfer}",
            "&7Type &b/pay confirm&7 to confirm.",
            "&7Type &c/pay cancel&7 to cancel."
    );
    public Text transferCancelled = Text.of("&7Transfer cancelled.");
    public Text invalidAmount = Text.of("&8&u{amount}&7 is not a valid amount. It should be a positive number.");
    public Text transferFailed = Text.of("&7Transfer failed. It may caused by insufficient balance. Or try again later if you have enough balance.");
    public Text noWaitingForConfirmTransfer = Text.of("&7You have no transfer waiting for your confirm.");
    public Text transferReceiverOffline = Text.of("&7Receiver {receivers} is not online, or you have typed the name incorrectly.");
    public Text transferReceived = Text.of(
            "&7You have received {amount} from {sender}, receipt id: 0x{receiptId}",
            "&7Service fee {serviceFee} Dollars ({serviceFeePercent}%) were automatically charged.",
            "&7{amountArrive} Dollars were added to your account."
    );
    public Text insufficientBalance = Text.of("&7Insufficient balance. You need at least {totallyCost} to transfer {amount} to {receivers}.");

}
