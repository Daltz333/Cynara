package Commands.MusicCommands;

import Music.MusicManager;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class PlayCommand extends Command {
    MusicManager manager;

    public PlayCommand(MusicManager manager) {
        this.name = "play";
        this.category = new Category("Music");
        this.arguments = "youtubeURL";
        this.help = "Plays the specified track or URL.";

        this.manager = manager;
    }

    @Override
    protected void execute(CommandEvent event) {
        if (event.getMember().getVoiceState() == null) {
            return;
        }

        if (!event.getMember().getVoiceState().inVoiceChannel()) {
            event.reply("You are not currently in a voice channel!");
            return;
        }

        event.getChannel().sendTyping().queue();

        manager.startSong(event.getArgs(), event);
    }
}
