import java.util.Map;
import java.util.HashMap;

public class ConsoleCommand {

    public String command;
    public String help;
    public CommandCallback callback;

    public ConsoleCommand(String command, String help, CommandCallback callback) {
        this.command = command;
        this.help = help;
        this.callback = callback;
    }

    public abstract static class CommandCallback {

        public Map<String, String> args = new HashMap<String, String>();

        abstract public String onInvoke();

        abstract public boolean matchesCommand(String candidiate);

    }
    
}