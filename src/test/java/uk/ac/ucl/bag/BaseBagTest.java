package uk.ac.ucl.bag;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

@Ignore
public class BaseBagTest {

    private BagFactory<String> bagFactory;
    protected Bag<String> bag;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    public BaseBagTest(String bagType) {
        bagFactory = BagFactory.getInstance();
        bagFactory.setBagClass(bagType);
    }

    @Before
    public void setUp() throws BagException {
        bag = bagFactory.getBag();
    }

    @Test
    public void testAdd_oneItem() throws BagException {
        bag.add("milk");
        bag.add("bread");
        bag.add("biscuits");
        assertBagHasSize(3);
        assertBagHasItem("milk", 1);
        assertBagHasItem("bread", 1);
        assertBagHasItem("biscuits", 1);
        assertBagUniqueItems(Arrays.asList("milk", "bread", "biscuits"));
        assertBagHasExactItems(Arrays.asList("milk", "bread", "biscuits"));
    }

    @Test
    public void testAdding_multipleItems() throws BagException {
        bag.add("milk");
        bag.add("milk");
        bag.add("milk");
        bag.add("bread");
        bag.add("biscuits");
        bag.add("biscuits");
        assertBagHasSize(3);
        assertBagHasItem("milk", 3);
        assertBagHasItem("bread", 1);
        assertBagHasItem("biscuits", 2);
        assertBagUniqueItems(Arrays.asList("milk", "bread", "biscuits"));
        assertBagHasExactItems(Arrays.asList("milk", "milk", "milk", "bread", "biscuits", "biscuits"));

    }

    @Test
    public void testAdding_bagFull() throws BagException {
        thrown.expect(BagException.class);
        bag = bagFactory.getBag(1);
        bag.add("milk");
        bag.add("bread");
    }

    @Test
    public void testAddWithOccurrences_zero() throws BagException {
        bag.addWithOccurrences("milk", 0);
        assertBagHasSize(0);
        assertBagHasItem("milk", 0);
    }

    @Test
    public void testAddWithOccurrences() throws BagException {
        bag.addWithOccurrences("milk", 10);
        assertBagHasSize(1);
        assertBagHasItem("milk", 10);
    }

    @Test
    public void testToString() throws BagException {
        bag.addWithOccurrences("milk", 3);
        bag.addWithOccurrences("bread", 2);
        bag.addWithOccurrences("biscuits", 1);
        assertThat(bag.toString(), startsWith("[ "));
        assertThat(bag.toString(), endsWith(" ]"));
        assertThat(bag.toString(), containsString("milk: 3"));
        assertThat(bag.toString(), containsString("bread: 2"));
        assertThat(bag.toString(), containsString("biscuits: 1"));
    }

    @Test
    public void testSubtract() throws BagException {
        bag.addWithOccurrences("milk", 3);
        bag.addWithOccurrences("bread", 2);
        bag.addWithOccurrences("biscuits", 1);
        Bag<String> bagToSubtract = bagFactory.getBag();
        bagToSubtract.add("milk");
        bagToSubtract.addWithOccurrences("bread", 2);

        bag.subtract(bagToSubtract);

        assertBagHasItem("milk", 2);
        assertBagHasItem("bread", 0);
        assertBagHasItem("biscuits", 1);
        assertBagUniqueItems(Arrays.asList("milk", "biscuits"));
        assertBagHasExactItems(Arrays.asList("milk", "milk", "biscuits"));
    }

    protected void assertBagHasSize(int size) {
        assertThat(bag.size(), equalTo(size));
    }

    protected void assertBagHasItem(String item, int occurrence) {
        assertThat(bag.countOf(item), equalTo(occurrence));
    }

    protected void assertBagUniqueItems(List<String> items) {
        int itemsInBag = 0;
        for (String item : bag) {
            itemsInBag++;
            assertThat(items.contains(item), equalTo(true));
        }
        assertThat(itemsInBag, equalTo(items.size()));
    }

    protected void assertBagHasExactItems(List<String> items) {
        List<String> actualItems = new ArrayList<>();
        for (Iterator<String> it = bag.allOccurrencesIterator(); it.hasNext(); ) {
            String item = it.next();
            actualItems.add(item);
        }
        // Sort both list as we don't care about order
        Collections.sort(actualItems);
        Collections.sort(items);
        assertThat(actualItems, equalTo(items));
    }
}
