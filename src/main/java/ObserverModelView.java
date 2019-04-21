public interface ObserverModelView {
    //TODO implementing different observer interface to avoid instanceof; for example, the model will update with different object everytime the virtualview that will react accordingly
    public void update(Object o);

}
