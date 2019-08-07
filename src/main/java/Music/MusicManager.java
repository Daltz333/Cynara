package Music;

import Constants.Configuration;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.managers.AudioManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MusicManager {
    private AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
    private AudioPlayer player = playerManager.createPlayer();
    private TrackScheduler trackScheduler = new TrackScheduler(player);

    private Logger logger = LoggerFactory.getLogger(Configuration.kLoggerName);


    public MusicManager() {
        AudioSourceManagers.registerRemoteSources(playerManager);
        player.addListener(trackScheduler);
    }

    public void startSong(String songUrl, CommandEvent event) {
        String pattern = "(?<=youtu.be/|watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(songUrl);

        String id = songUrl;

        if(matcher.find()){
            id = matcher.group();
        }

        System.out.println(id);

        playerManager.loadItem(id, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                event.getChannel().sendMessage("Adding to queue: ``" + track.getInfo().title + "``").queue();

                play(event, track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack firstTrack = playlist.getSelectedTrack();

                if (firstTrack == null) {
                    firstTrack = playlist.getTracks().get(0);
                }

                event.getChannel().sendMessage("Adding to queue: " + firstTrack.getInfo().title + " (first track of playlist " + playlist.getName() + ")").queue();

                play(event, firstTrack);
            }

            @Override
            public void noMatches() {
                event.getChannel().sendMessage("No matches were found for " + songUrl).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                event.getChannel().sendMessage("Could not play: " + exception.getMessage()).queue();
            }
        });
    }

    private void skipTrack(CommandEvent event) {
        trackScheduler.nextTrack();
        event.getChannel().sendMessage("Skipped to next track.").queue();
    }

    private void play(CommandEvent event, AudioTrack track) {
        event.getGuild().getAudioManager().setSendingHandler(new AudioPlayerSendHandler(player));

        //join voice channel, only if bot is not already connected
        if (event.getMember().getVoiceState() == null) {
            event.getChannel().sendMessage("There was an error grabbing voice channels!").queue();
            logger.warn(event.getMember().getVoiceState().toString());
        } else if (!event.getMember().getVoiceState().inVoiceChannel()) {
            event.getChannel().sendMessage("You are currently not in a voice channel!").queue();
        } else {
            event.getGuild().getAudioManager().openAudioConnection(event.getMember().getVoiceState().getChannel());
        }
    }
}
