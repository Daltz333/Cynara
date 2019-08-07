package Commands.MusicCommands;

import Music.MusicManager;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class SkipToTrackCommand extends Command{
    MusicManager manager;

    public SkipToTrackCommand(MusicManager manager) {
        this.name = "skiptotrack";
        this.category = new Command.Category("Music");
        this.arguments = "trackNum";
        this.help = "Skips to the specified track index.";

        this.manager = manager;
    }

    @Override
    protected void execute(CommandEvent event) {
        try {
            int trackNum = Integer.parseInt(event.getArgs());
            manager.skipToTrack(trackNum);
        } catch (NumberFormatException e) {
            event.reply("Invalid Argument!");
        }
    }
}
