package it.polimi.se2019.view.gui;

import java.util.Map;

public class AssetMaps {

    static final Map<String,String> weaponsAssetsMap = Map.ofEntries(
            Map.entry("sledgehammer","AD_weapons_IT_022.png"),
            Map.entry("cyberblade","AD_weapons_IT_023.png"),
            Map.entry("power glove","AD_weapons_IT_024.png"),
            Map.entry("shotgun","AD_weapons_IT_025.png"),
            Map.entry("railgun","AD_weapons_IT_026.png"),
            Map.entry("zx-2","AD_weapons_IT_027.png"),
            Map.entry("shockwave","AD_weapons_IT_028.png"),
            Map.entry("vortex cannon","AD_weapons_IT_029.png"),
            Map.entry("heatseeker","AD_weapons_IT_0210.png"),
            Map.entry("rocket launcher","AD_weapons_IT_0211.png"),
            Map.entry("grenade launcher","AD_weapons_IT_0212.png"),
            Map.entry("flamethrower","AD_weapons_IT_0213.png"),
            Map.entry("furnace","AD_weapons_IT_0214.png"),
            Map.entry("hellion","AD_weapons_IT_0215.png"),
            Map.entry("t.h.o.r.","AD_weapons_IT_0216.png"),
            Map.entry("tractor beam","AD_weapons_IT_0217.png"),
            Map.entry("lock rifle","AD_weapons_IT_0218.png"),
            Map.entry("plasma gun","AD_weapons_IT_0219.png"),
            Map.entry("machine gun","AD_weapons_IT_0220.png"),
            Map.entry("whisper","AD_weapons_IT_0221.png"),
            Map.entry("electroscythe","AD_weapons_IT_0222.png")
    );

    static final Map<String,String> colorToPlayerBoard = Map.of(
            "RED","yellow_player_board.png",
            "BLUE","blue_player_board.png",
            "GREEN","green_player_board.png",
            "PURPLE","purple_player_board.png",
            "WHITE","white_player_board.png",
            "YELLOW","yellow_player_board.png"
    );

    static final Map<String,String> colorToFrenzyActions = Map.of(
            "RED","yellow_player_frenzy_actions.png",
            "BLUE","blue_player_frenzy_actions.png",
            "GREEN","green_player_frenzy_actions.png",
            "PURPLE","purple_player_frenzy_actions.png",
            "WHITE","white_player_frenzy_actions.png",
            "YELLOW","yellow_player_frenzy_actions.png"
    );

    static final Map<String,String> powerUpsAssets = Map.ofEntries(
            Map.entry("tagback grenadeRED","AD_powerups_IT_023.png"),
            Map.entry("tagback grenadeBLUE","AD_powerups_IT_022.png"),
            Map.entry("tagback grenadeYELLOW","AD_powerups_IT_024.png"),
            Map.entry("targeting scopeBLUE","AD_powerups_IT_025.png"),
            Map.entry("targeting scopeRED","AD_powerups_IT_026.png"),
            Map.entry("targeting scopeYELLOW","AD_powerups_IT_027.png"),
            Map.entry("newtonBLUE","AD_powerups_IT_028.png"),
            Map.entry("newtonRED","AD_powerups_IT_029.png"),
            Map.entry("newtonYELLOW","AD_powerups_IT_0210.png"),
            Map.entry("teleporterBLUE","AD_powerups_IT_0211.png"),
            Map.entry("teleporterRED","AD_powerups_IT_0212.png"),
            Map.entry("teleporterYELLOW","AD_powerups_IT_0213.png")
    );

    static Map<String,String> ammoCardAssets = Map.of(
            "PBY","PYB",
            "PBR","PRB",
            "PRY","PYR"
    );

}
