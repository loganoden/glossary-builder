import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import components.map.Map;
import components.map.Map1L;
import components.queue.Queue;
import components.queue.Queue1L;
import components.set.Set;
import components.set.Set1L;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;

/**
 * JUnit test cases for the Glossary class methods. Author: Logan Oden
 */
public class GlossaryTest {

    /**
     * Challenging test case for the nextWordOrSeparator method. Tests when the
     * first character is a separator.
     */
    @Test
    public void testNextWordOrSeparator_Challenging() {
        Set<Character> separators = new Set1L<>();
        separators.add(' ');
        separators.add(',');

        String text = "Hello, world!";
        int position = 5;

        String result = Glossary.nextWordOrSeparator(text, position,
                separators);
        assertEquals(", ", result);
    }

    /**
     * Routine test case for the nextWordOrSeparator method. Tests when the
     * first character is not a separator.
     */
    @Test
    public void testNextWordOrSeparator_Routine() {
        Set<Character> separators = new Set1L<>();
        separators.add(' ');
        separators.add(',');

        String text = "Hello, world!";
        int position = 0;

        String result = Glossary.nextWordOrSeparator(text, position,
                separators);
        assertEquals("Hello", result);
    }

    /**
     * Challenging test case for the mapFromInputLines method. Tests with an
     * input file containing unexpected data.
     */
    @Test
    public void testMapFromInputLines_Challenging() {
        SimpleReader input = new SimpleReader1L("data\\terms_test.txt");

        Map<String, String> resultMap = Glossary.mapFromInputLines(input);

        assertEquals(resultMap.hasKey("Java"), true);
        assertEquals(resultMap.hasKey("key"), false);
    }

    /**
     * Routine test case for the mapFromInputLines method. Tests with a standard
     * input file.
     */
    @Test
    public void testMapFromInputLines_Routine() {
        SimpleReader input = new SimpleReader1L("data\\terms_test.txt");

        Map<String, String> expectedMap = new Map1L<>();
        expectedMap.add("Java", "A programming language.");
        expectedMap.add("JUnit", "A testing framework for Java.");

        Map<String, String> resultMap = Glossary.mapFromInputLines(input);

        assertEquals(expectedMap, resultMap);
    }

    /**
     * Edge test case for the mapFromInputLines method. Tests with an empty
     * input file.
     */
    @Test
    public void testMapFromInputLines_Edge() {
        SimpleReader input = new SimpleReader1L("data\\empty_file.txt");

        Map<String, String> resultMap = Glossary.mapFromInputLines(input);

        assertTrue(resultMap.size() == 0);
    }

    /**
     * Challenging test case for the SortingKeys method. Sorting a map with
     * entries in arbitrary order.
     */
    @Test
    public void testSortingKeys_Challenging() {
        Map<String, String> mapToSort = new Map1L<>();
        mapToSort.add("C", "Definition of C");
        mapToSort.add("A", "Definition of A");
        mapToSort.add("B", "Definition of B");

        Queue<String> expectedQueue = new Queue1L<>();
        expectedQueue.enqueue("A");
        expectedQueue.enqueue("B");
        expectedQueue.enqueue("C");

        Queue<String> resultQueue = Glossary.sortingKeys(mapToSort);

        assertEquals(expectedQueue, resultQueue);
    }

    /**
     * Routine test case for the SortingKeys method. Sorting a map with entries
     * in alphabetical order.
     */
    @Test
    public void testSortingKeys_Routine() {
        Map<String, String> mapToSort = new Map1L<>();
        mapToSort.add("Java", "A programming language.");
        mapToSort.add("Python", "A high-level programming language.");
        mapToSort.add("C++", "A general-purpose programming language.");

        Queue<String> expectedQueue = new Queue1L<>();
        expectedQueue.enqueue("C++");
        expectedQueue.enqueue("Java");
        expectedQueue.enqueue("Python");

        Queue<String> resultQueue = Glossary.sortingKeys(mapToSort);

        assertEquals(expectedQueue, resultQueue);
    }

    /**
     * Edge test case for the SortingKeys method. Sorting an empty map.
     */
    @Test
    public void testSortingKeys_Edge() {
        Map<String, String> mapToSort = new Map1L<>();

        // Test with an empty map
        Queue<String> resultQueue = Glossary.sortingKeys(mapToSort);

        assertTrue(resultQueue.length() == 0);
    }

    /**
     * Challenging test case for the processTerm method. Tests with a term
     * containing special characters.
     */
    @Test
    public void testProcessTerm_Challenging() {
        String term = "Java@Programming!";
        String definition = "A programming language.";

        Map<String, String> glossary = new Map1L<>();
        glossary.add(term, definition);
        Queue<String> terms = new Queue1L<>();
        terms.enqueue(term);

        Map.Pair<String, String> resultPair = glossary.removeAny();

        Glossary.processTerm(terms, resultPair, "data");

        assertNotNull(resultPair);
        assertEquals(term, resultPair.key());
        assertEquals(definition, resultPair.value());
    }

    /**
     * Routine test case for the processTerm method. Tests with a standard term
     * and definition.
     */
    @Test
    public void testProcessTerm_Routine() {
        String term = "Java";
        String definition = "A programming language.";

        Map<String, String> glossary = new Map1L<>();
        glossary.add(term, definition);
        Queue<String> terms = new Queue1L<>();
        terms.enqueue(term);

        Map.Pair<String, String> resultPair = glossary.removeAny();

        Glossary.processTerm(terms, resultPair, "data");

        assertNotNull(resultPair);
        assertEquals(term, resultPair.key());
        assertEquals(definition, resultPair.value());
    }

    /**
     * Edge test case for the processTerm method. Tests with an empty term and
     * definition.
     */
    @Test
    public void testProcessTerm_Edge() {
        String term = "";
        String definition = "";

        Map<String, String> glossary = new Map1L<>();
        glossary.add(term, definition);
        Queue<String> terms = new Queue1L<>();
        terms.enqueue(term);

        Map.Pair<String, String> resultPair = glossary.removeAny();
        Glossary.processTerm(terms, resultPair, "data");
    }

    /**
     * Challenging test case for the processTerm method. Tests with a term and
     * definition containing only spaces.
     */
    @Test
    public void testProcessTerm_Challenging2() {
        String term = "   ";
        String definition = "    ";

        Map<String, String> glossary = new Map1L<>();
        glossary.add(term, definition);
        Queue<String> terms = new Queue1L<>();
        terms.enqueue(term);

        Map.Pair<String, String> resultPair = glossary.removeAny();

        Glossary.processTerm(terms, resultPair, "data");

        assertNotNull(resultPair);
        assertEquals(term, resultPair.key());
        assertEquals("    ", resultPair.value());
    }

}
