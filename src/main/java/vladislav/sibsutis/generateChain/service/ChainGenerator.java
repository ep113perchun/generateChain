package vladislav.sibsutis.generateChain.service;

import java.util.*;
import java.util.regex.*;

public class ChainGenerator {

    public List<String> validateAlphabet(String input) {
        String[] symbols = input.split(",");
        List<String> alphabet = new ArrayList<>();
        for (String symbol : symbols) {
            if (symbol.matches("^[a-zA-Z0-9]$")) {
                alphabet.add(symbol);
            } else {
                return new ArrayList<>();
            }
        }
        return alphabet;
    }

    public String generateRegex(List<String> alphabet, String startSubstring, String endSubstring, int lengthMultiplier) {
        String alphabetPattern = "[" + String.join("", alphabet) + "]";

        int fixedLength = startSubstring.length() + endSubstring.length();          // длина суммы подстрок
        int remainder = fixedLength % lengthMultiplier;                             // длина подстрок % кратность
        int neededSymbols = (remainder == 0) ? 0 : (lengthMultiplier - remainder);  // сколько символов нужно добавить

        String mandatoryPattern = alphabetPattern.repeat(Math.max(0, neededSymbols));

        String repeatingPattern = alphabetPattern.repeat(Math.max(0, lengthMultiplier));

        return "^" + Pattern.quote(startSubstring) + mandatoryPattern + "(" + repeatingPattern + ")*" + Pattern.quote(endSubstring) + "$";
    }

    public String generateRegex2(String startSubstring, String endSubstring, int maxLength) {
        int startLength = startSubstring.length();
        int endLength = endSubstring.length();

        for (int overlap = Math.min(startLength, endLength); overlap > 0; overlap--) {
            boolean matches = true;
            for (int i = 0; i < overlap; i++) {
                if (startSubstring.charAt(startLength - overlap + i) != endSubstring.charAt(i)) {
                    matches = false;
                    break;
                }
            }

            if (matches && (startLength + endLength - overlap) <= maxLength) {
                String adjustedEnd = endSubstring.substring(overlap);
                return "^" + Pattern.quote(startSubstring) + ".{0," + (maxLength - startLength - adjustedEnd.length()) + "}" + Pattern.quote(adjustedEnd) + "$";
            }
        }

        throw new IllegalArgumentException("Невозможно создать цепочку длиной <= " + maxLength);
    }

    public List<String> generateChainsFromRegex(String regex, List<String> alphabet, int lengthMultiplier, int maxLength) {
        List<String> result = new ArrayList<>();
        Pattern pattern = Pattern.compile(regex);

        for (int length = lengthMultiplier; length <= maxLength; length += lengthMultiplier) {
            generateCombinations(result, alphabet, "", length, pattern);
        }

        return result;
    }

    private void generateCombinations(List<String> result, List<String> alphabet, String current, int maxLength, Pattern pattern) {
        if (current.length() == maxLength && pattern.matcher(current).matches()) {
            result.add(current);
            return;
        }

        if (current.length() >= maxLength) return;

        for (String symbol : alphabet) {
            generateCombinations(result, alphabet, current + symbol, maxLength, pattern);
        }
    }
}