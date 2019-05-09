package it.polimi.se2019.model.board;

public interface Deck<T>{
    T draw();

    void add(T t);

    void addToDiscarded(T t);
}
