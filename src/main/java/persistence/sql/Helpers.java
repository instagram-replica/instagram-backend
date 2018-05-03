package persistence.sql;

import org.joda.time.DateTime;

import java.sql.Timestamp;
import java.util.Date;

public class Helpers {
  /** Constructs a list of question marks that is usable in WHERE IN queries */
  public static String constructPlaceholdersList(int count) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("(");

    for (int i = 0; i < count; i++) {
      stringBuilder.append("?");

      if (i != count - 1) {
        stringBuilder.append(", ");
      }
    }

    stringBuilder.append(")");
    return stringBuilder.toString();
  }

  public static Timestamp toTimestamp(DateTime dateTime) {
    return dateTime == null ? null : new Timestamp(dateTime.getMillis());
  }

  public static DateTime toJodaDateTime(Date date) {
    return date == null ? null : new DateTime(date);
  }
}
