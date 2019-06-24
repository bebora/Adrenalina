package it.polimi.se2019.controller;

import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.actions.Action;
import it.polimi.se2019.model.ammos.Ammo;
import it.polimi.se2019.model.board.Color;
import it.polimi.se2019.model.board.Tile;
import it.polimi.se2019.model.cards.Direction;
import it.polimi.se2019.model.cards.PowerUp;
import it.polimi.se2019.model.cards.Weapon;
import it.polimi.se2019.view.SelectableOptions;

import java.util.List;

/**
 * Container class used in Controller to store accepted information from the client.
 * Every options contains the prompt to show to the client, the possible choices, the minimum and maximum number of options.
 * {@link #acceptedTypes} contains the list of ReceivingType of which the related SelectableOptions is not null.
 * Every {@link SelectableOptions} contains the allowed input that can be sent from the client, related to the acceptedType.
 */
public class AcceptableTypes {
    private SelectableOptions<Action> selectableActions;
    private SelectableOptions<String> selectabeEffects;
    private SelectableOptions<Player> selectablePlayers;
    private SelectableOptions<PowerUp> selectablePowerUps;
    private SelectableOptions<Color> selectableRooms;
    private SelectableOptions<Tile> selectableTileCoords;
    private SelectableOptions<Weapon> selectableWeapons;
    private SelectableOptions<Ammo> selectableAmmos;
    private SelectableOptions<Direction> selectableDirections;
    private boolean reverse;
    private String stopPrompt;
    private List<ReceivingType> acceptedTypes;


    public SelectableOptions<Ammo> getSelectableAmmos() {
        return selectableAmmos;
    }

    public void setSelectableAmmos(SelectableOptions<Ammo> selectableAmmos) {
        this.selectableAmmos = selectableAmmos;
    }

    public SelectableOptions<Direction> getSelectableDirections() {
        return selectableDirections;
    }

    public void setSelectableDirections(SelectableOptions<Direction> selectableDirections) {
        this.selectableDirections = selectableDirections;
    }

    public String getStopPrompt() {
        return stopPrompt;
    }

    public boolean isReverse() {
        return reverse;
    }

    public void setStop(boolean reverse, String stopPrompt) {
        this.reverse = reverse;
        this.stopPrompt = stopPrompt;
    }

    public AcceptableTypes(List<ReceivingType> receivingTypes) {
        this.acceptedTypes = receivingTypes;
    }

    public SelectableOptions<Action> getSelectableActions() {
        return selectableActions;
    }

    public void setSelectableActions(SelectableOptions<Action> selectableActions) {
        this.selectableActions = selectableActions;
    }

    public SelectableOptions<String> getSelectableEffects() {
        return selectabeEffects;
    }

    public void setSelectabeEffects(SelectableOptions<String> selectabeEffects) {
        this.selectabeEffects = selectabeEffects;
    }

    public SelectableOptions<Player> getSelectablePlayers() {
        return selectablePlayers;
    }

    public void setSelectablePlayers(SelectableOptions<Player> selectablePlayers) {
        this.selectablePlayers = selectablePlayers;
    }

    public SelectableOptions<PowerUp> getSelectablePowerUps() {
        return selectablePowerUps;
    }

    public void setSelectablePowerUps(SelectableOptions<PowerUp> selectablePowerUps) {
        this.selectablePowerUps = selectablePowerUps;
    }

    public SelectableOptions<Color> getSelectableRooms() {
        return selectableRooms;
    }

    public void setSelectableRooms(SelectableOptions<Color> selectableRooms) {
        this.selectableRooms = selectableRooms;
    }

    public SelectableOptions<Tile> getSelectableTileCoords() {
        return selectableTileCoords;
    }

    public void setSelectableTileCoords(SelectableOptions<Tile> selectableTileCoords) {
        this.selectableTileCoords = selectableTileCoords;
    }

    public SelectableOptions<Weapon> getSelectableWeapons() {
        return selectableWeapons;
    }

    public void setSelectableWeapons(SelectableOptions<Weapon> selectableWeapons) {
        this.selectableWeapons = selectableWeapons;
    }

    public List<ReceivingType> getAcceptedTypes() {
        return acceptedTypes;
    }
}
