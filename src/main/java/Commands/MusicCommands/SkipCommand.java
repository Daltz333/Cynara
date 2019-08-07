package Commands.MusicCommands;

import Music.MusicManager;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class SkipCommand extends Command {
    MusicManager manager;

    public SkipCommand(MusicManager manager) {
        this.name = "skip";
        this.category = new Command.Category("Music");
        this.arguments = "none";
        this.help = "Skips the currently playing track.";

        this.manager = manager;
    }

    @Override
    protected void execute(CommandEvent event) {
        event.getChannel().sendTyping().queue();
        manager.skipTrack(event);
    }
}
