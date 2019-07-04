package it.polimi.se2019.controller;

import it.polimi.se2019.model.Match;
import it.polimi.se2019.model.NormalMatch;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.cards.CardCreator;
import it.polimi.se2019.model.cards.Weapon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

class EventHelperTest {
    private Match match;
    private Player testPlayer;
    private Player enemy;
    private EventHelper eventHelper;

    @BeforeEach
    void setUp() {
        //Set the player and an enemy in the match
        testPlayer = new Player("foo");
        enemy = new Player("poo");
        match = new NormalMatch(Arrays.asList(testPlayer, enemy), "board1.btlb", 8);
        eventHelper = new EventHelper(match, testPlayer);
    }

    @Test
    void getPlayersFromUsernames() {
        //Test the player's username gets parsed correctly
        List<String> usernames = match.getPlayers().stream().map(Player::getUsername).collect(Collectors.toList());
        List<Player> players = eventHelper.getPlayersFromUsername(usernames);
        assertEquals(players, match.getPlayers());
    }


    @Test
    void getWeaponFromString() {
        //Test the weapon name gets parsed correctly from the helper
        Weapon testWeapon = CardCreator.parseWeapon("furnace.btl");
        testPlayer.addWeapon(testWeapon);
        testPlayer.setTile(match.getBoard().getTile(0, 0));
        match.setCurrentPlayer(testPlayer);
        Weapon equalWeapon = eventHelper.getWeaponFromString("furnace");
        assertEquals(equalWeapon, testWeapon);
    }

}