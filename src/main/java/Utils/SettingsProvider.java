package Utils;

import InternalParser.ConfigurationLoader;
import com.jagrosh.jdautilities.command.GuildSettingsManager;
import com.jagrosh.jdautilities.command.GuildSettingsProvider;
import net.dv8tion.jda.api.entities.Guild;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SettingsProvider implements GuildSettingsProvider {
    public Collection<String> getPrefixes(Guild guild) {
        if (!ConfigurationLoader.doesCollectionExist(guild.getId())) {
            return null;
        } else {
            Collection<String> prefixes = new ArrayList<>();

            for (Pair pair : ConfigurationLoader.getDatabase().getCollection(Pair.class)) {
                if (pair.getKey().toString().equalsIgnoreCase(guild.getId())) {
                    prefixes.add(pair.getValue().toString());
                }
            }

            return prefixes;
        }
    }
}
