package Commands.SysAdminCommands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

import java.util.List;

public class SetPrefixCommand extends Command {
    public SetPrefixCommand() {
        this.category = new Category("Admin");
        this.name = "setprefix";
        this.help = "Sets the server prefix.";
        this.arguments = "prefix";
    }

    @Override
    protected void execute(CommandEvent event) {

    }
}
