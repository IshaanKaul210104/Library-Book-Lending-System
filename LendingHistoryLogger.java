import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LendingHistoryLogger {
    private static final String FILE_NAME = "history.txt";

    public static void log(String action, Member member, Book book) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String entry = String.format("[%s] %s - Book: %s (%s), Member: %s (%s)\n",
                timestamp, action, book.getTitle(), book.getBookId(), member.getName(), member.getMemberId());

        try (FileWriter fw = new FileWriter(FILE_NAME, true)) {
            fw.write(entry);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
