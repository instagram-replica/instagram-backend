package persistence.sql;

public class Helpers {
    /**
     * Constructs a list of string values that is usable in WHERE IN queries
     */
    public static String constructList(String[] values) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < values.length; i++) {
            stringBuilder
                    .append("'")
                    .append(values[i])
                    .append("'");

            if (i != values.length - 1) {
                stringBuilder.append(", ");
            }
        }

        return stringBuilder.toString();
    }
}