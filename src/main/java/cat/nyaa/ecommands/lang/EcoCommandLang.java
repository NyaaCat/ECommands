package cat.nyaa.ecommands.lang;

import land.melon.lab.simplelanguageloader.components.Text;

public class EcoCommandLang {
    public Text help = Text.of(
            "Usage: ",
            "    /eco system <deposit|withdraw|set> <amount>",
            "      For example: ",
            "        &n/eco system deposit 100&r will deposit 100 to the system account.",
            "        &n/eco system withdraw 100&r will withdraw 100 from the system account.",
            "    /eco player <deposit|withdraw|set> <amount> <players...>",
            "      For example: ",
            "        cat and dog are two players",
            "        &n/eco player deposit 100 cat dog&r will deposit 100 to player cat and dog.",
            "        &n/eco player withdraw 100 cat&r will withdraw 100 from dog.",
            "        &n/eco player set 100 cat&r will set the balance of cat to 100.",
            "Attention: all the command above will NOT change the balance of the player who issue the commands," +
                    "withdraw means add and deposit means subtract."
    );
    public Text consoleOnly = Text.of("This command is too dangerous to execute as a player. Please use console instead.");
    public Text invalidAmount = Text.of("{amount} is not a valid amount.");
    public Text playerNotExistAbort = Text.of("player {player} does not correspond to any recorded player in the server, abort.");
    public Text setPlayerBalanceSuccess = Text.of("Successfully set the balance of {player} to {amount}.");
    public Text setSystemBalanceSuccess = Text.of("Successfully set the system balance to {amount}.");
    public Text depositPlayerSuccess = Text.of("Successfully deposit {amount} to {player}.");
    public Text depositSystemSuccess = Text.of("Successfully deposit {amount} to the system.");
    public Text withdrawPlayerSuccess = Text.of("Successfully withdraw {amount} from {player}.");
    public Text withdrawSystemSuccess = Text.of("Successfully withdraw {amount} from the system.");
    public Text operationFailure = Text.of("Operation failed, please try again and check the console log to see if there has a stake trace(pretty not sure, it depends on the economy provider of the server).");
    public Text operationInvalid = Text.of("{operation} is not a valid operation, operations available are {operations}, please use &n/eco help&r to check the help.");
    public Text tasksDone = Text.of("All operations have been done!");
}
