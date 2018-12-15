package designParts;

public interface Observable {
    void addObserver(Observer observer);
    void notifyObservers();
}
