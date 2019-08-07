package Commands.MusicCommands;

import Music.MusicManager;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class StopCommand extends Command{
    MusicManager manager;

    public StopCommand(MusicManager manager) {
        this.name = "stop";
        this.category = new Command.Category("Music");
        this.arguments = "none";
        this.help = "Stops the currently playing track.";

        this.manager = manager;
    }

    @Override
    protected void execute(CommandEvent event) {
        event.getChannel().sendTyping().queue();
        manager.stop(event);
    }
}
