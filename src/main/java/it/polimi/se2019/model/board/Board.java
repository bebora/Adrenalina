package it.polimi.se2019.model.board;

import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.ammos.Ammo;
import it.polimi.se2019.model.ammos.AmmoCard;
import it.polimi.se2019.model.cards.PowerUp;
import it.polimi.se2019.model.cards.Weapon;

import java.util.*;
import java.util.stream.Collectors;


public class Board {

    Random rand = new Random();

    private String name;

    private List<Integer> killShotReward;
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
	 * Deck containing the remaining weapons
	 */
	private Deck<Weapon> weaponsDeck;

	/**
	 * List of possible ammos to randomly appear on non-spawn Tiles
	 */
	private Deck<AmmoCard> ammoCards;
    /**
     * List of powerUps to randomly draw
     */
	private Deck<PowerUp> powerUps;

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
		private String name;
		private List<Player> killShotTrack = new ArrayList<>();
		private Deck<Weapon> weaponsDeck = new LimitedDeck<>();
		private Deck<AmmoCard> ammoCards = new UnlimitedDeck<>();
		private Deck<PowerUp> powerUps = new UnlimitedDeck<>();


		public Builder setPowerUps(Deck<PowerUp> powerUps) {
            this.powerUps = powerUps;
            return this;
        }


		public Builder(int skulls) {
			this.skulls = skulls;
		}

		public Builder setAmmoCards(Deck<AmmoCard> ammoCards) {
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

		public Builder setWeapon(Deck<Weapon> weaponsDeck) {
			this.weaponsDeck = weaponsDeck;
			return this;
		}

		public Builder setName(String name){
			this.name = name;
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
		this.name = builder.name;
		refreshWeapons();
		refreshAmmos();
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
	public boolean isLinked(Tile tile1,Tile tile2, boolean throughWalls) {
		if (!throughWalls)
			return doors.contains(new Door(tile1,tile2)) || (tile1.getRoom() == tile2.getRoom() && Tile.cabDistance(tile1,tile2) == 1);
		else return Tile.cabDistance(tile1,tile2) == 1;
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
				filter(t -> isLinked(t, pointOfView, false)).
				map(Tile::getRoom).
				collect(Collectors.toSet());
		return tiles.stream().
				flatMap(List::stream).filter(Objects::nonNull).
				filter(t -> visibleColors.contains(t.getRoom())).
				collect(Collectors.toSet());
	}

	/**
	 * Get the reachable tiles given a starting Tile, without going through walls
	 * @param pointOfView starting Tile
	 * @param minDistance minimum distance that can be reached
	 * @param maxDistance maximum distance that can be reached
	 * @return set of tiles that can be reached
	 */
	public Set<Tile> reachable(Tile pointOfView, int minDistance, int maxDistance, boolean throughWalls) {
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
					if (isLinked(t1, t2, throughWalls))
						tempTiles.add(t1);
			}
			currTile = new HashSet<>();
			tempTiles.forEach(currTile::add);
			reachableTiles.add(currTile);
		}
		if (maxDistance == -1) {
		    if (minDistance > 0) {
				reachableTiles.removeAll(reachableTiles.get(minDistance - 1));
				return reachableTiles.get(minDistance);
			} else {
				return totalTiles;
			}
        }
		else if (minDistance > 0)
		    reachableTiles.get(maxDistance).removeAll(reachableTiles.get(minDistance-1));
		return reachableTiles.get(maxDistance);
	}

	public String getName(){ return name;}

    public PowerUp drawPowerUp() {
		return powerUps.draw();
	}

    public Weapon drawWeapon(){
        return weaponsDeck.draw();
	}

    public void discardPowerUp(PowerUp powerUp) {
	    powerUps.addToDiscarded(powerUp);
    }

    public void discardAmmoCard(AmmoCard ammoCard) {
		ammoCards.addToDiscarded(ammoCard);
	}

	public Deck<Weapon> getWeaponsDeck() {
		return weaponsDeck;
	}

	public Tile getTile(int posy, int posx) {
		if (posy < tiles.size() && posx < tiles.get(posy).size())
	    	return tiles.get(posy).get(posx);
		else return null;
    }

    public List<List<Tile>> getTiles() {
		return tiles;
	}

	public int getSkulls() {
		return skulls;
	}

	public List<Door> getDoors() {
		return doors;
	}

	public Deck<PowerUp> getPowerUps() {
        return powerUps;
    }

    public List<Player> getKillShotTrack() {
		return killShotTrack;
	}

	public void setKillShotTrack(List<Player> killShotTrack) {
		this.killShotTrack = killShotTrack;
	}

	public void refreshWeapons() {
		List <Tile> spawnTiles = tiles.stream().flatMap(List::stream).filter(Objects::nonNull).filter(Tile::isSpawn).collect(Collectors.toList());
		for (Tile t : spawnTiles) {
			int numberWeapons = t.getWeaponsNumber();
			for (int i = 0; i < 3 - numberWeapons; i++)
			    t.addWeapon(drawWeapon());
		}
	}

	public void refreshAmmos() {
        List <Tile> emptyAmmosTiles = tiles.stream().
                flatMap(List::stream).
                filter(Objects::nonNull).
                filter(t-> !(t.isSpawn())).
                filter(t -> t.getAmmoCard() == null).collect(Collectors.toList());
        for (Tile t : emptyAmmosTiles) {
            t.addAmmo(ammoCards.draw());
        }
    }

    public Tile getSpawningPoint(PowerUp powerUp) {
		Ammo ammo = powerUp.getDiscardAward();
		powerUps.addToDiscarded(powerUp);
		return tiles.stream().
				flatMap(List::stream).
				filter(Objects::nonNull).
				filter(Tile::isSpawn).
				filter(t->t.getRoom() == Color.valueOf(ammo.toString())).
				findFirst().orElseThrow(UnsupportedOperationException::new);
	}

	public void addToKillShot(Player player) {
		killShotTrack.add(player);
	}

	public List<Integer> getKillShotReward() {
		return killShotReward;
	}



}