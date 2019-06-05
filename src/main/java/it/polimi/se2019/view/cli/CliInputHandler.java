package it.polimi.se2019.view.cli;

import it.polimi.se2019.Logger;
import it.polimi.se2019.Priority;
import it.polimi.se2019.model.board.Color;
import it.polimi.se2019.network.EventUpdater;
import it.polimi.se2019.view.*;
import it.polimi.se2019.view.gui.LoginScreen;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
                    eventUpdater.sendWeapon(inSplit[2]);
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
        String singlePlayer;
        boolean error = false;
        for (String p : players) {
            if (p.matches("\\d")) {
                singlePlayer = selectableOptions.getOption(Integer.parseInt(p));
                if (singlePlayer != null)
                    selectedViewPlayers.add(singlePlayer);
            } else {
                CLI.printMessage(wrongInputMessage, "R");
                error = true;
                break;
            }
        }
        if (!error){
            if (selectableOptions.checkForCoherency(selectedViewPlayers))
                eventUpdater.sendPlayers(selectedViewPlayers);
            else
                CLI.printMessage("You did not respect selection limits", "R");
        }
    }

    private void parseTiles(String[] tiles){
        List<ViewTileCoords> selectedCoords = new ArrayList<>();
        int x;
        int y;
        boolean error = false;
        for(String tile: tiles){
            if(tile.matches(".+,.+")) {
                String[] coords = tile.split(",");
                x = Integer.parseInt(coords[0]);
                y = Integer.parseInt(coords[1]);
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
        if(Color.initialToColor(room.charAt(0)) != null)
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
        List<String> actions = view.getSelf().getActions().stream()
                .map(ViewAction::getType)
                .collect(Collectors.toList());
        if(actions.contains(action))
            eventUpdater.sendAction(action);
        else
            CLI.printMessage(wrongInputMessage, "R");
    }

    private void parsePowerUps(String[] powerUps){
        SelectableOptions<ViewPowerUp> selectableOptions = view.getSelectableOptionsWrapper().getSelectablePowerUps();
        List<ViewPowerUp> selectedPowerUps = new ArrayList<>();
        ViewPowerUp singleSelection;
        boolean error = false;
        for(String p: powerUps){
            if(p.matches("\\d")){
                singleSelection = selectableOptions.getOption(Integer.parseInt(p));
                if(singleSelection != null)
                    selectedPowerUps.add(singleSelection);
            }else{
                CLI.printMessage(wrongInputMessage, "R");
                error = true;
                break;
            }
        }
        if(!error){
            if(view.getSelectableOptionsWrapper().getSelectablePowerUps().checkForCoherency(selectedPowerUps))
                eventUpdater.sendPowerUp(selectedPowerUps,false);
            else
                CLI.printMessage("You did not respect selection limits", "R");
        }
    }

    private void connectionChoice(BufferedReader input){
        CLI.printInColor("W","RMI or Socket?\n");
        String in;
        String answer = "RMI";
        String username = "user";
        String standard_pw = "password";
        String gameMode = "NORMAL";
        boolean existingGame = false;
        try{
            answer = input.readLine();
            answer = answer.toUpperCase();
            CLI.printInColor("W","Username: ");
            username = input.readLine();
            Logger.setLogFileSuffix(username);
            CLI.printInColor("W","Password: ");
            standard_pw = input.readLine();
            CLI.printInColor("W","Do you want to re enter an existing match? (y/n)");
            if(input.readLine().equals("y"))
                existingGame = true;
            CLI.printInColor("W","Game mode (NORMAL/DOMINATION)");
            if((in = input.readLine()).equalsIgnoreCase("DOMINATION")){
                gameMode = "DOMINATION";
            }

        }catch (IOException e){
            Logger.log(Priority.ERROR, "Can't read from stdin");
        }
        if(answer.equals("RMI") || answer.equals("SOCKET")) {
            view = new CLI();
            Properties connectionProperties = new Properties();
            FileInputStream fin;
            try{
                fin = new FileInputStream(getClass().getClassLoader().getResource("connection.properties").getPath());
                connectionProperties.load(fin);
            }catch (Exception e){
                Logger.log(Priority.ERROR,e.getMessage());
            }
            view.setupConnection(answer,username,standard_pw,connectionProperties,existingGame,gameMode);
            eventUpdater = view.getEventUpdater();
        }
    }

    private boolean viewChoice(){
        CLI.printInColor("W","GUI or CLI?\n");
        String answer = "CLI";
        try{
            answer = input.readLine();
            answer = answer.toUpperCase();
        }catch (IOException e){
            Logger.log(Priority.ERROR, "Can't read from stdin");
        }
        return answer.equals("CLI");
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