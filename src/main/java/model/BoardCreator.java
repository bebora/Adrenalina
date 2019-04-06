package model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BoardCreator {
    public Board parseBoard(String filename, int skulls) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        List<List<Tile>> tiles = new ArrayList<>();
        List<Door> doors = new ArrayList<>();
        List<Weapon> weaponsDeck = new ArrayList<>();
        List<Tile> temp;
        try (FileReader input = new FileReader(classLoader.getResource(filename).getFile());
             BufferedReader bufRead = new BufferedReader(input)
        ) {
            String curLine = null;
            int line = 0;
            int section = 0;
            while ((curLine = bufRead.readLine()) != null) {
                if (!curLine.contains(":")) {
                    switch (section) {
                        case 0:
                            temp = new ArrayList<>();
                            for (int i = 0; i < curLine.length(); i++) {
                                if (curLine.charAt(i) == ' ')
                                    temp.add(null);
                                temp.add(new Tile.Builder().
                                        setpos(i,line).
                                        setRoom(Color.initialToColor(curLine.charAt(i))).
                                        setspawn(Character.isUpperCase(curLine.charAt(i))).
                                        build());
                            }
                            line = line + 1;
                            tiles.add(temp);
                            break;
                        case 1:
                            String firstTile = curLine.split(":")[0];
                            String secondTile = curLine.split(":")[1];
                            doors.add(new Door(tiles.get(Character.getNumericValue(firstTile.charAt(0))).
                                    get(Character.getNumericValue(firstTile.charAt(2))),
                                    tiles.get(Character.getNumericValue(secondTile.charAt(0))).
                                    get(Character.getNumericValue(secondTile.charAt(2)))));
                            break;
                    }
                }
                else {
                    section = section +1;
                }
            }
        } catch (IOException e) {
            return null;
        }
        //TODO looping through files for weapons
        return new Board.Builder(skulls).
                setDoors(doors).
                setTiles(tiles).
                setWeapon(weaponsDeck).
                build();
    }
}
