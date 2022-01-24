package cat.nyaa.ecommands.lang;

import land.melon.lab.simplelanguageloader.components.Text;

public class EcoCommandLang {
    public Text help = Text.of(
            "&7Usage: ",
            "&7    /eco system <deposit|withdraw|set> <amount>",
            "&7      For example: ",
            "&7        &n/eco system deposit 100&r will deposit 100 to the system account.",
            "&7        &n/eco system withdraw 100&r will withdraw 100 from the system account.",
            "&7    /eco player <deposit|withdraw|set> <amount> <players...>",
            "&7      For example: ",
            "&7        cat and dog are two players",
            "&7        &n/eco player deposit 100 cat dog&r will deposit 100 to player cat and dog.",
            "&7        &n/eco player withdraw 100 cat&r will withdraw 100 from dog.",
            "&7        &n/eco player set 100 cat&r will set the balance of cat to 100.",
            "&7Attention: all the command above will NOT change the balance of the player who issue the commands," +
                    "withdraw means add and deposit means subtract."
    );
    public Text consoleOnly = Text.of("&7This command is too dangerous to execute as a player. Please use console instead.");
    public Text invalidAmount = Text.of("&7{amount} is not a valid amount.");
    public Text playerNotExistAbort = Text.of("&7Player {player} does not correspond to any recorded player in the server, abort.");
    public Text setPlayerBalanceSuccess = Text.of("&7Successfully set the balance of {player} to {amount}.");
    public Text setSystemBalanceSuccess = Text.of("&7Successfully set the system balance to {amount}.");
    public Text depositPlayerSuccess = Text.of("&7Successfully deposit {amount} to {player}.");
    public Text depositSystemSuccess = Text.of("&7Successfully deposit {amount} to the system.");
    public Text withdrawPlayerSuccess = Text.of("&7Successfully withdraw {amount} from {player}.");
    public Text withdrawSystemSuccess = Text.of("&7Successfully withdraw {amount} from the system.");
    public Text operationFailure = Text.of("&7Operation failed, please try again and check the console log to see if there has a stake trace(pretty not sure, it depends on the economy provider of the server).");
    public Text operationInvalid = Text.of("&7{operation} is not a valid operation, operations available are {operations}, please use &n/eco help&r to check the help.");
    public Text systemBalance = Text.of("&7System balance: {amount} Dollars.");
    public Text tasksDone = Text.of("&7All operations have been done!");
}
