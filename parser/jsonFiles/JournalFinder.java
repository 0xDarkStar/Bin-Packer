package parser.jsonFiles;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JournalFinder {
    final static String regex = "Journal\\.(\\d{4}-\\d{2}-\\d{2})T(\\d{6}).(\\d{2}).log";
    final static Pattern pattern = Pattern.compile(regex);
    final static String journalDir = System.getProperty("user.home") + "\\Saved Games\\Frontier Developments\\Elite Dangerous";

    public static File findLatestJournal(List<String> ignored) {
        File dir = new File(journalDir);
        File[] files = dir.listFiles((d, name) -> name.startsWith("Journal.") && name.endsWith(".log"));

        if (files == null || files.length == 0) {
            return null;
        }

        File latestFile = null;
        LocalDateTime latestDateTime = null;
        int latestPart = -1;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HHmmss");

        for (File file : files) {
            Matcher matcher = pattern.matcher(file.getName());
            if (matcher.matches() && !ignored.contains(file.getName())) {
                String dateStr = matcher.group(1);
                String timeStr = matcher.group(2);
                int part = Integer.parseInt(matcher.group(3));
                LocalDateTime dateTime = LocalDateTime.parse(dateStr+"T"+timeStr, formatter);

                if (latestDateTime == null ||
                    dateTime.isAfter(latestDateTime) ||
                    (dateTime.isEqual(latestDateTime) && part > latestPart)) {
                    
                    latestDateTime = dateTime;
                    latestPart = part;
                    latestFile = file;
                }
            }
        }
        return latestFile;
    }
}
