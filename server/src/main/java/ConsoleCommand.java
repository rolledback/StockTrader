public class ConsoleCommand {

    public String command;
    public String help;
    public CommandCallback callback;

    public ConsoleCommand(String command, String help, CommandCallback callback) {
        this.command = command;
        this.help = help;
        this.callback = callback;
    }

    abstract static class CommandCallback {

        abstract public String onInvoke();

    }
    
}