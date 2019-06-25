package it.polimi.se2019.controller;

import it.polimi.se2019.model.Mode;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.ammos.Ammo;
import it.polimi.se2019.model.board.Board;
import it.polimi.se2019.model.board.Door;
import it.polimi.se2019.model.board.Tile;
import it.polimi.se2019.model.cards.Effect;
import it.polimi.se2019.model.cards.PowerUp;
import it.polimi.se2019.model.cards.Weapon;
import it.polimi.se2019.view.*;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Helper class to convert model classes to view counterpart without having to import model in view
 */
public class ModelToViewConverter {
    //TODO add javadoc
    public static ViewBoard fromBoard(Board board) {
        ViewBoard ret = new ViewBoard();
        ret.setTiles(board.getTiles().stream().
                map(line -> line.stream().
                        map(t -> t == null ? null : ModelToViewConverter.fromTileToViewTile(t)).
                        collect(Collectors.toCollection(ArrayList::new))).
                collect(Collectors.toCollection(ArrayList::new)));
        ret.setKillShotTrack(board.getKillShotTrack().stream().
                map(p -> p == null ? null : p.getColor().name()).
                collect(Collectors.toCollection(ArrayList::new)));
        ret.setDoors(board.getDoors().stream().
                map(ModelToViewConverter::fromDoor).
                collect(Collectors.toCollection(ArrayList::new)));
        ret.setSkulls(board.getSkulls());
        ret.setName(board.getName());
        return ret;
    }

    public static ViewDoor fromDoor(Door door) {
        ViewDoor ret = new ViewDoor();
        ret.setTile1(ModelToViewConverter.fromTileToViewTile(door.getTile1()));
        ret.setTile2(ModelToViewConverter.fromTileToViewTile(door.getTile2()));
        return ret;
    }

    public static ViewEffect fromEffect(Effect effect) {
        ViewEffect ret = new ViewEffect();
        ret.setName(effect.getName());
        ret.setDesc(effect.getDesc());
        ret.setCost(effect.getCost().stream()
                .map(Ammo::name)
                .collect(Collectors.toList()));
        return ret;
    }

    public static ViewPlayer fromPlayer(Player player) {
        ViewPlayer ret = new ViewPlayer();
        if (player.getColor() != null) {
            ret.setColor(player.getColor().name());
        }
        ret.setUsername(player.getToken().split("\\$")[0]);
        ret.setId(player.getId());
        ret.setDamages(player.getDamages().stream().
                map(p -> p.getColor().name()).
                collect(Collectors.toCollection(ArrayList::new)));
        ret.setMarks(player.getMarks().stream().
                map(p -> p.getColor().name()).
                collect(Collectors.toCollection(ArrayList::new)));
        ret.setAmmos(player.getAmmos().stream().
                map(Ammo::name).
                collect(Collectors.toCollection(ArrayList::new)));
        ret.setFirstPlayer(player.getFirstPlayer());
        ret.setRewardPoints(new ArrayList<>(player.getRewardPoints()));
        ret.setUnloadedWeapons(player.getWeapons().stream().
                filter(w -> !w.getLoaded()).map(ModelToViewConverter::fromWeapon).
                collect(Collectors.toCollection(ArrayList::new)));
        ret.setActionCount(player.getActionCount());
        ret.setAlive(player.getAlive());
        ret.setDominationSpawn(player.getDominationSpawn());
        ret.setMaxActions(player.getMaxActions());
        if (player.getTile() == null) ret.setTile(null);
        else ret.setTile(ModelToViewConverter.fromTileToViewTile(player.getTile()));
        return ret;
    }

    public static ViewPowerUp fromPowerUp(PowerUp powerUp) {
        ViewPowerUp ret = new ViewPowerUp();
        ret.setDiscardAward(powerUp.getDiscardAward().name());
        ret.setName(powerUp.getName());
        return ret;
    }

    public static ViewTile fromTileToViewTile(Tile tile) {
        ViewTile ret = new ViewTile();
        ret.setRoom(tile.getRoom().name());
        ret.setCoords(new ViewTileCoords(tile.getPosy(), tile.getPosx()));
        ret.setWeapons(tile.getWeapons().stream().
                map(Weapon::getName).
                collect(Collectors.toCollection(ArrayList::new)));
        ret.setSpawn(tile.isSpawn());
        //Spawn tiles have a null ammoCard, so getting their ammoCard name would throw a NullPointerException
        ret.setAmmos((tile.isSpawn() || tile.getAmmoCard() == null) ? new ArrayList<>() : tile.getAmmoCard().getAmmos().stream().
                map(Ammo::name).
                collect(Collectors.toCollection(ArrayList::new)));
        return ret;
    }

    public static ViewTileCoords fromTileToViewTileCoords(Tile tile) {
        ViewTileCoords ret = new ViewTileCoords();
        ret.setPosx(tile.getPosx());
        ret.setPosy(tile.getPosy());
        return ret;
    }

    public static ViewWeapon fromWeapon(Weapon weapon) {
        ViewWeapon ret = new ViewWeapon();
        ret.setName(weapon.getName());
        ret.setEffects(weapon.getEffects().stream()
                .map(ModelToViewConverter::fromEffect)
                .collect(Collectors.toCollection(ArrayList::new)));
        ret.setCost(weapon.getCost().stream()
                .map(Ammo::name)
                .collect(Collectors.toCollection(ArrayList::new)));
        return ret;
    }
}
