package cat.nyaa.ecommands.lang;

import land.melon.lab.simplelanguageloader.components.Text;

public class EcoCommandLang {
    public Text help = Text.of(
            "&7Usage: ",
            "&7    /eco <transfer|add|remove|set> <amount> <vault> [targetVault]",
            "&8    Note: use &u$system&8 to refer to the system vault",
            "&7      For example: ",
            "&7        &n/eco add add 100 $system&7 will add 100{currencyUnit} to the system account.",
            "&7        &n/eco remove 100 $system&7 will deduct 100{currencyUnit}  from the system account.",
            "&7        &n/eco transfer 100 cat $system&7 will transfer 100{currencyUnit}  to {systemVaultName} from cat's vault.",
            "&7        &n/eco set 100 cat&7 will set the balance of cat's vault to 100{currencyUnit} .",
            "&7        &8Note: you can only set the balance of system vault via console."
    );
    public Text consoleOnly = Text.of("&7This command is too dangerous to execute as a player. Please use console instead.");
    public Text invalidAmount = Text.of("&7{amount} is not a valid amount.");
    public Text playerNotExistAbort = Text.of("&7Player {player} does not correspond to any recorded player in the server.");
    public Text setBalanceSuccess = Text.of("&7Successfully set the balance of {player} to {amount}{currencyUnit}.");
    public Text transferSuccess = Text.of("&7Successfully transfer {amount}{currencyUnit} from {payer} to {receiver}. New balance of {payer} is {payerBalance}{currencyUnit} and of {receiver} is {receiverBalance}{currencyUnit}.");
    public Text addBalanceSuccess = Text.of("&7Successfully add {amount}{currencyUnit} to {player}. New balance is {balance}{currencyUnit}.");
    public Text removeBalanceSuccess = Text.of("&7Successfully remove {amount}{currencyUnit} from {player}. New balance is {balance}{currencyUnit}.");
    public Text operationFailed = Text.of("&7Failed to {operation}. It may caused by insufficient/excessive balance of {player}, or other unexpected failures.");
    public Text exceptionOccurred = Text.of("unexpected exception occurred: {exception}");
    public Text operationInvalid = Text.of("&7{operation} is not a valid operation, operations available are {operations}.");
}
