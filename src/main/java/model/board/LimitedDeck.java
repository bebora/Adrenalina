package model.board;
import java.util.*;

public class LimitedDeck<T> implements Deck<T> {
    private Queue<T> remainingCards;

    public LimitedDeck () {
        remainingCards = new LinkedList<>();
    }

    @Override
    public void addToDiscard(T t) { throw new UnsupportedOperationException();}

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
