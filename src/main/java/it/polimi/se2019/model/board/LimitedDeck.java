package it.polimi.se2019.model.board;
import java.util.*;

/**
 * Handles drawing cards from a limited Queue.
 * The deck cards are limited, so they can't be discarded and once ended, {@link #draw()} will return null.
 * @param <T> what the deck is containing
 */
public class LimitedDeck<T> implements Deck<T> {
    private Queue<T> remainingCards;

    public LimitedDeck () {
        remainingCards = new LinkedList<>();
    }

    public LimitedDeck (List<T> cards) {
        remainingCards = new LinkedList<>(cards);
    }

    @Override
    public void addToDiscarded(T t) { throw new UnsupportedOperationException();}

    @Override
    public void add(T t) {
        remainingCards.add(t);
    }

    /**
     * @return a card if not empty, otherwise null
     */
    @Override
    public T draw() {
        if (!remainingCards.isEmpty())
            return remainingCards.poll();
        else return null;
    }
}
