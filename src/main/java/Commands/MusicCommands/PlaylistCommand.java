package Commands.MusicCommands;

import Music.MusicManager;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class PlaylistCommand extends Command{
    MusicManager manager;

    public PlaylistCommand(MusicManager manager) {
        this.name = "playlist";
        this.category = new Command.Category("Music");
        this.aliases = new String[]{"queue"};
        this.arguments = "none";
        this.help = "Shows the current track queue.";

        this.manager = manager;
    }

    @Override
    protected void execute(CommandEvent event) {
        manager.getPlaylist(event);
    }
}
