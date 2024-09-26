package org.translation;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Main class for this program.
 * Complete the code according to the "to do" notes.<br/>
 * The system will:<br/>
 * - prompt the user to pick a country name from a list<br/>
 * - prompt the user to pick the language they want it translated to from a list<br/>
 * - output the translation<br/>
 * - at any time, the user can type quit to quit the program<br/>
 */
public class Main {
    private static final String QUIT = "quit";

    /**
     * This is the main entry point of our Translation System!<br/>
     * A class implementing the Translator interface is created and passed into a call to runProgram.
     * @param args not used by the program
     */
    public static void main(String[] args) {
        Translator translator = new JSONTranslator("sample.json");
        runProgram(translator);
    }

    /**
     * This is the method which we will use to test your overall program, since
     * it allows us to pass in whatever translator object that we want!
     * See the class Javadoc for a summary of what the program will do.
     * @param translator the Translator implementation to use in the program
     */
    public static void runProgram(Translator translator) {

        LanguageCodeConverter languageCodeConverter = new LanguageCodeConverter();

        while (true) {
            String country = promptForCountry(translator);
            if (QUIT.equalsIgnoreCase(country)) {
                break;
            }
            String countryCode = translator.getCountries().stream()
                    .filter(code -> country.equalsIgnoreCase(translator.translate(code, "en")))
                    .findFirst()
                    .orElse(null);

            if (countryCode == null) {
                System.out.println("Invalid country selected.");
                continue;
            }

            String language = promptForLanguage(translator, countryCode, languageCodeConverter);
            if (QUIT.equalsIgnoreCase(language)) {
                break;
            }

            String languageCode = languageCodeConverter.fromLanguage(language);
            if (languageCode == null) {
                System.out.println("Invalid language selected.");
                continue;
            }
            String translatedCountry = translator.translate(countryCode, languageCode);
            System.out.println(country + " in " + language + " is " + translatedCountry);
            System.out.println("Press enter to continue or type 'quit' to exit.");
            Scanner s = new Scanner(System.in);
            String textTyped = s.nextLine();

            if (QUIT.equalsIgnoreCase(textTyped)) {
                break;
            }
        }
    }

    private static String promptForCountry(Translator translator) {
        List<String> countryNames = translator.getCountries().stream()
                .map(code -> translator.translate(code, "en"))
                .sorted()
                .collect(Collectors.toList());
        countryNames.forEach(System.out::println);

        System.out.println("Select a country from above or type 'quit' to exit:");
        Scanner s = new Scanner(System.in);
        return s.nextLine();
    }

    private static String promptForLanguage(Translator translator, String country,
                                            LanguageCodeConverter languageCodeConverter) {
        List<String> languageNames = translator.getCountryLanguages(country).stream()
                .map(languageCodeConverter::fromLanguageCode)
                .filter(name -> name != null && !name.isEmpty())
                .sorted()
                .collect(Collectors.toList());
        if (languageNames.isEmpty()) {
            System.out.println("No available languages for this country.");
            return "quit";
        }
        languageNames.forEach(System.out::println);
        System.out.println("Select a language from above or type 'quit' to exit:");
        Scanner s = new Scanner(System.in);
        return s.nextLine();
    }
}
