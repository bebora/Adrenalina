package it.polimi.se2019.model.board;

import it.polimi.se2019.model.ammos.Ammo;
import it.polimi.se2019.model.ammos.AmmoCard;
import it.polimi.se2019.model.cards.CardCreator;
import it.polimi.se2019.model.cards.PowerUp;
import it.polimi.se2019.model.cards.Weapon;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public class BoardCreator {
    private BoardCreator() {}
    public static Board parseBoard(String filename, int skulls) {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        List<List<Tile>> tiles = new ArrayList<>();
        List<Door> doors = new ArrayList<>();
        String name = null;
        Deck<Weapon> weaponsDeck;
        Deck<PowerUp> powerUps;
        List<Tile> temp;
        Deck <AmmoCard> ammoCards;
        try (InputStreamReader input = new InputStreamReader(new FileInputStream(classloader.getResource("boards/"+filename).getFile()), StandardCharsets.UTF_8);
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
                if(section == 2){
                    name = curLine.split(":")[1];
                }
            }
        } catch (IOException e) {
            return null;
        }

        // Looping through weapons to add to weapons deck
        weaponsDeck = parseWeapon(classloader, "weapons");
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

    public static Deck parseWeapon(ClassLoader classloader, String weaponPath) {
        List<Weapon> weapons = new ArrayList<>();
        String nameDir = classloader.getResource(weaponPath).getPath();
        File dir = new File(nameDir);
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File weapon : directoryListing) {
                weapons.add(CardCreator.parseWeapon(weapon.getName()));
            }
        }
        Collections.shuffle(weapons);
        return new UnlimitedDeck<>(weapons);
    }

    public static Deck parsePowerUps(ClassLoader classloader, String powerUpsPath) {
        List<PowerUp> powerUps = new ArrayList<>();
        String nameDir = classloader.getResource(powerUpsPath).getPath();
        File dir = new File(nameDir);
        File[] directoryListing = dir.listFiles();
        if (directoryListing != null) {
            for (File powerUp : directoryListing) {
                for (int i = 0; i < 2; i++) {
                    powerUps.add(CardCreator.parsePowerUp(powerUp.getName(), Ammo.BLUE));
                    powerUps.add(CardCreator.parsePowerUp(powerUp.getName(), Ammo.RED));
                    powerUps.add(CardCreator.parsePowerUp(powerUp.getName(), Ammo.YELLOW));
                }
            }
        }
        Collections.shuffle(powerUps);
        return new UnlimitedDeck<>(powerUps);
    }

    public static Deck generateAmmos() {
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
