package cat.nyaa.ecommands.lang;

import land.melon.lab.simplelanguageloader.components.Text;

public class BalanceCommandLang {
    public Text selfBalance = Text.of("Your balance: {balance} Dollars.");
    public Text otherBalance = Text.of("{player}'s balance: {balance} Dollars.");
    public Text insufficientPermission = Text.of("You could only check the balance of yourself.");
    public Text playerNotExistAbort = Text.of("Player {player} does not correspond to any recorded player in the server, please try again.");
}
