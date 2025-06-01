import java.io.*;
import java.util.*;

public class LibrarySystem {
    private ArrayList<Book> books = new ArrayList<>();
    private ArrayList<Member> members = new ArrayList<>();

    public LibrarySystem() { loadData(); }

    public void addBook(Book book) { books.add(book); }
    public void addMember(Member member) { members.add(member); }

    public ArrayList<Book> getBooks() { return books; }
    public ArrayList<Member> getMembers() { return members; }

    public Book findBookById(String id) {
        return books.stream().filter(b -> b.getBookId().equals(id)).findFirst().orElse(null);
    }

    public Member findMemberById(String id) {
        return members.stream().filter(m -> m.getMemberId().equals(id)).findFirst().orElse(null);
    }

    public ArrayList<Book> getAvailableBooks() {
        ArrayList<Book> available = new ArrayList<>();
        for (Book b : books) if (b.isAvailable()) available.add(b);
        return available;
    }

    public void saveData() {
        try (PrintWriter bw = new PrintWriter("books.txt");
             PrintWriter mw = new PrintWriter("members.txt")) {
            for (Book b : books) bw.println(b.toFileString());
            for (Member m : members) mw.println(m.toFileString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadData() {
        try (BufferedReader br = new BufferedReader(new FileReader("books.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                Book b = Book.fromFileString(line);
                if (b != null) books.add(b);
            }
        } catch (IOException ignored) {}

        try (BufferedReader br = new BufferedReader(new FileReader("members.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                Member m = Member.fromFileString(line);
                if (m != null) members.add(m);
            }
        } catch (IOException ignored) {}
    }

}
