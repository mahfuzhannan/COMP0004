package uk.ac.ucl.bag.io;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import uk.ac.ucl.bag.Bag;
import uk.ac.ucl.bag.BagFactory;
import uk.ac.ucl.bag.exceptions.BagException;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BagHandlerTest {

    @InjectMocks
    private BagHandler bagHandler;

    private String filename;

    @Before
    public void setUp() throws IOException {
        BagFactory.getInstance().setBagClass("ArrayBag");
        // Create directory data of not exists
        filename = Files.createTempFile( "bag", ".txt").getFileName().toString();
        bagHandler = new BagHandler<>();
    }

    @After
    public void tearDown() throws IOException {
        // Comment this out if you want to see the files after test is ran
        Files.deleteIfExists(Paths.get(filename));
    }

    @Test
    public void testReadAndWrite_String() throws BagException, IOException, ClassNotFoundException {
        Bag<String> bag = BagFactory.getInstance().getBag();
        bag.addWithOccurrences("milk", 5);
        bag.addWithOccurrences("bread", 3);
        bagHandler.write(bag, filename);

        Bag<String> resultBag = bagHandler.read(filename);

        assertThat(resultBag.size(), equalTo(bag.size()));
        assertThat(resultBag.toString(), equalTo(bag.toString()));
    }

    @Test
    public void testReadAndWrite_Integer() throws BagException, IOException, ClassNotFoundException {
        Bag<Integer> bag = BagFactory.getInstance().getBag();
        bag.addWithOccurrences(100, 5);
        bag.addWithOccurrences(100, 3);
        bagHandler.write(bag, filename);

        Bag<String> resultBag = bagHandler.read(filename);

        assertThat(resultBag.size(), equalTo(bag.size()));
        assertThat(resultBag.toString(), equalTo(bag.toString()));
    }

    @Test
    public void testReadAndWrite_Double() throws BagException, IOException, ClassNotFoundException {
        Bag<Double> bag = BagFactory.getInstance().getBag();
        bag.addWithOccurrences(100d, 5);
        bag.addWithOccurrences(100d, 3);
        bagHandler.write(bag, filename);

        Bag<String> resultBag = bagHandler.read(filename);

        assertThat(resultBag.size(), equalTo(bag.size()));
        assertThat(resultBag.toString(), equalTo(bag.toString()));
    }

    @Test
    public void testReadAndWrite_Float() throws BagException, IOException, ClassNotFoundException {
        Bag<Float> bag = BagFactory.getInstance().getBag();
        bag.addWithOccurrences(100f, 5);
        bag.addWithOccurrences(100f, 3);
        bagHandler.write(bag, filename);

        Bag<String> resultBag = bagHandler.read(filename);

        assertThat(resultBag.size(), equalTo(bag.size()));
        assertThat(resultBag.toString(), equalTo(bag.toString()));
    }

    @Test
    public void testReadAndWrite_Char() throws BagException, IOException, ClassNotFoundException {
        Bag<Character> bag = BagFactory.getInstance().getBag();
        bag.addWithOccurrences('z', 5);
        bag.addWithOccurrences('f', 3);
        bagHandler.write(bag, filename);

        Bag<String> resultBag = bagHandler.read(filename);

        assertThat(resultBag.size(), equalTo(bag.size()));
        assertThat(resultBag.toString(), equalTo(bag.toString()));
    }

    @Test
    public void read() {
    }
}