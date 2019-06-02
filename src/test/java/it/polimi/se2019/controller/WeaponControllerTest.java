package it.polimi.se2019.controller;

import it.polimi.se2019.model.Match;
import it.polimi.se2019.model.Mode;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.ammos.Ammo;
import it.polimi.se2019.model.cards.CardCreator;
import it.polimi.se2019.model.cards.Weapon;
import it.polimi.se2019.network.ViewUpdaterRMI;
import it.polimi.se2019.view.ConcreteViewReceiver;
import it.polimi.se2019.view.VirtualView;
import org.junit.jupiter.api.Test;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class WeaponControllerTest {
    private List<Player> testPlayers = new ArrayList<>(Arrays.asList(new Player("buono"),new Player("cattivo")));
    private Weapon testWeapon = CardCreator.parseWeapon("cannoneVortex.btl");
    private GameController gameController = new GameController(testPlayers,"board1.btlb",5,false);
    Match testMatch = gameController.getMatch();
    private Player currentPlayer = testMatch.getPlayers().get(testMatch.getCurrentPlayer());
    private ActionController actionController = new ActionController(testMatch,gameController);

    @Test
    void assignWeapon(){
        currentPlayer.addWeapon(testWeapon);
        currentPlayer.setVirtualView(new VirtualView(new LobbyController(Arrays.asList(Mode.NORMAL))));
        WeaponController weaponControllerTest = new WeaponController(testMatch,null,testMatch.getPlayers(),actionController);
        weaponControllerTest.updateOnWeapon(testWeapon);
        assertEquals(weaponControllerTest.getWeapon(),testWeapon);
        testMatch.getPlayers().get(testMatch.getCurrentPlayer()).getWeapons().remove(testWeapon);
        weaponControllerTest.updateOnWeapon(testWeapon);
        assertNull(weaponControllerTest.getWeapon());
    }

     @Test
    void getUsableEffects(){
        //test with cannoneVortex(absolutePriority, missing Ammos)
         currentPlayer.addWeapon(testWeapon);
         currentPlayer.setVirtualView(new VirtualView(new LobbyController(Arrays.asList(Mode.NORMAL))));
         try {
             currentPlayer.getVirtualView().setViewUpdater(new ViewUpdaterRMI(new ConcreteViewReceiver(currentPlayer.getVirtualView())));
         }
         catch (RemoteException e) {
             System.out.println("Unable to create ViewReceiver");
         }
         WeaponController weaponControllerTest = new WeaponController(testMatch,testWeapon,testMatch.getPlayers(),actionController);
         assertEquals(weaponControllerTest.getUsableEffects().size(),1);
         for(int i = 0; i<3; i++)
             currentPlayer.getAmmos().remove(Ammo.RED);
         assertEquals(weaponControllerTest.getUsableEffects().get(0),"effetto base");
         assertEquals(weaponControllerTest.getUsableEffects().size(),1);

         //fill ammos
         for(int i = 0; i<3; i++) {
             currentPlayer.addAmmo(Ammo.RED);
             currentPlayer.addAmmo(Ammo.YELLOW);
             currentPlayer.addAmmo(Ammo.BLUE);
         }

         //test with fucileLaser(equal absolutePriority)
         testWeapon = CardCreator.parseWeapon("fucileLaser.btl");
         currentPlayer.addWeapon(testWeapon);
         weaponControllerTest.updateOnWeapon(testWeapon);
         assertEquals(weaponControllerTest.getUsableEffects().size(), 2);

         //test with spadaFotonica(relativePriority)
         testWeapon = CardCreator.parseWeapon("spadaFotonica.btl");
         currentPlayer.addWeapon(testWeapon);
         weaponControllerTest.updateOnWeapon(testWeapon);
         assertEquals(3,weaponControllerTest.getUsableEffects().size());
         assertEquals("passo d'ombra",weaponControllerTest.getUsableEffects().get(1));
         weaponControllerTest.updateOnEffect(testWeapon.getEffects().get(0).getName());
         assertEquals(2,weaponControllerTest.getUsableEffects().size());
         assertEquals("passo d'ombra",weaponControllerTest.getUsableEffects().get(0));
         assertEquals("modalità sminuzzare",weaponControllerTest.getUsableEffects().get(1));
     }


}
