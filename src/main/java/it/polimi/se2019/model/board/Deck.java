package it.polimi.se2019.model.board;

public interface Deck<T>{
    public T draw();

    public void add(T t);

    public void addToDiscarded(T t);
}
