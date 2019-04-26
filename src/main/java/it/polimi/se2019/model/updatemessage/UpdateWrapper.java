package it.polimi.se2019.model.updatemessage;

public class UpdateWrapper {
    //TODO wraps the object responsible for the updates needed to the views, created by the model, updates VirtualView through Observer
    private UpdateVisitable update;

    public UpdateVisitable getUpdate() {
        return update;
    }

    private void setUpdate(UpdateVisitable update) {
        this.update = update;
    }
    public UpdateWrapper(UpdateVisitable update) {
        this.setUpdate(update);
    }
}
