package it.polimi.se2019.model.board;

import java.util.*;
import java.util.stream.Collectors;

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

    @Override
    public T draw() {
        if (remainingCards.isEmpty()) {
            List<T> tempArray = discardedCards.stream().collect(Collectors.toList());
            Collections.shuffle(tempArray);
            remainingCards = new LinkedList<>(tempArray);
            discardedCards = new LinkedList<>();
        }
        return remainingCards.poll();
    }
}
