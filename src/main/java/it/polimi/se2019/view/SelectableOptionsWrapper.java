package it.polimi.se2019.view;

public class SelectableOptionsWrapper {
    private SelectableOptions<ViewAction> selectableActions;
    private SelectableOptions<String> selectabeEffects;
    private SelectableOptions<String> selectablePlayers;
    private SelectableOptions<ViewPowerUp> selectablePowerUps;
    private SelectableOptions<String> selectableRooms;
    private SelectableOptions<ViewTileCoords> selectableTileCoords;
    private SelectableOptions<String> selectableWeapons;

    public SelectableOptions<ViewAction> getSelectableActions() {
        return selectableActions;
    }

    public SelectableOptions<String> getSelectabeEffects() {
        return selectabeEffects;
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
}
