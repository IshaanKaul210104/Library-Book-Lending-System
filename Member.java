import java.util.ArrayList;

public class Member {
    private String memberId;
    private String name;
    private ArrayList<String> borrowedBookIds = new ArrayList<>();

    public Member(String memberId, String name) {
        this.memberId = memberId;
        this.name = name;
    }

    public String getMemberId() { return memberId; }
    public String getName() { return name; }
    public ArrayList<String> getBorrowedBookIds() { return borrowedBookIds; }

    public void borrowBook(Book book) throws Exception {
        if (borrowedBookIds.size() >= 3) throw new MaxLimitReachedException("Max book limit reached.");
        if (!book.isAvailable()) throw new BookNotAvailableException("Book not available.");
        borrowedBookIds.add(book.getBookId());
        book.setAvailable(false);
    }

    public void returnBook(Book book) throws Exception {
        if (!borrowedBookIds.contains(book.getBookId())) throw new Exception("Book not borrowed by this member.");
        borrowedBookIds.remove(book.getBookId());
        book.setAvailable(true);
    }

    public String toFileString() {
        return memberId + "," + name + "," + String.join(";", borrowedBookIds);
    }

    public static Member fromFileString(String line) {
        if (line == null || line.trim().isEmpty()) return null;
        String[] parts = line.split(",");
        if (parts.length < 2) return null;  // Defensive check

        Member m = new Member(parts[0], parts[1]);
        if (parts.length > 2) {
            for (String bookId : parts[2].split(";")) {
                if (!bookId.isEmpty()) m.getBorrowedBookIds().add(bookId);
            }
        }
        return m;
    }

}
