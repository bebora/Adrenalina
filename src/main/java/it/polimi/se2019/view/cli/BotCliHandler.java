package it.polimi.se2019.view.cli;

import it.polimi.se2019.Logger;
import it.polimi.se2019.Priority;
import it.polimi.se2019.controller.ReceivingType;
import it.polimi.se2019.view.*;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BotCliHandler extends CliInputHandler {
    Random random = new Random();

    public <T> T getRandom(List<T> elements) {
        int rnd = random.nextInt(elements.size());
        Logger.log(Priority.DEBUG, "Chose " + elements.get(rnd));
        return elements.get(rnd);
    }

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

    @Override
    public void run() {
        int rnd = random.nextInt(2);
        String string;
        if (rnd == 1) {
            string = "rmi\nlocalhost\n1099\n\n\n\n\n\n";
        }
        else {
            string = "socket\nlocalhost\n1337\n\n\n\n\n\n";
        }
        Reader inputString = new StringReader(string);
        BufferedReader reader = new BufferedReader(inputString);
        connectionChoice(reader);
        String lastSent;
        while (view.getStatus() == Status.WAITING || view.getStatus() == null) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Logger.log(Priority.DEBUG, "Interrupted for " + e.getMessage());
            }
        }
        if (view.getStatus() == Status.PLAYING) {
            System.out.println(view.getStatus().name());
        }
        AsciiBoard.setBoard(view.getBoard());
        while (!view.getStatus().equals(Status.END)) {
            List<ReceivingType> types = view.getSelectableOptionsWrapper().
                    getAcceptedTypes();
            try {
                selectRandom(types);
            }
            catch (Exception e) {
                Logger.log(Priority.WARNING, "Selection error due to " + e.getMessage());
            }
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Logger.log(Priority.WARNING, "Interrupted!");
            }
        }
        if (view.getStatus().equals(Status.END)) {
            System.out.println("Finished with " + view.getReceivingTypes());
        }
        System.exit(0);
    }

    public void selectRandom(List<ReceivingType> types) {
        if (!types.isEmpty()) {
            SelectableOptionsWrapper wrapper = view.getSelectableOptionsWrapper();
            ReceivingType type = getRandom(types);
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
