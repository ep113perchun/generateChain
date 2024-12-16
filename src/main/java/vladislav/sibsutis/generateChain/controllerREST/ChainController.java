package vladislav.sibsutis.generateChain.controllerREST;

import org.springframework.web.bind.annotation.*;
import vladislav.sibsutis.generateChain.service.ChainGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/chains")
public class ChainController {

    @PostMapping("/generate")
    public Map<String, Object> generateChains(@RequestBody Map<String, Object> request) {

        ChainGenerator chainGenerator = new ChainGenerator();

        String alphabetInput = (String) request.get("alphabet");
        String startSubstring = (String) request.get("startSubstring");
        String endSubstring = (String) request.get("endSubstring");
        int lengthMultiplier = (int) request.get("lengthMultiplier");
        int maxLength = (int) request.get("maxLength");

        List<String> alphabet = chainGenerator.validateAlphabet(alphabetInput);
        if (alphabet.isEmpty()) {
            return Map.of("error", "Некорректный алфавит");
        }

        String regex;

        if (startSubstring.length() + endSubstring.length() > maxLength) {
            regex = chainGenerator.generateRegex2(startSubstring, endSubstring, maxLength);
        } else {
            regex = chainGenerator.generateRegex(alphabet, startSubstring, endSubstring, lengthMultiplier);
        }
        List<String> chains = chainGenerator.generateChainsFromRegex(regex, alphabet, lengthMultiplier, maxLength);

        return Map.of(
                "regex", regex,
                "chains", chains
        );
    }

    @PostMapping("/generatePB")
    public Map<String, Object> generateChainsByRegex(@RequestBody Map<String, Object> request) {

        ChainGenerator chainGenerator = new ChainGenerator();

        String regexInput = (String) request.get("regex");

        List<String> alphabet = new ArrayList<>();
        int lengthMultiplier = 1;

        try {
            Pattern alphabetPattern = Pattern.compile("\\[([^\\]]+)\\]");
            Matcher alphabetMatcher = alphabetPattern.matcher(regexInput);
            if (alphabetMatcher.find()) {
                String rawAlphabet = alphabetMatcher.group(1);
                alphabet = Arrays.asList(rawAlphabet.split(""));
            }

            Pattern lengthMultiplierPattern = Pattern.compile("\\{(\\d+),\\d*}");
            Matcher lengthMultiplierMatcher = lengthMultiplierPattern.matcher(regexInput);
            if (lengthMultiplierMatcher.find()) {
                lengthMultiplier = Integer.parseInt(lengthMultiplierMatcher.group(1));
            }

        } catch (Exception e) {
            return Map.of("error", "Ошибка при разборе регулярного выражения: " + e.getMessage());
        }

        if (alphabet.isEmpty()) {
            return Map.of("error", "Не удалось извлечь алфавит из регулярного выражения");
        }

        List<String> chains = chainGenerator.generateChainsFromRegex(regexInput, alphabet, lengthMultiplier, 9);

        return Map.of(
                "regex", regexInput,
                "chains", chains
        );
    }
}