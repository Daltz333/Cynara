package InternalParser;

import Constants.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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

        Files.copy(pathToConfig, Paths.get("config.json"), REPLACE_EXISTING);
    }

    public static String getCurrentDir() {
        return System.getProperty("user.dir");

    }
}
