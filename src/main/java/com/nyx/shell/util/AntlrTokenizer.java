package com.nyx.shell.util;

import com.nyx.shell.ShellLexer;
import org.antlr.v4.runtime.*;

import java.util.ArrayList;
import java.util.List;

/**
 * A utility class that uses ANTLR to tokenize shell input while supporting:
 *
 * <ul>
 *   <li>Single quotes: Text between single quotes is taken literally (backslashes remain).</li>
 *   <li>Double quotes: Text between double quotes supports escape sequences (only for \\, $, ", or newline).</li>
 *   <li>Unquoted text: A backslash escapes the next character, which is removed from the output.</li>
 *   <li>Adjacent tokens: Tokens with no intervening whitespace are concatenated (e.g. "world""test" becomes "worldtest").</li>
 * </ul>
 *
 * <p>For more details on ANTLR see the
 * <a href="https://www.antlr.org/">ANTLR Official Website</a> and the
 * <a href="https://github.com/antlr/antlr4">ANTLR4 GitHub Repository</a>.
 * For more information on shell quoting, refer to the
 * <a href="https://www.gnu.org/software/bash/manual/html_node/Quoting.html">Bash Quoting</a> section of the GNU Bash Manual.</p>
 */
public class AntlrTokenizer {

    /**
     * Tokenizes the given input string using the ANTLR-generated ShellLexer.
     *
     * <p>The method creates a {@link CharStream} from the input and instantiates a {@link ShellLexer}.
     * It processes tokens as follows:
     * <ul>
     *   <li><b>SQ_STRING:</b> Removes the surrounding single quotes. Backslashes are left as-is.</li>
     *   <li><b>DQ_STRING:</b> Removes the surrounding double quotes and unescapes sequences for \, $, and ".</li>
     *   <li><b>WORD:</b> Processes unquoted text by removing backslashes that escape the following character.</li>
     * </ul>
     * Finally, adjacent tokens (without intervening whitespace) are combined.
     * </p>
     *
     * @param input the raw input string from the user.
     * @return an array of tokens extracted from the input.
     */
    public static String[] tokenize(String input) {
        // Create a CharStream from the raw input.
        CharStream charStream = CharStreams.fromString(input);
        // Instantiate the lexer.
        ShellLexer lexer = new ShellLexer(charStream);
        // Gather all tokens.
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        tokens.fill();

        // A list to hold processed token data (token text + start/stop positions).
        List<TokenData> tokenDataList = new ArrayList<>();

        // Process each token from ANTLR.
        for (Token token : tokens.getTokens()) {
            // Skip whitespace and EOF tokens.
            if (token.getType() == ShellLexer.WS || token.getType() == Token.EOF) {
                continue;
            }
            String text = token.getText();
            int type = token.getType();

            // Process single-quoted strings: remove the surrounding quotes.
            if (type == ShellLexer.SQ_STRING) {
                text = text.substring(1, text.length() - 1);
            }
            // Process double-quoted strings: remove surrounding quotes and unescape.
            else if (type == ShellLexer.DQ_STRING) {
                text = text.substring(1, text.length() - 1);
                text = unescapeDoubleQuoted(text);
            }
            // Process unquoted text (WORD): unescape backslashes.
            else if (type == ShellLexer.WORD) {
                text = unescapeWord(text);
            }

            // Save the processed token along with its original positions.
            tokenDataList.add(new TokenData(text, token.getStartIndex(), token.getStopIndex()));
        }

        // Combine tokens that are adjacent (no whitespace gap).
        List<String> combinedTokens = combineAdjacentTokens(tokenDataList);
        return combinedTokens.toArray(new String[0]);
    }

    /**
     * Unescapes escape sequences in double-quoted tokens.
     *
     * <p>This method replaces escape sequences (e.g. \" becomes ", \\ becomes \, \$ becomes $)
     * but only for characters that have special meaning inside double quotes.
     * See <a href="https://www.gnu.org/software/bash/manual/html_node/Quoting.html">Bash Quoting</a>
     * for details.</p>
     *
     * @param text the text within double quotes.
     * @return the text with escape sequences processed.
     */
    private static String unescapeDoubleQuoted(String text) {
        // Replace \ followed by ", \, or $ with the literal character.
        return text.replaceAll("\\\\([\"\\\\$])", "$1");
    }

    /**
     * Unescapes escape sequences in unquoted tokens.
     *
     * <p>In unquoted text, a backslash escapes the following character.
     * This method removes the backslash, leaving the literal character.
     * </p>
     *
     * @param text the unquoted token text.
     * @return the text with escape sequences processed.
     */
    private static String unescapeWord(String text) {
        return text.replaceAll("\\\\(.)", "$1");
    }

    /**
     * Combines adjacent tokens if they are contiguous in the input.
     *
     * <p>Two tokens are considered adjacent if the start index of the current token is
     * exactly one more than the stop index of the previous token.
     * This behavior emulates the shell's concatenation of adjacent quoted strings.</p>
     *
     * @param tokenDataList the list of TokenData to combine.
     * @return a list of combined token strings.
     */
    private static List<String> combineAdjacentTokens(List<TokenData> tokenDataList) {
        List<String> result = new ArrayList<>();
        if (tokenDataList.isEmpty()) {
            return result;
        }

        // Start with the first token.
        TokenData current = tokenDataList.get(0);
        StringBuilder combined = new StringBuilder(current.text);
        int currentStop = current.stop;

        // Iterate through subsequent tokens.
        for (int i = 1; i < tokenDataList.size(); i++) {
            TokenData next = tokenDataList.get(i);
            // If next token starts immediately after current token ends, they are adjacent.
            if (next.start == currentStop + 1) {
                combined.append(next.text);
                currentStop = next.stop;
            } else {
                result.add(combined.toString());
                combined = new StringBuilder(next.text);
                currentStop = next.stop;
            }
        }
        // Add the final combined token.
        result.add(combined.toString());
        return result;
    }

    /**
     * A helper class to store token text along with its start and stop indices.
     */
    private static class TokenData {
        final String text;
        final int start;
        final int stop;

        TokenData(String text, int start, int stop) {
            this.text = text;
            this.start = start;
            this.stop = stop;
        }
    }
}
