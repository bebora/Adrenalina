package it.polimi.se2019;

import java.io.FileInputStream;
import java.util.Properties;

public class GameProperties extends Properties {
    private static GameProperties instance = null;

    private GameProperties() {
    }

    public static GameProperties getInstance() {
        if (instance == null) {
            try {
                instance = new GameProperties();
                FileInputStream in = new FileInputStream("./game.properties");
                instance.load(in);
                in.close();
            } catch (Exception e) {
                Logger.log(Priority.DEBUG,e.toString());
                try {
                    FileInputStream in = new FileInputStream(GameProperties.class.getClassLoader().getResource("game.properties").getPath());
                    instance.load(in);
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
}