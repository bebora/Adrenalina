package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.File;


public class BoardCreator {
    private BoardCreator() {}
    public static Board parseBoard(String filename, int skulls) {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        List<List<Tile>> tiles = new ArrayList<>();
        List<Door> doors = new ArrayList<>();
        List<Weapon> weaponsDeck;
        List<PowerUp> powerUps;
        List<Tile> temp;
        try (FileReader input = new FileReader(classloader.getResource("boards/"+filename).getFile());
             BufferedReader bufRead = new BufferedReader(input)
        ) {
            String curLine;
            int line = 0;
            int section = 0;
            while ((curLine = bufRead.readLine()) != null) {
                if (curLine.contains(":"))
                    section += 1;
                else if (section == 0) {
                    temp = new ArrayList<>();
                    for (int i = 0; i < curLine.length(); i++) {
                        if (curLine.charAt(i) == '-')
                            temp.add(null);
                        else {
                            temp.add(new Tile.Builder().
                                    setpos(i, line).
                                    setRoom(Color.initialToColor(curLine.charAt(i))).
                                    setspawn(Character.isUpperCase(curLine.charAt(i))).
                                    build());
                        }
                    }
                    line = line + 1;
                    tiles.add(temp);
                }
                else if (section == 1) {
                    String firstTile = curLine.split("\\+")[0];
                    String secondTile = curLine.split("\\+")[1];
                    doors.add(new Door(tiles.get(Character.getNumericValue(firstTile.charAt(0))).
                            get(Character.getNumericValue(firstTile.charAt(2))),
                            tiles.get(Character.getNumericValue(secondTile.charAt(0))).
                            get(Character.getNumericValue(secondTile.charAt(2)))));

                }
            }
        } catch (IOException e) {
            return null;
        }

        // Looping through weapons to add to weapons deck
        weaponsDeck = parseWeapon(classloader, "weapons");
        powerUps = parsePowerUps(classloader, "powerups");



        return new Board.Builder(skulls).
                setDoors(doors).
                setTiles(tiles).
                setWeapon(weaponsDeck).
                setPowerUps(powerUps).build();
    }

    public static List parseWeapon(ClassLoader classloader, String weaponPath) {
        List <Weapon> weapons = new ArrayList<>();
        String nameDir = classloader.getResource(weaponPath).getPath();
        File dir = new File(nameDir);
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File weapon : directoryListing) {
                weapons.add(CardCreator.parseWeapon(weapon.getName()));
            }
        }

        return weapons;
    }

    public static List parsePowerUps(ClassLoader classloader, String powerUpsPath) {
        List<PowerUp> powerUps = new ArrayList<>();
        String nameDir = classloader.getResource(powerUpsPath).getPath();
        File dir = new File(nameDir);
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File powerUp : directoryListing) {
                powerUps.add(CardCreator.parsePowerUp(powerUp.getName(), Ammo.BLUE));
                powerUps.add(CardCreator.parsePowerUp(powerUp.getName(), Ammo.RED));
                powerUps.add(CardCreator.parsePowerUp(powerUp.getName(), Ammo.YELLOW));
            }
        }
        return powerUps;
    }
}
