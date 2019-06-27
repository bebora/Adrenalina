package it.polimi.se2019.model.board;

import java.util.*;

/**
 * Interface drawing cards from an unlimited queue.
 * The deck cards are unlimited, so they are discarded after they are used.
 * Once the {@link #remainingCards} gets empty, the {@link #discardedCards} gets shuffled and added to them.
 * @param <T> what the deck is containing
 */
public class UnlimitedDeck<T> implements Deck<T>{

    private Queue<T> discardedCards = new LinkedList<>();
    private Queue<T> remainingCards;

    public UnlimitedDeck (List<T> cards) {
        remainingCards = new LinkedList<>();
        discardedCards = new LinkedList<>(cards);
    }

    public UnlimitedDeck () {
        remainingCards = new LinkedList<>();
    }

    @Override
    public void add(T t) {
        remainingCards.add(t);
    }

    @Override
    public void addToDiscarded(T t) {
        discardedCards.add(t);
    }

    /**
     * Draw a cards from the deck.
     * If {@link #remainingCards} are empty, it shuffles {@link #discardedCards} and add them to {@link #remainingCards}.
     * @return a card
     */
    @Override
    public T draw() {
        if (remainingCards.isEmpty()) {
            List<T> tempArray = new ArrayList<>(discardedCards);
            Collections.shuffle(tempArray);
            remainingCards = new LinkedList<>(tempArray);
            discardedCards = new LinkedList<>();
        }
        return remainingCards.poll();
    }
}
