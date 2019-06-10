package it.polimi.se2019.model.board;
import java.util.*;

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

    @Override
    public T draw() {
        if (!remainingCards.isEmpty())
            return remainingCards.poll();
        else return null;
    }
}
