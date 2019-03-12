package uk.ac.ucl.bag;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import uk.ac.ucl.bag.exceptions.BagException;

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

    @Test
    public void testBag_withArrayList() throws BagException {
        // Comparator will check if lists are not null,
        // if not null run compare to for strings of first element
        Bag<List<String>> bag = BagFactory.getInstance().getBag((Comparator<List<String>>) (o1, o2) -> {
            if (o1 == null && o2 == null) {
                return 0;
            }
            if (o1 == null && o2 != null) {
                return 1;
            }
            if (o1 != null && o2 == null) {
                return -1;
            }
            return o1.get(0).compareTo(o2.get(0));
        });

        // these should be the same
        bag.add(Arrays.asList("milk", "milk", "milk"));
        bag.add(Arrays.asList("milk"));

        bag.addWithOccurrences(Arrays.asList("bread"), 3);

        assertThat(bag.toString(), equalTo("[ [bread]: 3, [milk, milk, milk]: 2 ]"));
    }

    @Test
    public void testBag_withBag() throws BagException {
        // Comparator will check bags by size a bit pointless but you know...
        Bag<Bag<String>> bag = BagFactory.getInstance().getBag((Comparator<Bag<String>>) (o1, o2) -> {
            return o1.size() - o2.size();
        });

        // these should be the same
        Bag<String> bigBag = BagFactory.getInstance().getBag();
        bigBag.add("milk");
        bigBag.add("bread");
        bigBag.add("biscuits");
        bag.add(bigBag);

        assertThat(bag.toString(), equalTo("[ [ biscuits: 1, bread: 1, milk: 1 ]: 1 ]"));
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
