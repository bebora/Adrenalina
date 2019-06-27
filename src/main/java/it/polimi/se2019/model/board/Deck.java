package it.polimi.se2019.model.board;

/**
 * Interface for drawing and discarding into the deck.
 * @param <T> what the deck is containing
 */
public interface Deck<T>{
    T draw();

    void add(T t);

    void addToDiscarded(T t);
}
