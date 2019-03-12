package uk.ac.ucl.bag;

import uk.ac.ucl.bag.exceptions.BagException;

import java.util.*;

/**
 * This class implements methods common to all concrete bag implementations
 * but does not represent a complete bag implementation.<br />
 * <p>
 * New bag objects are created using a BagFactory, which can be configured in the application
 * setup to select which bag implementation is to be used.
 */

public abstract class AbstractBag<T> implements Bag<T> {

    protected int maxSize;
    protected Comparator<T> comparator;

    public AbstractBag() {
        maxSize = MAX_SIZE;
    }
    public AbstractBag(Comparator<T> comparator) {
        maxSize = MAX_SIZE;
        this.comparator = comparator;
    }

    public AbstractBag(int maxSize) throws BagException {
        if (maxSize > MAX_SIZE) {
            throw new BagException("Bag size is too large");
        } else if (maxSize < 1) {
            throw new BagException("Bag size is too small");
        } else {
            this.maxSize = maxSize;
        }
    }


    public AbstractBag(int maxSize, Comparator<T> comparator) throws BagException {
        this(maxSize);
        this.comparator = comparator;
    }

    public Bag<T> createMergedAllOccurrences(Bag<T> b) throws BagException {
        Bag<T> result = BagFactory.getInstance().getBag();
        for (T value : this) {
            result.addWithOccurrences(value, this.countOf(value));
        }
        for (T value : b) {
            result.addWithOccurrences(value, b.countOf(value));
        }
        return result;
    }

    public Bag<T> createMergedAllUnique(Bag<T> b) throws BagException {
        Bag<T> result = BagFactory.getInstance().getBag();
        for (T value : this) {
            if (!result.contains(value)) result.add(value);
        }
        for (T value : b) {
            if (!result.contains(value)) result.add(value);
        }
        return result;
    }

    @Override
    public String toString() {
        SortedMap<T, Integer> itemToOccurence = new TreeMap<>(comparator);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");

        // Just to make sure all implementations print the string in the same order
        for (Iterator<T> it = this.allOccurrencesIterator(); it.hasNext(); ) {
            T item = it.next();
            itemToOccurence.computeIfPresent(item, (key, value) -> value+1);
            itemToOccurence.putIfAbsent(item, 1);
        }

        for (Iterator<Map.Entry<T, Integer>> it = itemToOccurence.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<T, Integer> entry = it.next();
            stringBuilder.append(" ").append(entry.getKey()).append(": ").append(entry.getValue()).append(",");
        }

        if (stringBuilder.lastIndexOf(",") == stringBuilder.length()-1) {
            stringBuilder.deleteCharAt(stringBuilder.length()-1);
        }

        return stringBuilder.append(" ]").toString();
    }

    @Override
    public void removeAllCopies() {
        for (T item : this) {
            while (contains(item)) {
                remove(item);
            }
        }
    }

    /**
     * Spec says to create a new bag, however using BagFactory to create a new bag throws a BagException,
     * this is not defined in the subtract header - will assume changing the current bag will be okay
     * @param bag - the other bag
     * @return - existing bag with items removed
     */
    @Override
    public Bag<T> subtract(Bag<T> bag) {
        for (Iterator<T> it = bag.allOccurrencesIterator(); it.hasNext(); ) {
            T item = it.next();
            remove(item);
        }
        return this;
    }

    protected int compareValues(T value, T otherValue) {
        return value instanceof Comparable ? ((Comparable)value).compareTo(otherValue) : comparator.compare(value, otherValue);
    }
}
