package it.polimi.se2019.model.board;

import it.polimi.se2019.Logger;
import it.polimi.se2019.Priority;
import it.polimi.se2019.model.ammos.Ammo;
import it.polimi.se2019.model.ammos.AmmoCard;
import it.polimi.se2019.model.cards.CardCreator;
import it.polimi.se2019.model.cards.PowerUp;
import it.polimi.se2019.model.cards.Weapon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Parser used for reading configuration files for a Board.
 * It also parses PowerUps and Weapons and AmmoCards using the related Parsers, to create a Match-ready Board.
 *
 */
public class BoardCreator {
    /**
     * Hide the public constructor
     */
    private BoardCreator() {}
    /**
     * It parses a board in the format .btlb.
     * It adds to the decks of the Board the parsed weapons and powerUps and AmmoCards.
     * @param filename name of the board to parse
     * @param skulls number of skulls to use
     * @return a Match-ready board
     */
    public static Board parseBoard(String filename, int skulls) {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        List<List<Tile>> tiles = new ArrayList<>();
        List<Door> doors = new ArrayList<>();
        String name = null;
        Deck<Weapon> weaponsDeck;
        Deck<PowerUp> powerUps;
        List<Tile> temp;
        Deck<AmmoCard> ammoCards;
        try (InputStream input = classloader.getResourceAsStream("boards/"+filename);
             BufferedReader bufRead = new BufferedReader(new InputStreamReader(input));
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
                                    setPos(i, line).
                                    setRoom(Color.initialToColor(curLine.charAt(i))).
                                    setSpawn(Character.isUpperCase(curLine.charAt(i))).
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
                if(section == 2){
                    name = curLine.split(":")[1];
                }
            }
        } catch (IOException e) {
            return null;
        }

        // Looping through weapons to add to weapons deck
        weaponsDeck = parseWeapons(classloader, "weapons");
        powerUps = parsePowerUps(classloader, "powerups");
        ammoCards = generateAmmos();


        return new Board.Builder(skulls).
                setDoors(doors).
                setTiles(tiles).
                setWeapon(weaponsDeck).
                setPowerUps(powerUps).
                setAmmoCards(ammoCards).
                setName(name).
                build();
    }

    /**
     * Retrieves the index file of a folder containing one, avoiding the Java restriction of listing files in directories.
     * @param folder
     * @return list of the files in the directory, listed in the the index.
     */
    public static List<String> listIndex(ClassLoader classLoader, String folder) {
        InputStream is = classLoader.getResourceAsStream(folder+"/index");
        BufferedReader r = new BufferedReader(new InputStreamReader(is));
        List<String> files = new ArrayList<>();
        String line;
        try {
            while ((line= r.readLine()) != null) {
                files.add(line);
            }
        }
        catch (IOException e) {
            Logger.log(Priority.ERROR, String.format("Can't read index file for %s, because of %s", folder, e.getMessage()));
        }
        return files;
    }

    /**
     * Parses all the weapons in {@code weaponPath}, adding them to an LimitedDeck.
     * @param classloader
     * @param weaponPath containing the weapons
     * @return a Deck containing weapons.
     */
    public static Deck<Weapon> parseWeapons(ClassLoader classloader, String weaponPath) {
        List<Weapon> weapons = new ArrayList<>();
        List<String> weaponNames = listIndex(classloader, weaponPath);
        for (String weapon : weaponNames)
            weapons.add(CardCreator.parseWeapon(weapon));
        Collections.shuffle(weapons);
        weapons.removeIf(Objects::isNull);
        return new LimitedDeck<>(weapons);
    }


    /**
     * Parses all the powerUps in {@code powerUpsPath}, adding them to an UnlimitedDeck.
     * @param classloader
     * @param powerUpsPath containing the powerUps
     * @return a Deck containing powerUps.
     */
    public static Deck<PowerUp> parsePowerUps(ClassLoader classloader, String powerUpsPath) {
        List<PowerUp> powerUps = new ArrayList<>();
        List<String> powerUpsNames = listIndex(classloader, powerUpsPath);
        for (String powerUp : powerUpsNames)
            for (int i = 0; i < 2; i++) {
                powerUps.add(CardCreator.parsePowerUp(powerUp, Ammo.BLUE));
                powerUps.add(CardCreator.parsePowerUp(powerUp, Ammo.RED));
                powerUps.add(CardCreator.parsePowerUp(powerUp, Ammo.YELLOW));
            }
        Collections.shuffle(powerUps);
        powerUps.removeIf(Objects::isNull);
        return new UnlimitedDeck<>(powerUps);
    }

    /**
     * Creates ammo generating them using the Adrenalina's game rules directions.
     * @return a deck containing the ammoCards.
     */
    public static Deck<AmmoCard> generateAmmos() {
        List <AmmoCard> ammoCards = new ArrayList<>();
        List <Ammo> ammosColor = new ArrayList<>(Arrays.asList(Ammo.RED, Ammo.BLUE, Ammo.YELLOW));
        // Create ammosCards according to the game mechanics
        for (int i = 0; i < 3; i++) {
            int finalI = i;
            List<Ammo> otherAmmos = ammosColor.stream().
                    filter(a -> !(a.equals(ammosColor.get(finalI)))).
                    collect(Collectors.toList());
            for (int j = 0; j < 3; j++) {
                ammoCards.add(new AmmoCard(otherAmmos.get(0), ammosColor.get(i), ammosColor.get(i)));
                ammoCards.add(new AmmoCard(otherAmmos.get(1), ammosColor.get(i), ammosColor.get(i)));
                if (j >= 1) {
                    ammoCards.add(new AmmoCard(Ammo.POWERUP, ammosColor.get(i), ammosColor.get(i)));
                }
            }
        }
        for (int i = 0; i < 4; i++) {
            ammoCards.add(new AmmoCard(Ammo.POWERUP, Ammo.RED, Ammo.YELLOW));
            ammoCards.add(new AmmoCard(Ammo.POWERUP, Ammo.RED, Ammo.BLUE));
            ammoCards.add(new AmmoCard(Ammo.POWERUP, Ammo.BLUE, Ammo.YELLOW));
        }
        Collections.shuffle(ammoCards);
        return new UnlimitedDeck<>(ammoCards);
    }
}
