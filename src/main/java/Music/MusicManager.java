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
import net.dv8tion.jda.api.EmbedBuilder;
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
        String patternPlaylist = "(?:http|https|)(?::\\/\\/|)(?:www.|)(?:youtu\\.be\\/|youtube\\.com(?:\\/embed\\/|\\/v\\/|\\/watch\\?v=|\\/ytscreeningroom\\?v=|\\/feeds\\/api\\/videos\\/|\\/user\\S*[^\\w\\-\\s]|\\S*[^\\w\\-\\s]))([\\w\\-]{12,})[a-z0-9;:@#?&%=+\\/\\$_.-]*";
        Pattern compiledPatternPlaylist = Pattern.compile(patternPlaylist);
        Matcher matcherPlaylist = compiledPatternPlaylist.matcher(songUrl);

        String pattern = "(?<=youtu.be/|watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(songUrl);

        String id = songUrl;

        if(matcherPlaylist.find()){
            id = matcherPlaylist.group();
        } else if (matcher.find()){
            id = matcher.group();
        }

        logger.info("Playing: " + id);

        playerManager.loadItem(id, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                play(event, track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack firstTrack = playlist.getSelectedTrack();

                if (firstTrack == null) {
                    firstTrack = playlist.getTracks().get(0);
                }

                for (AudioTrack track : playlist.getTracks()) {
                    trackScheduler.queue(track, event);
                }

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

    public void skipTrack(CommandEvent event) {
        trackScheduler.nextTrack();
    }

    public void stop(CommandEvent event) {
        event.getChannel().sendMessage("Stopping Song!").queue();
        event.getGuild().getAudioManager().closeAudioConnection();
        trackScheduler.clearQueue();
        player.destroy();
    }

    public void skipToTrack() {

    }

    public void getPlaylist(CommandEvent event) {
        String playlist = "Upcoming Songs: \n ```";
        int i = 0;
        for (AudioTrack track : trackScheduler.getQueue()) {
            i++;
            playlist = playlist + i + ". " + track.getInfo().title + "\n";

            //limit to 30 to avoid discord from being dumb
            if (i > 30) {
                break;
            }
        }

        playlist = playlist + "```";


        event.getChannel().sendMessage(playlist).queue();
    }

    public void skipToTrack(int trackNum) {
        trackScheduler.skipToTrack(trackNum);
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

        trackScheduler.queue(track,event);
    }
}
