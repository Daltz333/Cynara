package Utils;

import com.jagrosh.jdautilities.command.GuildSettingsManager;
import net.dv8tion.jda.api.entities.Guild;

import javax.annotation.Nullable;

public class SettingsManager implements GuildSettingsManager {
    @Nullable
    @Override
    public Object getSettings(Guild guild) {
        return null;
    }

    @Override
    public void init() {

    }

    @Override
    public void shutdown() {

    }
}
