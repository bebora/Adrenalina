package it.polimi.se2019.view.cli;

import it.polimi.se2019.Logger;
import it.polimi.se2019.Priority;
import it.polimi.se2019.model.board.Color;
import it.polimi.se2019.network.EventUpdater;
import it.polimi.se2019.view.*;
import it.polimi.se2019.view.gui.LoginScreen;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class CliInputHandler implements Runnable{
    private BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
    private String in = "notQuit";
    private CLI view;
    private EventUpdater eventUpdater;
    private static final String wrongInputMessage = "Wrong input!";
    private String URL;
    private int port;
    private String[] args;

    public CliInputHandler(String[] args){
        this.args = args;
    }

    private void tileInfoMode(BufferedReader input){
        String in = "InitialValue";
        String[] inSplit;
        int requestedX;
        int requestedY;
        CLI.moveCursor(1,AsciiBoard.boardBottomBorder+6);
        while(!in.equals("q")){
            try{
                in = input.readLine();
            }catch (IOException e){
                Logger.log(Priority.ERROR,"Failed to read stdin");
            }
            CLI.clearUntilEndOfLine(AsciiBoard.boardBottomBorder+6,AsciiBoard.boardBottomBorder+8,1);
            if(in.matches("^\\d+(,\\d+)")){
                inSplit = in.split(",");
                CLI.moveCursor(1, AsciiBoard.boardBottomBorder+6);
                CLI.cleanRow();
                requestedX = Math.abs(Integer.parseInt(inSplit[0]));
                requestedY = Math.abs(Integer.parseInt(inSplit[1]));
                AsciiBoard.requestTileInfo(requestedX,requestedY);
            }
            CLI.moveCursor(1, AsciiBoard.boardBottomBorder + 6);
            CLI.cleanRow();
        }
    }

    private void infoWeapon(int i){
        if(i < view.getDisplayedWeapons().size()){
            AsciiWeapon.drawWeaponInfo(i,view.getDisplayedWeapons());
        }else{
            CLI.printMessage("The selected weapon does not exist!","R");
        }
    }

    private void parseSelection(String[] inSplit){
        String[] selectedElements = new String[inSplit.length-2];
        System.arraycopy(inSplit,2,selectedElements,0,inSplit.length-2);
        if (view.getReceivingTypes().contains(inSplit[1])) {
            switch (inSplit[1]) {
                case "PLAYERS":
                    parsePlayers(selectedElements);
                    break;
                case "TILES":
                    parseTiles(selectedElements);
                    break;
                case "ROOM":
                    parseRoom(inSplit[2]);
                    break;
                case "WEAPON":
                    parseWeapon(inSplit[2]);
                    break;
                case "DIRECTION":
                    parseDirection(inSplit[2]);
                    break;
                case "ACTION":
                    parseAction(inSplit[2]);
                    break;
                case "POWERUP":
                    parsePowerUps(selectedElements);
                    break;
                case "EFFECT":
                    parseEffect(inSplit[2]);
                    break;
                default:
                    CLI.printMessage("This is not something you can select!", "R");
                    break;
            }
        }
        else
            CLI.printMessage("This is not something you can select!", "R");
    }

    private void parsePlayers(String[] players) {
        List<String> selectedViewPlayers = new ArrayList<>();
        SelectableOptions<String> selectableOptions = view.getSelectableOptionsWrapper().getSelectablePlayers();
        boolean success;
        success = selectFromOptions(players,selectedViewPlayers,selectableOptions);
        if (success){
            if (selectableOptions.checkForCoherency(selectedViewPlayers))
                eventUpdater.sendPlayers(selectedViewPlayers);
            else
                CLI.printMessage("You did not respect selection limits", "R");
        }
    }

    private void parseWeapon(String weaponIndex){
        String selectedWeapon = null;
        if(weaponIndex.matches("\\d")){
            selectedWeapon = view.getSelectableOptionsWrapper().getSelectableWeapons().getOption(Integer.parseInt(weaponIndex));
        }
        if(selectedWeapon != null){
            eventUpdater.sendWeapon(selectedWeapon);
        }else{
            CLI.printMessage("R","Selected weapon does not exist");
        }

    }

    private void parseEffect(String effectIndex){
        String selectedEffect = null;
        if(effectIndex.matches("\\d")){
            selectedEffect = view.getSelectableOptionsWrapper().getSelectableEffects().getOption(Integer.parseInt(effectIndex));
        }
        if(selectedEffect != null){
            eventUpdater.sendEffect(selectedEffect);
        }else{
            CLI.printMessage("R","Selected effect does not exist");
        }

    }

    private <T> boolean selectFromOptions(String[] toBeParsed, List<T> selected,SelectableOptions<T> selectableOptions){
        T singleParsed;
        for (String p : toBeParsed) {
            if (p.matches("\\d")) {
                singleParsed = selectableOptions.getOption(Integer.parseInt(p));
                if (singleParsed != null)
                    selected.add(singleParsed);
            } else {
                CLI.printMessage(wrongInputMessage, "R");
                return false;
            }
        }
        return true;
    }

    private void parseTiles(String[] tiles){
        List<ViewTileCoords> selectedCoords = new ArrayList<>();
        int x;
        int y;
        boolean error = false;
        for(String tile: tiles){
            if(tile.matches(".+,.+")) {
                String[] coords = tile.split(",");
                x = Integer.parseInt(coords[1]);
                y = Integer.parseInt(coords[0]);
                selectedCoords.add(new ViewTileCoords(x,y));
            }else
            {
                CLI.printMessage(wrongInputMessage,"R");
                error = true;
                break;
            }
        }
        if(!error)
            eventUpdater.sendTiles(selectedCoords);
    }

    private void parseRoom(String room){
        if(view.getSelectableOptionsWrapper().getSelectableRooms().getOptions().contains(room))
            eventUpdater.sendRoom(room);
        else
            CLI.printMessage("Wrong input","R");
    }

    private void parseDirection(String direction){
        List<String> possibleDirections = Arrays.asList("NORTH", "SOUTH", "WEST", "EAST");
        if(possibleDirections.contains(direction))
            eventUpdater.sendDirection(direction);
        else
            CLI.printMessage(wrongInputMessage,"R");
    }

    private void parseAction(String action){
        String selectedOption = null;
        if(action.matches("\\d"))
            selectedOption = view.getSelectableOptionsWrapper().getSelectableActions().getOption(Integer.parseInt(action));
        if(selectedOption != null)
            eventUpdater.sendAction(selectedOption);
        else
            CLI.printMessage(wrongInputMessage, "R");
    }

    private void parsePowerUps(String[] powerUps){
        SelectableOptions<ViewPowerUp> selectableOptions = view.getSelectableOptionsWrapper().getSelectablePowerUps();
        List<ViewPowerUp> selectedPowerUps = new ArrayList<>();
        boolean success;
        success = selectFromOptions(powerUps,selectedPowerUps,selectableOptions);
        if(success){
            if(view.getSelectableOptionsWrapper().getSelectablePowerUps().checkForCoherency(selectedPowerUps))
                eventUpdater.sendPowerUp(selectedPowerUps,false);
            else
                CLI.printMessage("You did not respect selection limits", "R");
        }
    }

    private void connectionChoice(BufferedReader input){
        CLI.printInColor("W","RMI or Socket?\n");
        String answer = "RMI";
        String username = "user";
        String standard_pw = "password";
        String gameMode = "NORMAL";
        boolean existingGame = false;
        try{
            answer = input.readLine();
            answer = answer.toUpperCase();
            switch (answer){
                case "RMI":
                    break;
                case "SOCKET":
                    break;
                default:
                    CLI.printInColor("Y", "Wrong network mode, assuming RMI\n");
                    answer = "RMI";
            }
            CLI.printInColor("W","Username: ");
            username = input.readLine();
            Logger.setLogFileSuffix(username);
            System.setErr(new PrintStream(System.getProperty("user.home")+"/rawlog"+username));
            CLI.printInColor("W","Password: ");
            standard_pw = input.readLine();
            CLI.printInColor("W","Do you want to re enter an existing match? (y/N)");
            switch (input.readLine().toLowerCase()){
                case "y":
                    existingGame = true;
                    break;
                case "n":
                    break;
                default:
                    CLI.printInColor("Y", "Assuming \"n\"\n");
                    break;
            }
            CLI.printInColor("W","Game mode (NORMAL/DOMINATION)");
            switch (input.readLine().toUpperCase()){
                case "NORMAL":
                    break;
                case "DOMINATION":
                    gameMode = "DOMINATION";
                    break;
                default:
                    CLI.printInColor("Y", "Wrong game mode, assuming NORMAL\n");
            }
        }catch (IOException e){
            Logger.log(Priority.ERROR, "Can't read from stdin");
        }
        view = new CLI();
        Properties connectionProperties = new Properties();
        FileInputStream fin;
        try{
            fin = new FileInputStream(getClass().getClassLoader().getResource("connection.properties").getPath());
            connectionProperties.load(fin);
        }catch (Exception e){
            Logger.log(Priority.ERROR, e.getMessage());
        }
        view.setupConnection(answer, username, standard_pw, connectionProperties, existingGame, gameMode);
        eventUpdater = view.getEventUpdater();
    }

    /**
     * @return cli selected or not
     */
    private boolean viewChoice(){
        CLI.printInColor("W","GUI or CLI?\n");
        String answer = "CLI";
        try{
            answer = input.readLine();
            answer = answer.toUpperCase();
        }catch (IOException e){
            Logger.log(Priority.ERROR, "Can't read from stdin");
        }
        boolean ret;
        switch (answer){
            case "CLI":
                ret = true;
                break;
            case "GUI":
                ret = false;
                break;
            default:
                ret = false;
                CLI.printInColor("Y", "Wrong view mode, assuming GUI\n");
        }
        return ret;
    }

    public void run(){
        boolean cliSelected = viewChoice();
        if(cliSelected)
            connectionChoice(input);
        else {
            LoginScreen.main(args);
            return;
        }
        while(view.getStatus()==Status.WAITING) {
            try {
                Thread.sleep(100);
            }
            catch (InterruptedException e) {
                Logger.log(Priority.DEBUG, "Interrupted for " + e.getMessage());
            }
        }
        if(view.getStatus()==Status.PLAYING){
            System.out.println(view.getStatus().name());
        }
        AsciiBoard.setBoard(view.getBoard());
        while(!in.equals("quit")){
            CLI.moveCursor(AsciiBoard.offsetX,AsciiBoard.boardBottomBorder+6);
            CLI.cleanRow();
            try{
                in = input.readLine();
            }catch(IOException e){
                Logger.log(Priority.ERROR,"Can't read from stdin");
            }
            if(in.matches(".+\\s.+")){
                in = in.toUpperCase();
                String[] inSplit = in.split("\\s");
                switch (inSplit[0]){
                    case "SELECT":
                        parseSelection(inSplit);
                        break;
                    case "TILE":
                        tileInfoMode(input);
                        break;
                    case "WEAPON":
                        if(inSplit.length > 2 && inSplit[2].matches("\\d"))
                            infoWeapon(Integer.parseInt(inSplit[2]));
                        else
                            CLI.printMessage("Wrong format", "R");
                        break;
                    default:
                        view.refresh();
                        break;
                }
            }
        }
    }
}