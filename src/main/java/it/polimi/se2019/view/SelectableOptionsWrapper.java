package it.polimi.se2019.view;

import it.polimi.se2019.controller.ReceivingType;

import java.io.Serializable;
import java.util.List;

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

    public SelectableOptions<String> getSelectableAmmos() {
        return selectableAmmos;
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

    public void setSelectableActions(SelectableOptions<String> selectableActions) {
        this.selectableActions = selectableActions;
    }

    public void setSelectableEffects(SelectableOptions<String> selectableEffects) {
        this.selectableEffects = selectableEffects;
    }

    public void setSelectablePlayers(SelectableOptions<String> selectablePlayers) {
        this.selectablePlayers = selectablePlayers;
    }

    public void setSelectablePowerUps(SelectableOptions<ViewPowerUp> selectablePowerUps) {
        this.selectablePowerUps = selectablePowerUps;
    }

    public void setSelectableRooms(SelectableOptions<String> selectableRooms) {
        this.selectableRooms = selectableRooms;
    }

    public void setSelectableTileCoords(SelectableOptions<ViewTileCoords> selectableTileCoords) {
        this.selectableTileCoords = selectableTileCoords;
    }

    public void setSelectableWeapons(SelectableOptions<String> selectableWeapons) {
        this.selectableWeapons = selectableWeapons;
    }

    public void setSelectableDirections(SelectableOptions<String> selectableDirections) {
        this.selectableDirections = selectableDirections;
    }

    public void setSelectableAmmos(SelectableOptions<String> selectableAmmos) {
        this.selectableAmmos = selectableAmmos;
    }

    public void setAcceptedTypes(List<ReceivingType> acceptedTypes) {
        this.acceptedTypes = acceptedTypes;
    }

    public void setStopPrompt(String stopPrompt) {
        this.stopPrompt = stopPrompt;
    }

    public SelectableOptions getSelectableOptions(ReceivingType selected){
        switch (selected){
            case AMMO:
                return getSelectableAmmos();
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
                return getSelectableEffects();
            default:
                return null;
        }
    }
}
