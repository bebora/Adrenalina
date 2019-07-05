package it.polimi.se2019.view.cli;

import it.polimi.se2019.Logger;
import it.polimi.se2019.Priority;
import it.polimi.se2019.Utils;
import it.polimi.se2019.controller.ReceivingType;
import it.polimi.se2019.view.*;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Handles the creation of a fake player that choose randomly between the options that the server propose him.
 * It supports random exits, choosing more items in a list, connecting with both RMI and SOCKET.
 */
public class BotCliHandler extends CliInputHandler {
    Random random = new Random();

    /**
     * Get a random element from a list
     * @param elements list of elements to choose from
     * @param <T> type of the list
     * @return a random element from the list
     */
    public <T> T getRandom(List<T> elements) {
        int rnd = random.nextInt(elements.size());
        Logger.log(Priority.DEBUG, "Chose " + elements.get(rnd));
        return elements.get(rnd);
    }

    /**
     * Get a random list of elements from a selectable option
     * @param selectableOptions options to choose from, parsing constraints
     * @param <T> type of the list
     * @return a random list of element from the list
     */
    public <T> List<T> getRandomList(SelectableOptions<T> selectableOptions) {
        List<T> elements = new ArrayList<>();
        int min = selectableOptions.getMinSelectables();
        int max = selectableOptions.getMaxSelectables();
        Logger.log(Priority.DEBUG,String.format("min is %d , max is %d", min, max));
        int number = random.nextInt(max - min + 1) + min;
        for (int i = 0; i < number; i++) {
            T element = getRandom(selectableOptions.getOptions());
            elements.add(element);
        }
        Logger.log(Priority.DEBUG, "Chose " + elements);
        return elements;
    }

    /**
     * Handles the logging and the random choosing from the bot, and the connection.
     * Debug password to reconnect with a bot is sweng.
     */
    @Override
    public void run() {
        int rnd = random.nextInt(2);
        String string;
        if (rnd == 1) {
            string = "rmi\nlocalhost\n1099\n\nsweng\n\n\n\n";
        }
        else {
            string = "socket\nlocalhost\n1337\n\nsweng\n\n\n\n";
        }
        System.out.print(string);
        Reader inputString = new StringReader(string);
        BufferedReader reader = new BufferedReader(inputString);
        connectionChoice(reader);
        view.setClean(true);
        while (view.getStatus() == Status.WAITING || view.getStatus() == null) {
            Utils.sleepABit(1000);
        }
        if (view.getStatus() == Status.PLAYING) {
            System.out.println(view.getStatus().name());
        }
        AsciiBoard.setBoard(view.getBoard());
        while (!view.getStatus().equals(Status.END)) {
            Utils.sleepABit(100);
            List<ReceivingType> types = view.getSelectableOptionsWrapper().
                    getAcceptedTypes();
            try {
                selectRandom(types);
            }
            catch (Exception e) {
                Logger.log(Priority.WARNING, "Selection error due to " + e.getMessage());
            }
        }
        if (view.getStatus().equals(Status.END)) {
            Logger.log(Priority.DEBUG,"Finished with " + view.getReceivingTypes());
        }
        System.exit(0);
    }

    /**
     * Select a random element, or list of element, between the given types (if not empty), sending it to the server
     * @param types list of types accepted from the backend
     */
    public void selectRandom(List<ReceivingType> types) {
        if (!types.isEmpty()) {
            Logger.log(Priority.DEBUG, "Trying to choose an action");
            Logger.log(Priority.DEBUG,types.stream().map(Enum::toString).collect(Collectors.toList()).toString());
            SelectableOptionsWrapper wrapper = view.getSelectableOptionsWrapper();
            ReceivingType type = getRandom(types);
            if (random.nextInt(100000) == 314) {
                System.exit(0);
            }
            switch (type) {
                case AMMO:
                    String ammo = getRandom((wrapper.getSelectableStringOptions(type).getOptions()));
                    eventUpdater.sendAmmo(ammo);
                    break;
                case DIRECTION:
                    String direction = getRandom((wrapper.getSelectableStringOptions(type).getOptions()));
                    eventUpdater.sendDirection(direction);
                    break;
                case ROOM:
                    String room = getRandom((wrapper.getSelectableStringOptions(type).getOptions()));
                    eventUpdater.sendRoom(room);
                    break;
                case WEAPON:
                    String weapon = getRandom((wrapper.getSelectableStringOptions(type).getOptions()));
                    eventUpdater.sendWeapon(weapon);
                    break;
                case ACTION:
                    String action = getRandom((wrapper.getSelectableStringOptions(type).getOptions()));
                    eventUpdater.sendAction(action);
                    break;
                case EFFECT:
                    String effect = getRandom((wrapper.getSelectableStringOptions(type).getOptions()));
                    eventUpdater.sendEffect(effect);
                    break;
                case PLAYERS:
                    List<String> players = getRandomList(wrapper.getSelectablePlayers());
                    eventUpdater.sendPlayers(players);
                    break;
                case TILES:
                    List<ViewTileCoords> coords = getRandomList(wrapper.getSelectableTileCoords());
                    eventUpdater.sendTiles(coords);
                    break;
                case POWERUP:
                    List<ViewPowerUp> powerUps = getRandomList(wrapper.getSelectablePowerUps());
                    eventUpdater.sendPowerUp(powerUps, false);
                    break;
                case STOP:
                    eventUpdater.sendStop();
            }
        }
    }
}
