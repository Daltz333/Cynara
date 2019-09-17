package Constants;

import InternalParser.ConfigurationLoader;

import java.awt.*;

public class Configuration {
    //JDA constants
    public static final String kOwnerId = "343633819426881537";
    public static final String kBotPrefix = "c!";
    public static final String kActivityText = "with Daltz[c!]";

    //logger constants
    public static final String kLoggerName = "CynaraDev";

    //embedbuilder constants
    public static final String kEmbedFooterText = "Brought to you by Daltz333";
    public static final String kEmbedFooterUrl = "https://avatars2.githubusercontent.com/u/10674555?s=460&v=4.png";
    public static final Color kEmbedColor = Color.GREEN;

    public static final String kDatabaseUrl = "jdbc:sqlite:"+ ConfigurationLoader.getCurrentDir() + "/db/test.db";
}
