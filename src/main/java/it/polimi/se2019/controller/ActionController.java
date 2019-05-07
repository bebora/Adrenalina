package it.polimi.se2019.controller;

import it.polimi.se2019.model.DominationMatch;
import it.polimi.se2019.model.Match;
import it.polimi.se2019.model.NormalMatch;
import it.polimi.se2019.model.actions.Action;

public class ActionController {
    private Match originalMatch;
    private Match sandboxMatch;

    public ActionController(Match match){
        originalMatch = match;
        cloneMatch();
    }

    public void update(Action action){
        cloneMatch();
    }

    private void cloneMatch(){
        if(originalMatch.getSpawnPoints() == null){
            sandboxMatch = new NormalMatch(originalMatch);
        }else{
            sandboxMatch = new DominationMatch(originalMatch);
        }
    }
}
