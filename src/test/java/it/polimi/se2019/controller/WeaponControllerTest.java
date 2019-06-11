package it.polimi.se2019.controller;

import it.polimi.se2019.model.Match;
import it.polimi.se2019.model.Mode;
import it.polimi.se2019.model.Player;
import it.polimi.se2019.model.ammos.Ammo;
import it.polimi.se2019.model.cards.CardCreator;
import it.polimi.se2019.model.cards.Weapon;
import it.polimi.se2019.network.ViewUpdater;
import it.polimi.se2019.network.ViewUpdaterRMI;
import it.polimi.se2019.view.ConcreteViewReceiver;
import it.polimi.se2019.view.VirtualView;
import org.junit.jupiter.api.Test;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WeaponControllerTest {
    private List<Player> testPlayers = new ArrayList<>(Arrays.asList(new Player("buono"),new Player("cattivo")));
    private Weapon testWeapon = CardCreator.parseWeapon("vortexCannon.btl");
    private GameController gameController = new GameController(testPlayers,"board3.btlb",5,false, null);
    Match testMatch = gameController.getMatch();
    private Player currentPlayer = testMatch.getPlayers().get(testMatch.getCurrentPlayer());
    private ActionController actionController = new ActionController(testMatch,gameController);

    @Test
    void assignWeapon(){
        VirtualView view = new VirtualView();
        ViewUpdater viewUpdater;
        try {
            viewUpdater = new ViewUpdaterRMI(new ConcreteViewReceiver(view), view);
        }
        catch (RemoteException e){
            assert false;
            return;
        }
        LobbyController lobbyController = new LobbyController(new ArrayList<>(Collections.singleton(Mode.NORMAL)));
        currentPlayer.setVirtualView(new VirtualView(lobbyController));
        currentPlayer.getVirtualView().setViewUpdater(viewUpdater);
        currentPlayer.addWeapon(testWeapon);
        WeaponController weaponControllerTest = new WeaponController(testMatch,null,testMatch.getPlayers(), null);
        weaponControllerTest.updateOnWeapon(testWeapon);
        assertEquals(weaponControllerTest.getWeapon(),testWeapon);
        testMatch.getPlayers().get(testMatch.getCurrentPlayer()).getWeapons().remove(testWeapon);
    }

     @Test
    void getUsableEffects(){
        //test with vortexCannon(absolutePriority, missing Ammos)
         currentPlayer.addWeapon(testWeapon);
         currentPlayer.setVirtualView(new VirtualView(new LobbyController(Arrays.asList(Mode.NORMAL))));
         try {
             currentPlayer.getVirtualView().setViewUpdater(new ViewUpdaterRMI(new ConcreteViewReceiver(currentPlayer.getVirtualView()), currentPlayer.getVirtualView()));
         }
         catch (RemoteException e) {
             System.out.println("Unable to create ViewReceiver");
         }
         WeaponController weaponControllerTest = new WeaponController(testMatch,testWeapon,testMatch.getPlayers(), null);
         assertEquals(weaponControllerTest.getUsableEffects().size(),1);
         for(int i = 0; i<3; i++)
             currentPlayer.getAmmos().remove(Ammo.RED);
         assertEquals(weaponControllerTest.getUsableEffects().get(0),"basic effect");
         assertEquals(weaponControllerTest.getUsableEffects().size(),1);

         //fill ammos
         for(int i = 0; i<3; i++) {
             currentPlayer.addAmmo(Ammo.RED);
             currentPlayer.addAmmo(Ammo.YELLOW);
             currentPlayer.addAmmo(Ammo.BLUE);
         }

         //test with railgun(equal absolutePriority)
         testWeapon = CardCreator.parseWeapon("railgun.btl");
         currentPlayer.addWeapon(testWeapon);
         weaponControllerTest.updateOnWeapon(testWeapon);
         assertEquals(weaponControllerTest.getUsableEffects().size(), 2);

         //test with cyberblade(relativePriority)
         testWeapon = CardCreator.parseWeapon("cyberblade.btl");
         currentPlayer.addWeapon(testWeapon);
         weaponControllerTest.updateOnWeapon(testWeapon);
         assertEquals(3,weaponControllerTest.getUsableEffects().size());
         assertEquals("with shadowstep", weaponControllerTest.getUsableEffects().get(1));
         /*weaponControllerTest.updateOnEffect(testWeapon.getEffects().get(0).getName());
         assertEquals(2,weaponControllerTest.getUsableEffects().size());
         assertEquals("with shadowstep", weaponControllerTest.getUsableEffects().get(0));
         assertEquals("with slice and dice", weaponControllerTest.getUsableEffects().get(1));*/
     }
}
