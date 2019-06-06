package it.polimi.se2019.controller;

import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.actions.Action;
import it.polimi.se2019.model.board.Color;
import it.polimi.se2019.model.board.Tile;
import it.polimi.se2019.model.cards.Effect;
import it.polimi.se2019.model.cards.PowerUp;
import it.polimi.se2019.model.cards.Weapon;
import it.polimi.se2019.view.SelectableOptions;

import java.util.List;

public class AcceptableTypes {
    private SelectableOptions<Action> selectableActions;
    private SelectableOptions<Effect> selectabeEffects;
    private SelectableOptions<Player> selectablePlayers;
    private SelectableOptions<PowerUp> selectablePowerUps;
    private SelectableOptions<Color> selectableRooms;
    private SelectableOptions<Tile> selectableTileCoords;
    private SelectableOptions<Weapon> selectableWeapons;
    private boolean reverse;
    private List<ReceivingType> acceptedTypes;


    public boolean isReverse() {
        return reverse;
    }

    public void setReverse(boolean reverse) {
        this.reverse = reverse;
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

    public SelectableOptions<Effect> getSelectableEffects() {
        return selectabeEffects;
    }

    public void setSelectabeEffects(SelectableOptions<Effect> selectabeEffects) {
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

    public void setAcceptedTypes(List<ReceivingType> acceptedTypes) {
        this.acceptedTypes = acceptedTypes;
    }
}
