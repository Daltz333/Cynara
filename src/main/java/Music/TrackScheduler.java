package Music;

import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class TrackScheduler extends AudioEventAdapter {
    private AudioPlayer player;
    private final BlockingQueue<AudioTrack> queue;
    private CommandEvent event;

    public boolean isPlaying = false;

    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
    }

    public void queue(AudioTrack track, CommandEvent event) {
        this.event = event;

        if (!player.startTrack(track, true)) {
            queue.offer(track);
        }
    }

    @Override
    public void onPlayerPause(AudioPlayer player) {
        // Player was paused
    }

    @Override
    public void onPlayerResume(AudioPlayer player) {
        // Player was resumed
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        isPlaying = true;

        event.getChannel().sendMessage("Now playing ``" + track.getInfo().title +"``!").queue();
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        isPlaying = false;

        if (endReason.mayStartNext) {
            //only start if queue is empty
            if(!queue.isEmpty()) {
                nextTrack();
            }
        } else if (endReason == AudioTrackEndReason.FINISHED) {
            // Track finished
        } else {
            //confusion??
        }

        if(!queue.isEmpty()) {
            queue.remove();
        }

        // endReason == FINISHED: A track finished or died by an exception (mayStartNext = true).
        // endReason == LOAD_FAILED: Loading of a track failed (mayStartNext = true).
        // endReason == STOPPED: The player was stopped.
        // endReason == REPLACED: Another track started playing while this had not finished
        // endReason == CLEANUP: Player hasn't been queried for a while, if you want you can put a
        //                       clone of this back to your queue
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {
        // An already playing track threw an exception (track end event will still be received separately)
        player.destroy();
    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        // Audio track has been unable to provide us any audio, might want to just start a new track
        try {
            player.startTrack(queue.poll(100, TimeUnit.MILLISECONDS), false);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void nextTrack() {
        // Start the next track, regardless of if something is already playing or not. In case queue was empty, we are
        // giving null to startTrack, which is a valid argument and will simply stop the player.
        try {
            AudioTrack track = queue.poll(100, TimeUnit.MILLISECONDS);
            player.startTrack(track, false);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<AudioTrack> getQueue() {
        return new ArrayList<>(queue);
    }

    public void skipToTrack(int trackNum) {
        for (int i = 0; i < trackNum - 1; i++) {
            queue.remove();
        }
        nextTrack();
    }

    public void clearQueue() {
        for (int i = 0; i < queue.size(); i++) {
            queue.remove();
        }

        isPlaying = false;
    }

    public boolean isPlaying() {
        return isPlaying;
    }
}