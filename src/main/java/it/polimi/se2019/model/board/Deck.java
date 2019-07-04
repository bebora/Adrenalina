package it.polimi.se2019.model.board;

/**
 * Interface for drawing and discarding into the deck.
 * @param <T> what the deck is containing
 */
public interface Deck<T>{
    /**
     * Draw a card from the deck
     * @return the drawn card, or null if no card is left
     */
    T draw();

    /**
     * Add a card to the deck
     * @param t
     */
    void add(T t);

    /**
     * Add a card to the discarded cards
     * @param t
     */
    void addToDiscarded(T t);
}
