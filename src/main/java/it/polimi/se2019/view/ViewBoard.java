package it.polimi.se2019.view;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.board.Board;

import java.util.*;
import java.util.stream.Collectors;

public class ViewBoard {

	public ViewBoard(List<List<ViewTile>> tiles, List<String> killShotTrack,
					 List<ViewDoor> doors, int skulls) {
		this.tiles = tiles;
		this.killShotTrack = killShotTrack;
		this.doors = doors;
		this.skulls = skulls;
	}

	private List<List<ViewTile>> tiles;

	private List<String> killShotTrack;

	private List<ViewDoor> doors;

	private int skulls;

	public List<List<ViewTile>> getTiles() {
		return tiles;
	}
	public ViewBoard (Board board){
		this.tiles = board.getTiles().stream().
				map(line -> line.stream().
						map(t -> t == null ? null : new ViewTile(t)).collect(Collectors.toList())).
				collect(Collectors.toList());
		this.killShotTrack = board.getKillShotTrack().stream().map(Player::getId).collect(Collectors.toList());
		this.doors = board.getDoors().stream().
				map(ViewDoor::new).collect(Collectors.toList());
		this.skulls = board.getSkulls();
	}
}