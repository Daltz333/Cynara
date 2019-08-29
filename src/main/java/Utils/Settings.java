package Utils;

import InternalParser.ConfigurationLoader;
import com.jagrosh.jdautilities.command.CommandClient;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;

public class Settings {
    public void loadSettings(JDA jda) {
        for (Guild guild : jda.getGuilds()) {
            if (!ConfigurationLoader.doesCollectionExist(guild.getId())) {
                ConfigurationLoader.getDatabase().createCollection(guild.getId());
            }

        }
    }
}
