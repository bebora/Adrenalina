package it.polimi.se2019.view;

import it.polimi.se2019.controller.AcceptableTypes;
import it.polimi.se2019.controller.ReceivingType;
import it.polimi.se2019.model.actions.Action;
import it.polimi.se2019.model.board.Color;
import it.polimi.se2019.model.cards.Effect;
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
    private List<ReceivingType> acceptedTypes;

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
                            map(Effect::getName).
                            collect(Collectors.toCollection(ArrayList::new)));
                    break;
                case PLAYERS:
                    selectablePlayers = new SelectableOptions<>(acceptableTypes.getSelectablePlayers());
                    selectablePlayers.setOptions(acceptableTypes.
                            getSelectablePlayers().
                            getOptions().
                            stream().
                            map(p -> p.getToken().split("$")[0]).
                            collect(Collectors.toCollection(ArrayList::new)));
                    break;
                case POWERUP:
                    selectablePowerUps = new SelectableOptions<>(acceptableTypes.getSelectablePowerUps());
                    selectablePowerUps.setOptions(acceptableTypes.getSelectablePowerUps().
                            getOptions().
                            stream().
                            map(p -> new ViewPowerUp(p)).
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
                            map(t -> new ViewTileCoords(t)).
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
            }
        }
    }
}
