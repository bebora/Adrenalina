package it.polimi.se2019.view.cli;

import it.polimi.se2019.Logger;
import it.polimi.se2019.Priority;
import it.polimi.se2019.controller.ReceivingType;
import it.polimi.se2019.network.EventUpdater;
import it.polimi.se2019.view.*;
import it.polimi.se2019.view.gui.LoginScreen;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Map.entry;

public class CliInputHandler implements Runnable{
    private InputStreamReader inputStreamReader = new InputStreamReader(System.in);
    private BufferedReader input = new BufferedReader(inputStreamReader);
    private String in = "notQuit";
    protected CLI view;
    protected EventUpdater eventUpdater;
    private static final String WRONGINPUT = "Wrong input!";
    private String[] args;
    /**
     * //Default values for connection setup
     */
    private Map<String, String> dv = Map.ofEntries(
            entry("network", "rmi"),
            entry("url", "localhost"),
            entry("rmiport", "1099"),
            entry("socketport", "1337"),
            entry("username", String.format("Player-%05d",new Random().nextInt(99999))),
            entry("pw", String.format("%05d", new Random().nextInt(99999))),
            entry("gamemode", "DOMINATION"),
            entry("existinggame", "n")
    );

    public CliInputHandler() {
        //Default constructor doesn't need to initialize nothing
    }
    public CliInputHandler(String[] args){
        this.args = args;
    }

    private void tileInfoMode(BufferedReader input){
        String tileIn = "InitialValue";
        String[] inSplit;
        int requestedX;
        int requestedY;
        CLI.moveCursor(1,AsciiBoard.boardBottomBorder+6);
        while(!tileIn.equals("q")){
            try{
                tileIn = input.readLine();
            }catch (IOException e){
                Logger.log(Priority.ERROR,"Failed to read stdin");
            }
            CLI.clearUntilEndOfLine(AsciiBoard.boardBottomBorder+6,AsciiBoard.boardBottomBorder+8,1);
            if(tileIn.matches("^\\d+(,\\d+)")){
                inSplit = tileIn.split(",");
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

    private void infoPlayer(String color,boolean spawn){
        List<String> possibleColors = view.getPlayers().stream()
                .map(ViewPlayer::getColor)
                .collect(Collectors.toList());
        if(possibleColors.contains(color)) {
            ViewPlayer requestedPlayer = view.getPlayers().stream()
                    .filter(p -> p.getColor().equalsIgnoreCase(color))
                    .filter(p -> p.getDominationSpawn() == spawn)
                    .findAny().orElse(null);
            if (requestedPlayer != null){
                AsciiPlayer.drawPlayerInfo(requestedPlayer, new ArrayList<>(), requestedPlayer.getUnloadedWeapons());
                view.setDisplayedPlayer(requestedPlayer);
                view.displayTurnInfo();
            }
        }else
            CLI.printMessage("No valid player", "R");
    }

    void parseAmmo(String ammo)  {
        if(view.getSelectableOptionsWrapper().getSelectableAmmos().getOptions().contains(ammo))
            eventUpdater.sendAmmo(ammo);
        else
            CLI.printMessage("Wrong input","R");
    }

    void parseSelection(String[] inSplit){
        String error = "This is not something you can select!";
        String[] selectedElements = new String[inSplit.length-2];
        System.arraycopy(inSplit,2,selectedElements,0,inSplit.length-2);
        if (view.getReceivingTypes().contains(inSplit[1])) {
            switch (inSplit[1]) {
                case "AMMO":
                    parseAmmo(inSplit[2]);
                    break;
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
                case "STOP":
                    if (view.getSelectableOptionsWrapper().getAcceptedTypes().contains(ReceivingType.STOP)) {
                        eventUpdater.sendStop();
                    }
                    else {
                        CLI.printMessage(error, "R");
                    }
                    break;
                default:
                    CLI.printMessage(error, "R");
                    break;
            }
        }
        else
            CLI.printMessage(error, "R");
    }

    void parsePlayers(String[] players) {
        List<String> selectedViewPlayers = new ArrayList<>();
        SelectableOptions<String> selectableOptions = view.getSelectableOptionsWrapper().getSelectablePlayers();
        boolean success;
        success =
                selectFromOptions(players,selectedViewPlayers,selectableOptions);
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
            CLI.printMessage("r","Selected effect does not exist");
        }

    }

    private <T> boolean selectFromOptions(String[] toBeParsed, List<T> selected,SelectableOptions<T> selectableOptions){
        T singleParsed;
        String temp = toBeParsed[0];
        if (temp.matches("\\d") && Integer.parseInt(temp) == 0) {
            return true;
        }
        for (String p : toBeParsed) {
            if (p.matches("\\d")) {
                singleParsed = selectableOptions.getOption(Integer.parseInt(p));
                if (singleParsed != null)
                    selected.add(singleParsed);
            } else {
                CLI.printMessage(WRONGINPUT, "R");
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
                CLI.printMessage(WRONGINPUT,"R");
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
            CLI.printMessage(WRONGINPUT,"R");
    }

    private void parseAction(String action){
        String selectedOption = null;
        if(action.matches("\\d"))
            selectedOption = view.getSelectableOptionsWrapper().getSelectableActions().getOption(Integer.parseInt(action));
        if(selectedOption != null)
            eventUpdater.sendAction(selectedOption);
        else
            CLI.printMessage(WRONGINPUT, "R");
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

    /**
     * Helper method that checks validity of user input
     * @param acceptedOptions options that can be accepted
     * @param bannedOptions options that must be rejected. If null, reject all values not in {@code acceptedOptions}
     * @param defaultChoice option returned if {@code inputChoice} is not valid
     * @param inputChoice option to check
     * @param description description of option name
     * @return {@code inputChoice} is valid
     */
    private String parseOption(List<String> acceptedOptions, List<String> bannedOptions, String defaultChoice, String inputChoice, String description){
        if (acceptedOptions.contains(inputChoice)) {
            return inputChoice;
        }
        else if (bannedOptions != null && !bannedOptions.contains(inputChoice)) {
            return inputChoice;
        }
        else {
            CLI.printInColor("Y", "Invalid "+description+", assuming " + defaultChoice + "\n");
            return defaultChoice;
        }
    }

    /**
     * Asks connection parameters with automatic default values
     * @param input BufferedReader that reads input
     * @return map of options selected by the player, that can be passed at the next invocation of the overloaded method as default values
     */
    Map<String, String> connectionChoice(BufferedReader input) {
        return connectionChoice(input, dv);
    }

    /**
     * Asks parameters to connect with custom default values provided with {@code dv}
     * Asks in order:
     * <li>Connection type (RMI/Socket)</li>
     * <li>Server url</li>
     * <li>Server port</li>
     * <li>Username</li>
     * <li>Password</li>
     * <li>Whether to re enter an existing match</li>
     * <li>Gamemode (Normal/Domination)</li>
     * Then proceeds to connect to the server and get an {@link EventUpdater}
     * @param input BufferedReader that reads input
     * @param dv default values for asked parameters
     * @return map of options selected by the player, that can be passed at the next invocation of this method as default values
     */
    Map<String, String> connectionChoice(BufferedReader input, Map<String, String> dv){
        CLI.printInColor("W","RMI or Socket?\n");
        try{
            String connectionType = parseOption(Arrays.asList("rmi", "socket"), null, dv.get("network"), input.readLine().toLowerCase(), "network mode");
            CLI.printInColor("W","URL: ");
            String url = parseOption(new ArrayList<>(), Arrays.asList(""), dv.get("url"), input.readLine(), "url");
            CLI.printInColor("W","Port: ");
            String rmiport = dv.get("rmiport");
            String socketport = dv.get("socketport");
            if (connectionType.equalsIgnoreCase("RMI")) {
                rmiport = parseOption(new ArrayList<>(), Arrays.asList(""), dv.get("rmiport"), input.readLine(), "RMI port");
            }
            else {
                socketport = parseOption(new ArrayList<>(), Arrays.asList(""), dv.get("socketport"), input.readLine(), "socket port");
            }
            CLI.printInColor("W","Username: ");
            String username = parseOption(new ArrayList<>(), Arrays.asList(""), dv.get("username"), input.readLine(), "username");
            Logger.setLogFileSuffix(username);
            System.setErr(new PrintStream(System.getProperty("user.home")+"/rawlog"+username));
            CLI.printInColor("W","Password: ");
            String pw = parseOption(new ArrayList<>(), Arrays.asList(""), dv.get("pw"), input.readLine(), "password");
            CLI.printInColor("W","Do you want to re enter an existing match? (y/n)");
            String existingGame = parseOption(Arrays.asList("y", "n"), null, dv.get("existinggame"), input.readLine().toLowerCase(), "existing game");
            if (existingGame.equals("y")) existingGame = "true";
            else if (existingGame.equals("n")) existingGame = "false";
            CLI.printInColor("W", "Game mode (NORMAL/DOMINATION)");
            String gameMode = parseOption(Arrays.asList("NORMAL", "DOMINATION"), null, dv.get("gamemode"), input.readLine().toUpperCase(), "gamemode");

            view = new CLI();
            Properties connectionProperties = new Properties();
            connectionProperties.setProperty("url", url);
            connectionProperties.setProperty("port", connectionType.equalsIgnoreCase("rmi") ? rmiport : socketport);
            view.setGameMode(gameMode);
            int retryTime = 5; //seconds
            while (!view.setupConnection(connectionType, username, pw, connectionProperties, Boolean.parseBoolean(existingGame), gameMode)){
                Logger.log(Priority.ERROR, String.format("Can't connect to server! Retrying in %d seconds", retryTime));
                try {
                    Thread.sleep(retryTime*1000);
                }
                catch (InterruptedException e) {
                    Logger.log(Priority.ERROR, "Interrupted ");
                }
            }
            Logger.log(Priority.DEBUG, "Connected succesfully to server");
            eventUpdater = view.getEventUpdater();
            Map<String, String> customValues = Map.ofEntries(
                    entry("network", connectionType),
                    entry("url", url),
                    entry("rmiport", rmiport),
                    entry("socketport", socketport),
                    entry("username", username),
                    entry("pw", pw),
                    entry("gamemode", gameMode),
                    entry("existinggame", existingGame)
            );
            return customValues;
        }catch (IOException e) {
            Logger.log(Priority.ERROR, "Can't read from stdin, aborting connection setup");
            return dv;
        }
    }

    /**
     * @return cli selected or not, asking to the player via stdin
     */
    private boolean viewChoice(){
        try {
            System.in.read(new byte[System.in.available()]);
        }
        catch (IOException e) {
            Logger.log(Priority.ERROR, "IOException reading stdin");
        }
        CLI.printInColor("W","GUI or CLI?\n");
        final String DEFAULTVIEW = "CLI";
        try{
            String choice = parseOption(Arrays.asList("CLI", "GUI"), null, DEFAULTVIEW, input.readLine().toUpperCase(), "view mode");
            return choice.equals("CLI");
        }catch (IOException e){
            Logger.log(Priority.ERROR, "Can't read from stdin");
            return true;
        }
    }

    private void handleInput() {
        while(!in.equals("quit") && !view.getStatus().equals(Status.END)){
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
                        try {
                            parseSelection(inSplit);
                        } catch(Exception e) {
                            CLI.printMessage("Wrong choice!", "R");
                        }
                        break;
                    case "TILE":
                        tileInfoMode(input);
                        break;
                    case "PLAYER":
                        infoPlayer(inSplit[1],false);
                        break;
                    case "SPAWN":
                        infoPlayer(inSplit[1],true);
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

    /**
     * Set {@link #dv} with custom values
     * @param defaultValues
     */
    public void setDefaultValues(Map<String, String> defaultValues) {
        dv = defaultValues;
    }

    public void run(){
        boolean cliSelected = viewChoice();
        Map<String, String> selectedValues;
        if(cliSelected)
            selectedValues = connectionChoice(input, dv);
        else {
            LoginScreen.main(args);
            return;
        }
        while(view.getStatus() == null || view.getStatus()==Status.WAITING) {
            try {
                Thread.sleep(100);
            }
            catch (InterruptedException e) {
                Logger.log(Priority.DEBUG, "Interrupted for " + e.getMessage());
            }
        }
        if(view.getStatus()==Status.PLAYING){
            System.out.println(view.getStatus().name());
        } else {
            CliInputHandler cliInputHandler = new CliInputHandler(args);
            cliInputHandler.setDefaultValues(selectedValues);
            Thread start = new Thread(cliInputHandler);
            start.start();
            return;
        }
        AsciiBoard.setBoard(view.getBoard());
        handleInput();
        if (view.getStatus().equals(Status.END)) {
            //Previous match ended, start a new one
            CLI.printInColor("w", "Using previous selected values as default");
            CliInputHandler cliInputHandler = new CliInputHandler(args);
            cliInputHandler.setDefaultValues(selectedValues);
            Thread start = new Thread(cliInputHandler);
            start.start();
            return;
        }
    }
}