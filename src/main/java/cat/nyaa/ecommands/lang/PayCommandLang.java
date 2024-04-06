package cat.nyaa.ecommands.lang;

import land.melon.lab.simplelanguageloader.components.Text;

public class PayCommandLang {
    public Text help = Text.of(
            "&7Usage:",
            "&7  /pay <amount> <player...>",
            "&7    For example:",
            "&7      cat and dog are two players,",
            "&7      &n/pay 100 cat&7 will pay 100 to player cat (arrival &n100 - service fee&r&7)",
            "&7      &n/pay 100 cat dog&7 will pay 100 to player cat and 100 to player dog (cost 200 totally)"
    );
    public Text transferSuccess = Text.of(
            "&#79a15fDone! &7Receipt id: &#5f7fa10x{receiptId}",
            "&7Successfully transferred {amount} to {receivers}, charged service fee {serviceFee} ({serviceFeePercent}%)."
    );
    public Text transferConfirm = Text.of(
            "&7You are transferring {amount}{currencyUnit} to {receivers}.",
            "&7Estimate arrive: {estimateArrive}{currencyUnit}. Charged service fee {serviceFee}{currencyUnit} ({serviceFeePercent}%).",
            "&7Your balance after transfer: {balanceAfterTransfer}"
    );
    public Text transferConfirmButton = Text.of("&7Do you want to continue&8?  {confirmButton} {cancelButton}");
    public Text confirmButtonText = Text.of("&7[&#79a15fYes&7]");
    public Text confirmButtonHoverText = Text.of("&#79a15f/pay confirm");
    public Text cancelButtonText = Text.of("&7[&#a15f5fNo&7]");
    public Text cancelButtonHoverText = Text.of("&#a15f5f/pay cancel");
    public Text transferCancelled = Text.of("&7Transfer cancelled.");
    public Text invalidAmount = Text.of("&8&u{amount}&7 is not a valid amount. It should be a positive number.");
    public Text transferFailed = Text.of("&7Transfer failed. It may caused by insufficient balance. Or try again later if you have enough balance.");
    public Text noWaitingForConfirmTransfer = Text.of("&7You have no transfer waiting for confirm.");
    public Text transferReceiverNotFound = Text.of("&7Receiver {receivers} was not found, you may check the name spelling");
    public Text transferReceived = Text.of(
            "&7You have received {amount} from {sender}, receipt id: 0x{receiptId}",
            "&7Service fee {serviceFee}{currencyUnit} ({serviceFeePercent}%) were automatically charged.",
            "&7{amountArrive}{currencyUnit} were added to your account."
    );
    public Text insufficientBalance = Text.of("&7Insufficient balance. You need at least {totallyCost} to transfer {amount} to {receivers}.");
}