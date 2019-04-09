package model;

import model.ammos.*;

import java.util.*;
import java.util.stream.Collectors;


public class Board {

    Random rand = new Random();



	/**
	 * List of tiles that make up the Board
	 */
	private List<List<Tile>> tiles;
	/**
	 * Adjacency list of doors between tiles
	 */
	private List<Door> doors;
	/**
	 * Number of skulls remaining on the Board
	 */
	private int skulls;

	/**
	 * List of weapons remaining to be drawn
	 */
	private List<Weapon> weaponsDeck;

	/**
	 * List of possible ammos to randomly appear on non-spawn Tiles
	 */
	private List<AmmoCard> ammoCards;
    /**
     * List of powerUps to randomly draw
     */
	private List<PowerUp> powerUps;

	/**
	 * List of players that got a Kill and an Overkill
	 * Every kill is composed by two elements added to the list:
	 * <li>
	 *     Player who gave the KillShot
	 * </li>
	 * <li>
	 *     Player who gave the OverKill; null gets added if no overkill.
	 * </li>
	 */
	private List<Player> killShotTrack;




	public static class Builder {

		private List<List<Tile>> tiles;
		private List<Door> doors;
		private int skulls;
		private List<Weapon> weaponsDeck;
		private List<Player> killShotTrack = new ArrayList<>();
		private List<AmmoCard> ammoCards = new ArrayList<>();

        public Builder setPowerUps(List<PowerUp> powerUps) {
            this.powerUps = powerUps;
            return this;
        }

        private List<PowerUp> powerUps;

		public Builder(int skulls) {
			this.skulls = skulls;
		}

		public Builder setAmmoCards(List<AmmoCard> ammoCards) {
			this.ammoCards = ammoCards;
			return this;
		}

		public Builder setDoors(List<Door> doors) {
			this.doors = doors;
			return this;

		}

		public Builder setTiles(List<List<Tile>> tiles) {
			this.tiles = tiles;
			return this;

		}

		public Builder setWeapon(List<Weapon> weaponsDeck) {
			this.weaponsDeck = weaponsDeck;
			return this;
		}

		public Board build() {
			return new Board(this);
		}
	}

	public Board(Builder builder) {
		this.tiles = builder.tiles;
		this.doors = builder.doors;
		this.skulls = builder.skulls;
		this.weaponsDeck = builder.weaponsDeck;
		this.killShotTrack = builder.killShotTrack;
		this.powerUps = builder.powerUps;
		this.ammoCards = builder.ammoCards;
		// draw cards when we create corresponding weapons
		/*for (int i = 0; i < 3; i++) {
            tiles.stream().
                    flatMap(List::stream).
                    filter(Tile::isSpawn).
                    forEach(tile -> tile.addWeapon(drawWeapon()));
        }*/





	}

	/**
	 * Check if two tiles are linked by:
	 * <li>
	 *     A door
	 * </li>
	 * <li>
	 *     Being in the same room and having distance 1
	 * </li>
	 * @param tile1 first tile
	 * @param tile2 second tile
	 * @return true if the tiles are connected
	 */
	public boolean isLinked(Tile tile1,Tile tile2) {
		return doors.contains(new Door(tile1,tile2)) || (tile1.getRoom() == tile2.getRoom() && Tile.cabDistance(tile1,tile2) == 1);
	}

	/**
	 * Visible tiles by the pointOfView
	 * All tiles that are or in the same room, or in the same room of a tile connected by a Door to POV are visibleTiles
	 * @param pointOfView square used to calculate visibility
	 * @return a set containing visibleTiles tiles
	 */
	public Set<Tile> visibleTiles(Tile pointOfView) {
		Set<Color> visibleColors = tiles.stream().
				flatMap(List::stream).filter(Objects::nonNull).
				filter(t -> isLinked(t, pointOfView)).
				map(Tile::getRoom).
				collect(Collectors.toSet());
		return tiles.stream().
				flatMap(List::stream).filter(Objects::nonNull).
				filter(t -> visibleColors.contains(t.getRoom())).
				collect(Collectors.toSet());
	}

	/**
	 * Get the reachable tiles given a starting Tile
	 * @param pointOfView starting Tile
	 * @param minDistance minimum distance that can be reached
	 * @param maxDistance maximum distance that can be reached
	 * @return set of tiles that can be reached
	 */
	public Set<Tile> reachable(Tile pointOfView, int minDistance, int maxDistance) {
	    Set<Tile> totalTiles = tiles.stream().
				flatMap(List::stream).filter(Objects::nonNull).collect(Collectors.toSet());
		List<Set<Tile>> reachableTiles= new ArrayList<>();
		Set<Tile> tempTiles = new HashSet<>();
        tempTiles.add(pointOfView);
		Set<Tile> currTile = new HashSet<>();
		tempTiles.forEach(currTile::add);
		reachableTiles.add(new HashSet<>(currTile));
		for (int i = 0; i < Math.max(maxDistance,minDistance); i++) {
			for (Tile t1 : totalTiles) {
				for (Tile t2 : currTile)
					if (isLinked(t1, t2))
						tempTiles.add(t1);
			}
			currTile = new HashSet<>();
			tempTiles.forEach(currTile::add);
			reachableTiles.add(currTile);
		}
		if (maxDistance == -1) {
		    return totalTiles;
        }
		else if (minDistance > 0)
		    reachableTiles.get(maxDistance).removeAll(reachableTiles.get(minDistance-1));
		return reachableTiles.get(maxDistance);
	}
    @Override
    public String toString() {
	    //TODO print string with format 3x3 each, 9 spaces means to have space for 5 players, 1 color, 3 ammos
		return "";
    }

    public PowerUp drawPowerUp() {
		return powerUps.get(rand.nextInt(powerUps.size()));
	}


    public Weapon drawWeapon() {
        int indexWeaponToPop = rand.nextInt(powerUps.size());
        Weapon weapon = weaponsDeck.get(indexWeaponToPop);
        weaponsDeck.remove(indexWeaponToPop);
        return weapon;
	}


	public List<Weapon> getWeaponsDeck() {
		return weaponsDeck;
	}

	public Tile getTile(int posy, int posx) {
	    return tiles.get(posy).get(posx);
    }

    public List<List<Tile>> getTiles() {
		return tiles;
	}

	public int getSkulls() {
		return skulls;
	}

    public List<PowerUp> getPowerUps() {
        return powerUps;
    }

    public List<Player> getKillShotTrack() {
		return killShotTrack;
	}

	public void setKillShotTrack(List<Player> killShotTrack) {
		this.killShotTrack = killShotTrack;
	}

	public void updateWeapons() {
		//TODO refill tiles with less than three weapons
	}

	public void refreshSpawnTiles() {
		List <Tile> spawnTiles = tiles.stream().flatMap(List::stream).filter(Tile::isSpawn).collect(Collectors.toList());
		for (Tile t : spawnTiles) {
			int numberWeapons = t.getWeaponsNumber();
			for (int i = 0; i < 3 - numberWeapons; i++)
				t.addWeapon(drawWeapon());
		}
	}
}