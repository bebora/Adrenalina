package it.polimi.se2019.view;
import it.polimi.se2019.model.board.Board;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class ViewBoard implements Serializable {

	public ViewBoard(List<ArrayList<ViewTile>> tiles, List<String> killShotTrack,
					 List<ViewDoor> doors, int skulls) {
		this.tiles = new ArrayList<>(tiles);
		this.killShotTrack = new ArrayList<>(killShotTrack);
		this.doors = new ArrayList<>(doors);
		this.skulls = skulls;
	}

	private ArrayList<ArrayList<ViewTile>> tiles;

	private ArrayList<String> killShotTrack;

	private ArrayList<ViewDoor> doors;

	private int skulls;

	private String name;

	public ArrayList<ArrayList<ViewTile>> getTiles() {
		return tiles;
	}
	public ViewBoard (Board board){
		this.tiles = board.getTiles().stream().
				map(line -> line.stream().
						map(t -> t == null ? null : new ViewTile(t)).
						collect(Collectors.toCollection(ArrayList::new))).
				collect(Collectors.toCollection(ArrayList::new));
		this.killShotTrack = board.getKillShotTrack().stream().
				map(p -> p == null ? null : p.getId()).
				collect(Collectors.toCollection(ArrayList::new));
		this.doors = board.getDoors().stream().
				map(ViewDoor::new).
				collect(Collectors.toCollection(ArrayList::new));
		this.skulls = board.getSkulls();
		this.name = board.getName();
	}

	public boolean isLinked(ViewTile tile1, ViewTile tile2, boolean throughWalls) {
		if (!throughWalls)
			return doors.contains(new ViewDoor(tile1,tile2)) || (tile1.getRoom().equals(tile2.getRoom()) && ViewTile.cabDistance(tile1,tile2) == 1);
		else return ViewTile.cabDistance(tile1,tile2) == 1;
	}

	public String getName(){
		return name;
	}
}