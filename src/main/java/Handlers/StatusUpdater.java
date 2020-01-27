package Handlers;

import Constants.Configuration;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimerTask;

/**
 * Handles updating the Discord status periodically
 * because discord will sometimes just cancel the last
 * playing status
 */
public class StatusUpdater extends TimerTask {
    private JDA jda;

    private Logger logger = LoggerFactory.getLogger(Configuration.kLoggerName);

    public StatusUpdater(JDA jda) {
        this.jda = jda;
    }

    @Override
    public void run() {
        jda.getPresence().setActivity(Activity.playing(Configuration.kActivityText));
        logger.info("Activity has been updated!");
    }
}
