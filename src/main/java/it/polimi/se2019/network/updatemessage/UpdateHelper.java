package it.polimi.se2019.network.updatemessage;

import it.polimi.se2019.model.Player;
import it.polimi.se2019.view.ViewPlayer;

import java.util.ArrayList;
import java.util.List;

public class UpdateHelper {
    /**
     * Transform list of Players to list of matching ViewPlayer by id
     * Doesn't modify passed arguments
     * @param realPlayers
     * @param viewPlayers
     * @return
     */
    public static List<ViewPlayer> playersToViewPlayers(List<Player> realPlayers, List<ViewPlayer> viewPlayers) {
        List<ViewPlayer> ret = new ArrayList<>();
        for (Player p: realPlayers) {
            ret.add(playerToViewPlayer(p, viewPlayers));
        }
        return ret;
    }
    public static ViewPlayer playerToViewPlayer(Player player, List<ViewPlayer> viewPlayers){
        return viewPlayers.stream().filter(p->p.getId().equals(player.getId())).
                findFirst().orElseThrow(()->new RuntimeException("Can't find player"));
    }
}
