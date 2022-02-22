package cat.nyaa.ecommands.lang;

import land.melon.lab.simplelanguageloader.components.Text;

public class BalanceCommandLang {
    public Text selfBalance = Text.of("&7Your balance: {balance} Dollars.");
    public Text otherBalance = Text.of("&7{player}'s balance: {balance} Dollars.");
    public Text insufficientPermission = Text.of("&7You could only check the balance of yourself.");
    public Text consoleWithParametersOnly = Text.of(
            "&7Please use command with parameters(/balance <player>) when you execute it in console.",
            "&8Note: you could use $system to refer to {systemVaultName}."
            );
    public Text playerNotExistAbort = Text.of("&7Player {player} does not correspond to any recorded player in the server, please try again.");
}
