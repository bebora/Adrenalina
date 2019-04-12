package model.board;

public interface Deck<T>{
    public T draw();

    public void add(T t);

    public void addToDiscard(T t);
}
