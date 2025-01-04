import java.util.Comparator;

import components.map.Map;
import components.map.Map1L;
import components.queue.Queue;
import components.queue.Queue1L;
import components.set.Set;
import components.set.Set1L;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;

/**
 * This program is designed to create an easy-to-maintain glossary facility,
 * following the instructions of the customer Cy Burnett.
 *
 * @author L. Oden
 *
 */
public final class Glossary {

    /**
     * No argument constructor--private to prevent instantiation.
     */
    private Glossary() {
    }

    /**
     * Returns the first "word" (maximal length string of characters not in
     * {@code separators}) or "separator string" (maximal length string of
     * characters in {@code separators}) in the given {@code text} starting at
     * the given {@code position}.
     *
     * @param text
     *            the {@code String} from which to get the word or separator
     *            string
     * @param position
     *            the starting index
     * @param separators
     *            the {@code Set} of separator characters
     * @return the first word or separator string found in {@code text} starting
     *         at index {@code position}
     * @requires 0 <= position < |text|
     * @ensures <pre>
     * nextWordOrSeparator =
     *   text[position, position + |nextWordOrSeparator|)  and
     * if entries(text[position, position + 1)) intersection separators = {}
     * then
     *   entries(nextWordOrSeparator) intersection separators = {}  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      intersection separators /= {})
     * else
     *   entries(nextWordOrSeparator) is subset of separators  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      is not subset of separators)
     * </pre>
     */
    public static String nextWordOrSeparator(String text, int position,
            Set<Character> separators) {
        int p = position;
        /*
         * isSeperator determines if the character at position p in text is part
         * of separator set
         */
        boolean isSeparator = separators.contains(text.charAt(p));
        /*
         * While within text length and each character is still its initial
         * condition (either a separator or not), keep incrementing p.
         */
        while (p < text.length()
                && separators.contains(text.charAt(p)) == isSeparator) {
            p++;
        }
        /*
         * Return substring of text from starting position to position reached
         * after ending loop p.
         */
        return text.substring(position, p);
    }

    /**
     * Returns a Map of the lines read from {@code input}, where the key (term)
     * is a 1-word, 1-line String, and the value (definition) is a several line,
     * multiple word String. The map cannot have duplicate keys (terms).
     *
     * @param input
     *            source of strings, one per line for keys (terms), and one per
     *            several lines for values (definitions).
     * @return Map of several terms and definitions read from the lines of
     *         {@code input}, where each Map pair is separated by an empty line
     *         in the input file.
     * @requires input.is_open
     * @ensures <pre>
     * input.is_open  and  input.content = <>  and
     * linesFromInput = [maximal Map of different lines from #input.content such that
     *                   CONTAINS_NO_DUPLICATE_KEYS(linesFromInput)]
     * </pre>
     */
    public static Map<String, String> mapFromInputLines(SimpleReader input) {
        assert input != null : "Violation of: input is not null";
        assert input.isOpen() : "Violation of: input.is_open";

        // Initialize new set to hold all input lines.
        Map<String, String> inputLinesMap = new Map1L<>();

        /*
         * While we're not at the end of stream, initialize term to the initial
         * line that SimpleReader input reads. Then, initialize definition by
         * adding each line that is not Empty. Finally, if the key is not
         * already in the Map, then add the Map pair of the term and definition
         * to the Map.
         */
        while (!input.atEOS()) {
            String term = input.nextLine();
            String currentLine = input.nextLine();
            String definition = "";
            while (!currentLine.isEmpty()) {
                definition += currentLine;
                currentLine = input.nextLine();
            }
            if (!inputLinesMap.hasKey(term)) {
                inputLinesMap.add(term, definition);
            }
        }
        // Return the set inputLinesSet.
        return inputLinesMap;
    }

    /**
     *
     * Returns a sorted Queue, holding the values of the keys of mapToSort. This
     * is based on the alphabetical order of the keys in each Map.Pair of the
     * input Map. This order is found by using a created String comparator. This
     * can be used later to get each Map.Pair from the Map in alphabetical
     * order.
     *
     * @param mapToSort
     *            The map of keys (terms) and values (definitions) that is used
     *            for sorting.
     *
     * @return The sorted Queue<String> of the mapToSort keys
     *
     * @requires mapToSort is not null
     *
     * @ensures Output Queue contains the keys from the input Map in
     *          alphabetical order.
     */
    public static Queue<String> sortingKeys(Map<String, String> mapToSort) {
        // Initialize String comparator using implementation in this java file.
        Comparator<String> sort = new StringLT();
        // Create temp Map variable as new instance, and transferFrom mapToSort
        Map<String, String> temp = mapToSort.newInstance();
        temp.transferFrom(mapToSort);
        /*
         * Initialize keys to be a new Queue which will hold the value of the
         * keys of temp
         */
        Queue<String> keys = new Queue1L<>();
        /*
         * While temp still has elements, remove random Map.Pair from temp and
         * initialize termPlusDef. Enqueue the key of termPlusDef to keys. Add
         * these values back to mapToSort every time.
         */
        while (temp.size() > 0) {
            Map.Pair<String, String> termPlusDef = temp.removeAny();
            keys.enqueue(termPlusDef.key());
            mapToSort.add(termPlusDef.key(), termPlusDef.value());
        }
        /*
         * Sort the keys Queue using the String comparator initialized above.
         */
        keys.sort(sort);
        // Return sorted Queue.
        return keys;
    }

    /**
     * Processes a singlePair of term and definition from a Map, printing an
     * appropriate term & definition page for input Map.Pair to an HTML file
     * named after the Map.Pair.
     *
     * @param singlePair
     *            A single Map.Pair object from a Map
     * @param keys
     *            A queue containing all keys of a Map in alphabetical order.
     * @param outputFolder
     *            The folder where all output files are stored.
     * @ensures <pre>
     * [Saves HTML document with page of a Map.Pair's term and definition]
     * </pre>
     */
    public static void processTerm(Queue<String> keys,
            Map.Pair<String, String> singlePair, String outputFolder) {
        /*
         * Write code to new HTML page named based on key of the input
         * singlePair
         */
        SimpleWriter termPageFileOut = new SimpleWriter1L(
                outputFolder + "\\" + singlePair.key() + ".html");
        // Output headers of term HTML file
        /*
         * Opening HTML and head tags
         */
        termPageFileOut.println("<html>");
        termPageFileOut.println("<head>");
        // Opening title tag with key of singlePair as title of this term.
        termPageFileOut.println("<title>" + singlePair.key() + "</title>");
        // Close head tag
        termPageFileOut.println("</head>");
        // Open body tag
        termPageFileOut.println("<body>");
        // Print header in format specified in project instructions.
        termPageFileOut.println("<h1><em><b style='color:red;'>"
                + singlePair.key() + "</b></em></h1>");
        // Open paragraph tag
        termPageFileOut.println("<p>");

        // Initialize separators set. Add various different separators of words.
        Set<Character> separators = new Set1L<>();
        separators.add(' ');
        separators.add('.');
        separators.add(',');
        separators.add(';');
        separators.add(':');

        /*
         * Starting at position 0, indexTerm has not been found yet, and
         * wordOrSeparator is empty
         */
        int position = 0;
        boolean indexTerm = false;
        String wordOrSeparator = "";
        /*
         * Print &nbsp;, a single one representing a space in HTML, to match the
         * specified format.
         */
        termPageFileOut.print(
                "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"
                        + "&nbsp;&nbsp;");
        // While the position doesn't exceed length of description of a pair
        while (position < singlePair.value().length()) {
            indexTerm = false;
            /*
             * Call wordOrSeparator method to return either a full word or full
             * separator from the description/value
             */
            wordOrSeparator = nextWordOrSeparator(singlePair.value(), position,
                    separators);
            /*
             * For each string in keys (sorted Queue), if the wordOrSeparator
             * equals a value that is a key, then indexTerm is true.
             */
            for (String s : keys) {
                if (wordOrSeparator.equals(s)) {
                    indexTerm = true;
                }
            }
            /*
             * If indexTerm, then print the particular word as a link to the
             * HTML page of the term with that name. Otherwise, just print the
             * word/separator.
             */
            if (indexTerm) {
                termPageFileOut.print("<a href = \"" + wordOrSeparator
                        + ".html\">" + wordOrSeparator + "</a>");
            } else {
                termPageFileOut.print(wordOrSeparator);
            }
            // Increment position
            position += wordOrSeparator.length();
        }
        // Print closing paragraph header
        termPageFileOut.println("</p>");
        // Print horizontal line
        termPageFileOut.println("<hr>");
        // Print the return to index. button with a link to index page.
        termPageFileOut.println(
                "<p>Return to <a href = \"index.html\">index</a>.</p>");

        // Output the footer of term HTML file
        // Close body
        termPageFileOut.println("</body>");
        // Close HTML file
        termPageFileOut.println("</html>");
        termPageFileOut.close();
    }

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {
        SimpleReader inFromConsole = new SimpleReader1L();
        SimpleWriter outToConsole = new SimpleWriter1L();

        // Ask for input file and initialize inputFile
        outToConsole.print("Please enter the name of an input file: ");
        String inputFile = inFromConsole.nextLine();
        // Ask for output folder and initialize outputFolder.
        outToConsole.print(
                "Please enter the name of an output folder where all output "
                        + "files will be saved: ");
        String outputFolder = inFromConsole.nextLine();

        /*
         * inFromFile reads input from specified file, and outToFile writes
         * output to index.html in the specified folder.
         */
        SimpleReader inFromFile = new SimpleReader1L(inputFile);
        SimpleWriter outToFile = new SimpleWriter1L(
                outputFolder + "\\index.html");

        /*
         * Initialize termsAndDefinitions to a call to mapFromInputLines reading
         * from specified input file.
         */
        /*
         * Initialize sortedKeys to be a Queue holding the keys of
         * termsAndDefinitions in alphabetical order.
         */
        Map<String, String> termsAndDefinitions = mapFromInputLines(inFromFile);
        Queue<String> sortedKeys = sortingKeys(termsAndDefinitions);

        // Opening tag of an HTML document
        outToFile.println("<html>");

        // Opening tag of a head
        outToFile.println("<head>");

        // Opens title, prints "Index" as title, and closes title.
        outToFile.println("<title>Glossary</title>");

        // Closes the header
        outToFile.println("</head>");

        // Opens the body
        outToFile.println("<body>");

        // Opens h1, prints "Index"
        outToFile.println("<h1>Glossary</h1>");

        // Open second section with horizontal line divider
        outToFile.println("<hr>");

        // Open second header and print "Index"
        outToFile.println("<h2>Index</h2>");

        // Opens the bullet point list
        outToFile.println("<ul>");

        // For each string s in sortedKeys (same length as termsAndDefinitions)
        for (String s : sortedKeys) {
            /*
             * Single Map.Pair, starting from smallest alphabetically is a
             * result of removing pair from termsAndDefinitions at key s.
             */
            Map.Pair<String, String> single = termsAndDefinitions.remove(s);
            /*
             * Call process term to process this single Map.Pair and print the
             * appropriate separate HTML page for it.
             */
            processTerm(sortedKeys, single, outputFolder);
            /*
             * Creates an unordered list entry, and links the Map.Pair key name
             * to the page with that name.
             */
            outToFile.println("<li><a href = \"" + single.key() + ".html\">"
                    + single.key() + "</a></li>");
        }
        // Close bullet point list
        outToFile.println("</ul>");
        // Close body
        outToFile.println("</body>");
        // Close HTML file
        outToFile.println("</html>");

        // Print success generation message.
        outToConsole.println("HTML file successfully generated!");

        /*
         * Close input and output streams
         */
        inFromConsole.close();
        outToConsole.close();
        inFromFile.close();
        outToFile.close();
    }

    /**
     * Comparator for Strings, implementing Comparator<String> and overriding
     * compare method.
     */
    private static class StringLT implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            return o1.compareTo(o2);
        }
    }

}
