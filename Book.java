public class Book {
    private String bookId;
    private String title;
    private String author;
    private boolean isAvailable;

    public Book(String bookId, String title, String author, boolean isAvailable) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.isAvailable = isAvailable;
    }

    public String getBookId() { return bookId; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public boolean isAvailable() { return isAvailable; }

    public void setAvailable(boolean available) { isAvailable = available; }

    public String toFileString() {
        return bookId + "," + title + "," + author + "," + isAvailable;
    }

    public void displayBookInfo() {
        System.out.println(bookId + " | " + title + " | " + author + " | " + (isAvailable ? "Available" : "Not Available"));
    }

    public static Book fromFileString(String line) {
        if (line == null || line.trim().isEmpty()) return null;
        String[] parts = line.split(",");
        if (parts.length < 4) return null;  // Defensive check
        return new Book(parts[0], parts[1], parts[2], Boolean.parseBoolean(parts[3]));
    }

}
