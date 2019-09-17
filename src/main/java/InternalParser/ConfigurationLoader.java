package InternalParser;

import Constants.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class ConfigurationLoader {
    private static Logger logger = LoggerFactory.getLogger(Configuration.kLoggerName);

    public static void copyTemplateJSON() throws IOException {
        if (new File(getCurrentDir() + File.separator + "config.json").exists()) {
            return;
        }

        InputStream pathToConfig = ConfigurationLoader.class.getClassLoader().getResourceAsStream("config.json");

        if (pathToConfig == null) {
            throw new IOException("Path to config is null!");
        }

        Files.copy(pathToConfig, Paths.get(getCurrentDir() + File.separator + "config.json"), REPLACE_EXISTING);
    }

    public static String getCurrentDir() {
        try {
            return new File(ConfigurationLoader.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getPath();
        } catch (URISyntaxException e) {
            logger.error("Exception: ", e);
            return "";
        }
    }
}
