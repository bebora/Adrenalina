package it.polimi.se2019.model.UpdateMessage;

import it.polimi.se2019.view.UpdateVisitor;

/**
 * Represent attack from and to any player
 */
public class AttackPlayerUpdate implements UpdateVisitable {
    private String attackerId;
    private String receiverId;
    private int damageAmount;
    private int marksAmount;
    public void accept(UpdateVisitor visitor) {
        visitor.visit(this);
    }

    public String getAttackerId() {
        return attackerId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public int getDamageAmount() {
        return damageAmount;
    }

    public int getMarksAmount() {
        return marksAmount;
    }
}
