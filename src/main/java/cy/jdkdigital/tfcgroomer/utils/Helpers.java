package cy.jdkdigital.tfcgroomer.utils;

public final class Helpers {
    public static String toCamelCase(String string) {
        String[] words = string.split("_");
        StringBuilder result = new StringBuilder();
        for (String word : words) {
            if (word.isEmpty()) continue;
            word = word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase();
            result.append(word);
        }
        return result.toString();
    }


}
