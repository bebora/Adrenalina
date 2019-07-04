package it.polimi.se2019;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Singleton class defining the properties of the game.
 * Properties are searched in a file named "game.properties", in the same directory of the jar.
 * If not found, default properties get used.
 */
public class GameProperties extends Properties {
    private static GameProperties instance = null;
    private static String defaultPath = "./game.properties";
    private GameProperties() {
    }

    public static void setDefaultPath(String path) {
        defaultPath = path;
    }

    public static GameProperties getInstance() {
        if (instance == null) {
            try {
                instance = new GameProperties();
                FileInputStream in = new FileInputStream(defaultPath);
                instance.load(in);
                in.close();
            } catch (Exception e) {
                Logger.log(Priority.DEBUG,e.toString());
                try {
                    InputStream in = GameProperties.class.getClassLoader().getResourceAsStream("game.properties");
                    instance.load(in);
                    in.close();
                }
                catch (Exception istance) {
                    Logger.log(Priority.ERROR, "Couldn't find file from resources, for " + e.toString());
                }
            }
        }
        return instance;
    }

    /**
     * Convert a string with "," separator to a list of integers
     * @param string string to convert
     * @return list of integers converted
     */
    public static List<Integer> toList(String string) {
        return Arrays.stream(string.split(",")).map(Integer::parseInt).collect(Collectors.toList());
    }

}