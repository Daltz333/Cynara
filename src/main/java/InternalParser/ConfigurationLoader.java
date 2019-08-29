package InternalParser;

import Constants.Configuration;
import io.jsondb.JsonDBTemplate;
import io.jsondb.crypto.DefaultAESCBCCipher;
import io.jsondb.crypto.ICipher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class ConfigurationLoader {
    private static Logger logger = LoggerFactory.getLogger(Configuration.kLoggerName);
    private static JsonDBTemplate jsonDBTemplate = null;

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
            logger.info("Using directory: " +  new File(ConfigurationLoader.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getPath());
            return new File(ConfigurationLoader.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile().getPath();
        } catch (URISyntaxException e) {
            logger.error("Exception: ", e);
            return "";
        }
    }

    public static void createDb() {
        //Actual location on disk for database files, process should have read-write permissions to this folder
        String dbFilesLocation = "<path on disk to save or read .json files from>";

        //Java package name where POJO's are present
        String baseScanPackage = "";

        //Optionally a Cipher object if you need Encryption
        ICipher cipher = null;
        try {
            cipher = new DefaultAESCBCCipher("1r8+24pibarAWgS85/Heeg==");
        } catch (GeneralSecurityException e) {
            logger.warn("Error ", e);
        }

        JsonDBTemplate jsonDBTemplate = new JsonDBTemplate(dbFilesLocation, baseScanPackage, cipher);
    }

    public static JsonDBTemplate getDatabase() {
        return jsonDBTemplate;
    }

    public static boolean doesCollectionExist(String name) {
        for (String data : jsonDBTemplate.getCollectionNames()) {
            if (data.equalsIgnoreCase(name)) {
                return true;
            }
        }

        return false;
    }
}
