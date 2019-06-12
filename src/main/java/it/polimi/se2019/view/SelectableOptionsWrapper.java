package it.polimi.se2019.view;

import it.polimi.se2019.controller.AcceptableTypes;
import it.polimi.se2019.controller.ReceivingType;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.actions.Action;
import it.polimi.se2019.model.board.Color;
import it.polimi.se2019.model.cards.Weapon;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SelectableOptionsWrapper implements Serializable {
    private SelectableOptions<String> selectableActions;
    private SelectableOptions<String> selectableEffects;
    private SelectableOptions<String> selectablePlayers;
    private SelectableOptions<ViewPowerUp> selectablePowerUps;
    private SelectableOptions<String> selectableRooms;
    private SelectableOptions<ViewTileCoords> selectableTileCoords;
    private SelectableOptions<String> selectableWeapons;
    private SelectableOptions<String> selectableDirections;
    private SelectableOptions<String> selectableAmmos;
    private List<ReceivingType> acceptedTypes;
    private String stopPrompt;

    public String getStopPrompt() {
        return stopPrompt;
    }

    public SelectableOptions<String> getSelectableEffects() {
        return selectableEffects;
    }

    public List<ReceivingType> getAcceptedTypes() {
        return acceptedTypes;
    }

    public SelectableOptions<String> getSelectableActions() {
        return selectableActions;
    }

    public SelectableOptions<String> getSelectabeEffects() {
        return selectableEffects;
    }

    public SelectableOptions<String> getSelectablePlayers() {
        return selectablePlayers;
    }

    public SelectableOptions<ViewPowerUp> getSelectablePowerUps() {
        return selectablePowerUps;
    }

    public SelectableOptions<String> getSelectableRooms() {
        return selectableRooms;
    }

    public SelectableOptions<ViewTileCoords> getSelectableTileCoords() {
        return selectableTileCoords;
    }

    public SelectableOptions<String> getSelectableWeapons() {
        return selectableWeapons;
    }

    public SelectableOptionsWrapper(AcceptableTypes acceptableTypes) {
        acceptedTypes = acceptableTypes.getAcceptedTypes();
        for (ReceivingType receivingType : acceptableTypes.getAcceptedTypes()) {
            switch (receivingType) {
                case ACTION:
                    selectableActions = new SelectableOptions<>(acceptableTypes.getSelectableActions());
                    selectableActions.setOptions(acceptableTypes.
                            getSelectableActions().
                            getOptions().
                            stream().
                            map(Action::toString).
                            collect(Collectors.toCollection(ArrayList::new)));
                    break;
                case EFFECT:
                    selectableEffects = new SelectableOptions<>(acceptableTypes.getSelectableEffects());
                    selectableEffects.setOptions(acceptableTypes.
                            getSelectableEffects().
                            getOptions().
                            stream().
                            collect(Collectors.toCollection(ArrayList::new)));
                    break;
                case PLAYERS:
                    selectablePlayers = new SelectableOptions<>(acceptableTypes.getSelectablePlayers());
                    selectablePlayers.setOptions(acceptableTypes.
                            getSelectablePlayers().
                            getOptions().
                            stream().
                            filter(p -> p.getToken() != null).
                            map(Player::getUsername).
                            collect(Collectors.toCollection(ArrayList::new)));
                    break;
                case POWERUP:
                    selectablePowerUps = new SelectableOptions<>(acceptableTypes.getSelectablePowerUps());
                    selectablePowerUps.setOptions(acceptableTypes.getSelectablePowerUps().
                            getOptions().
                            stream().
                            map(ViewPowerUp::new).
                            collect(Collectors.toCollection(ArrayList::new)));
                    break;
                case ROOM:
                    selectableRooms = new SelectableOptions<>(acceptableTypes.getSelectableRooms());
                    selectableRooms.setOptions(acceptableTypes.getSelectableRooms().
                            getOptions().
                            stream().
                            map(Color::toString).
                            collect(Collectors.toCollection(ArrayList::new)));
                    break;
                case TILES:
                    selectableTileCoords = new SelectableOptions<>(acceptableTypes.getSelectableTileCoords());
                    selectableTileCoords.setOptions(acceptableTypes.getSelectableTileCoords().
                            getOptions().
                            stream().
                            map(ViewTileCoords::new).
                            collect(Collectors.toCollection(ArrayList::new)));
                    break;
                case WEAPON:
                    selectableWeapons = new SelectableOptions<>(acceptableTypes.getSelectableWeapons());
                    selectableWeapons.setOptions(acceptableTypes.getSelectableWeapons().
                            getOptions().
                            stream().
                            map(Weapon::getName).
                            collect(Collectors.toCollection(ArrayList::new)));
                    break;
                case DIRECTION:
                    selectableDirections = new SelectableOptions<>(acceptableTypes.getSelectableDirections());
                    selectableDirections.setOptions(acceptableTypes.getSelectableDirections().getOptions().stream().map(Enum::toString).collect(Collectors.toCollection(ArrayList::new)));
                    break;
                case AMMO:
                    selectableAmmos = new SelectableOptions<>(acceptableTypes.getSelectableAmmos());
                    selectableAmmos.setOptions(acceptableTypes.getSelectableDirections().getOptions().stream().map(a -> a.toString()).collect(Collectors.toCollection(ArrayList::new)));
                    break;
                case STOP:
                    stopPrompt = acceptableTypes.getStopPrompt();
                    break;
            }
        }
    }

    public SelectableOptions getSelectableOptions(ReceivingType selected){
        switch (selected){
            case ROOM:
                return getSelectableRooms();
            case POWERUP:
                return getSelectablePowerUps();
            case PLAYERS:
                return getSelectablePlayers();
            case TILES:
                return getSelectableTileCoords();
            case WEAPON:
                return getSelectableWeapons();
            case ACTION:
                return getSelectableActions();
            case EFFECT:
                return getSelectabeEffects();
            default:
                return null;
        }
    }
}
