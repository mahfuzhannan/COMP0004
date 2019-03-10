package uk.ac.ucl.bag;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MapBag<T extends Comparable> extends AbstractBag<T> {

    private Map<T, Integer> bag;

    public MapBag() {
        super();
        bag = new HashMap<>();
    }

    public MapBag(int maxSize) throws BagException {
        super(maxSize);
        bag = new HashMap<>();
    }

    private boolean isBagFull() throws BagException {
        if (size() == maxSize) {
            throw new BagException("Bag is full");
        }
        return false;
    }

    @Override
    public void add(T item) throws BagException {
        addWithOccurrences(item, 1);
    }

    @Override
    public void addWithOccurrences(T item, int occurrences) throws BagException {
        if (occurrences > 0) {
            if (contains(item)) {
                bag.compute(item, (key, val) -> val == null ? occurrences : val + occurrences);
            } else {
                if (!isBagFull()) {
                    bag.put(item, occurrences);
                }
            }
        }
    }

    @Override
    public boolean contains(T item) {
        return bag.containsKey(item);
    }

    @Override
    public int countOf(T item) {
        return bag.getOrDefault(item, 0);
    }

    @Override
    public void remove(T item) {
        if (contains(item)) {
            if (bag.get(item) == 1) {
                bag.remove(item);
            } else {
                bag.compute(item, (key, val) -> val == null ? 0 : val - 1);
            }
        }
    }

    @Override
    public int size() {
        return bag.size();
    }

    @Override
    public boolean isEmpty() {
        return bag.isEmpty();
    }

    @Override
    public Iterator<T> allOccurrencesIterator() {
        return new MapBagIterator();
    }

    @Override
    public Iterator<T> iterator() {
        return bag.keySet().iterator();
    }

    private class MapBagIterator implements Iterator<T> {
        private Iterator<T> items;
        private T current;
        private int count = 0;

        public MapBagIterator() {
            items = bag.keySet().iterator();
        }

        @Override
        public boolean hasNext() {
            return items.hasNext() || count != 0;
        }

        @Override
        public T next() {
            if (count == 0) {
                current = items.next();
            }
            if (++count == bag.get(current)) {
                count = 0;
            }
            return current;
        }
    }

}
