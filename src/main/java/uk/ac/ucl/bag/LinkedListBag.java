package uk.ac.ucl.bag;

import java.util.Iterator;

public class LinkedListBag<T extends Comparable> extends AbstractBag<T> {

    private Element<T> bag;

    public LinkedListBag(int maxSize) throws BagException {
        super(maxSize);
    }

    public LinkedListBag() {
        super();
    }

    private static class Element<E> {
        public E value;
        public int occurrences;
        public Element<E> next;

        public Element(E value, int occurrences, Element<E> next) {
            this.value = value;
            this.occurrences = occurrences;
            this.next = next;
        }
    }

    /**
     * Recursively iterate over bag nodes, to find element
     *
     * @param value - value to search for
     * @param bag   - the linked list
     * @return - null value not in beg, else the element with the item
     */
    private Element findElementInBag(T value, Element bag) {
        if (bag == null) {
            // Reached the end of the bag return null as item not in bag
            return null;
        } else if (value.equals(bag.value)) {
            // Found item in bag, return current element
            return bag;
        }
        // Check next element in bag, recursively
        return findElementInBag(value, bag.next);
    }

    /**
     * Check if bag is full
     *
     * @return - false if bag not full
     * @throws BagException - when bag is full
     */
    private boolean isBagFull() throws BagException {
        if (size() == maxSize) {
            throw new BagException("Bag is full");
        }
        return false;
    }

    @Override
    public void add(T value) throws BagException {
        addWithOccurrences(value, 1);
    }

    @Override
    public void addWithOccurrences(T value, int occurrences) throws BagException {
        if (occurrences > 0) {
            if (bag == null) {
                bag = new Element<>(value, occurrences, null);
            } else {
                Element element = findElementInBag(value, bag);
                if (element == null) {
                    if (!isBagFull()) {
                        bag = new Element<>(value, occurrences, bag);
                    }
                } else {
                    element.occurrences += occurrences;
                }
            }
        }
    }

    @Override
    public boolean contains(T value) {
        return findElementInBag(value, bag) != null;
    }

    @Override
    public int countOf(T value) {
        Element element = findElementInBag(value, bag);
        return element == null ? 0 : element.occurrences;
    }

    @Override
    public void remove(T value) {
        Element element = findElementInBag(value, bag);
        element.occurrences -= 1;
        if (element.occurrences == 0) {
            removeElement(value, bag);
        }
    }

    /**
     * Remove element from bag recursively
     *
     * @param value - item to remove
     * @param bag   - the bag to iterate
     */
    private void removeElement(T value, Element bag) {
        if (bag.next == null) {
            // Reached the end of the bag, check the last item
            if (value.equals(bag.value)) {
                // Remove the element
                this.bag = null;
            }
            return;
        } else if (bag.next.value == value) {
            // Next item matches, lets remove by pointing current to the next next
            // Say we are looking for bread:
            //  milk -> bread -> biscuits => milk -> biscuits
            bag.next = bag.next.next;
            return;
        }
        removeElement(value, bag.next);
    }

    @Override
    public int size() {
        int count = 0;
        for (Element element = bag; element != null; element = element.next) {
            count++;
        }
        return count;
    }

    @Override
    public boolean isEmpty() {
        return bag == null;
    }

    @Override
    public Iterator<T> allOccurrencesIterator() {
        return new LinkedListBagIterator();
    }

    @Override
    public Iterator<T> iterator() {
        return new LinkedListBagUniqueIterator();
    }

    private class LinkedListBagIterator implements Iterator<T> {
        private T value;
        private Element<T> current = bag;
        private int count = 0;

        @Override
        public boolean hasNext() {
            // true while we aren't at the end of the list
            return current != null;
        }

        @Override
        public T next() {
            if (value == null || !value.equals(current.value)) {
                // if value is null or value is not the current value
                value = current.value;
            }

            // check count matches current occurance
            if (++count == current.occurrences) {
                // reset count as we have reached the end
                count = 0;
                current = current.next;
            }
            return value;
        }
    }

    private class LinkedListBagUniqueIterator implements Iterator<T> {
        private Element<T> current = bag;

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public T next() {
            T value = current.value;
            current = current.next;
            return value;
        }
    }
}
