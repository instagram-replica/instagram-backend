package persistence.sql;

import java.util.ArrayList;

public class Helpers {
    /**
     * Constructs a list of string values that is usable in WHERE IN queries
     */
    public static String constructList(ArrayList<String> values) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < values.size(); i++) {
            stringBuilder
                    .append("'")
                    .append(values.get(i))
                    .append("'");

            if (i != values.size() - 1) {
                stringBuilder.append(", ");
            }
        }

        return stringBuilder.toString();
    }
}
