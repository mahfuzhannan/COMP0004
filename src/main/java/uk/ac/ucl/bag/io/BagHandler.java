package uk.ac.ucl.bag.io;

import uk.ac.ucl.bag.Bag;
import uk.ac.ucl.bag.BagFactory;
import uk.ac.ucl.bag.exceptions.BagException;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BagHandler<T extends Object & Comparable> {

    public void write(Bag<T> bag, String filename) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename));
        List<T> objects = new ArrayList<>();
        for (Iterator<T> it = bag.allOccurrencesIterator(); it.hasNext(); ) {
            T item = it.next();
            objects.add(item);
        }
        oos.writeObject(objects);
        oos.close();
    }

    public Bag<T> read(String filename) throws IOException, ClassNotFoundException, BagException {
        Bag<T> bag = BagFactory.getInstance().getBag();
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename));
        List<T> objects = (List<T>) ois.readObject();

        for(T item: objects) {
            bag.add(item);
        }
        return bag;
    }
}
