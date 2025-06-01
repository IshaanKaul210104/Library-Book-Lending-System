import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class LibraryGUI extends JFrame {
    private LibrarySystem system;

    public LibraryGUI(LibrarySystem system) {
        this.system = system;
        setTitle("Library Book Lending System");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(6, 1, 10, 10));

        JButton viewBooksBtn = new JButton("View Available Books");
        JButton addBookBtn = new JButton("Add New Book");
        JButton registerMemberBtn = new JButton("Register New Member");
        JButton issueBookBtn = new JButton("Issue Book");
        JButton returnBookBtn = new JButton("Return Book");
        JButton searchBtn = new JButton("Search Books/Members");
        JButton viewHistoryBtn = new JButton("View Lending History");
        JButton resetDbBtn = new JButton("Reset Database");
        JButton exitBtn = new JButton("Exit");

        add(viewBooksBtn);
        add(addBookBtn);
        add(registerMemberBtn);
        add(issueBookBtn);
        add(returnBookBtn);
        add(searchBtn);
        add(viewHistoryBtn);
        add(resetDbBtn);
        add(exitBtn);

        viewBooksBtn.addActionListener(e -> showAvailableBooksTable());
        addBookBtn.addActionListener(e -> showAddBookDialog());
        registerMemberBtn.addActionListener(e -> showRegisterMemberDialog());
        issueBookBtn.addActionListener(e -> showIssueBookDialog());
        returnBookBtn.addActionListener(e -> showReturnBookDialog());
        searchBtn.addActionListener(e -> showSearchDialog());
        viewHistoryBtn.addActionListener(e -> showLendingHistory());
        resetDbBtn.addActionListener(e -> resetDatabase());
        exitBtn.addActionListener(e -> {
            system.saveData();
            System.exit(0);
        });

        setVisible(true);
    }

    private void showAvailableBooksTable() {
        ArrayList<Book> books = system.getAvailableBooks();
        String[] columns = {"Book ID", "Title", "Author", "Available"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);

        for (Book b : books) {
            tableModel.addRow(new Object[]{b.getBookId(), b.getTitle(), b.getAuthor(), b.isAvailable()});
        }

        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        JOptionPane.showMessageDialog(this, scrollPane, "Available Books", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showAddBookDialog() {
        JTextField idField = new JTextField();
        JTextField titleField = new JTextField();
        JTextField authorField = new JTextField();
        Object[] fields = {
                "Book ID:", idField,
                "Title:", titleField,
                "Author:", authorField
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Add Book", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String id = idField.getText().trim();
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();

            if (id.isEmpty() || title.isEmpty() || author.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields must be filled out.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 🔍 Check for duplicate ID
            for (Book existing : system.getBooks()) {
                if (existing.getBookId().equalsIgnoreCase(id)) {
                    JOptionPane.showMessageDialog(this, "A book with this ID already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            Book b = new Book(id, title, author, true);
            system.addBook(b);
            JOptionPane.showMessageDialog(this, "Book added successfully!");
        }
    }

    private void showRegisterMemberDialog() {
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        Object[] fields = {
                "Member ID:", idField,
                "Name:", nameField
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Register Member", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String id = idField.getText().trim();
            String name = nameField.getText().trim();

            if(id.isEmpty() || name.isEmpty()){
                JOptionPane.showMessageDialog(this, "All fields must be filled out.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            for(Member existing : system.getMembers()){
                if(existing.getMemberId().equalsIgnoreCase(id)){
                    JOptionPane.showMessageDialog(this, "A member with this ID already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            Member m = new Member(idField.getText(), nameField.getText());
            system.addMember(m);
            JOptionPane.showMessageDialog(this, "Member registered successfully!");
        }
    }

    private void showIssueBookDialog() {
        ArrayList<Book> availableBooks = system.getAvailableBooks();
        if (availableBooks.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No books are available for issuing at this time.");
            return;
        }

        JComboBox<String> memberBox = new JComboBox<>();
        for (Member m : system.getMembers())
            memberBox.addItem(m.getMemberId() + " - " + m.getName());

        if (memberBox.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "No members are registered to issue books.");
            return;
        }

        JComboBox<String> bookBox = new JComboBox<>();
        for (Book b : availableBooks)
            bookBox.addItem(b.getBookId() + " - " + b.getTitle());

        Object[] fields = {
                "Select Member:", memberBox,
                "Select Book:", bookBox
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Issue Book", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String memberId = memberBox.getSelectedItem().toString().split(" - ")[0];
            String bookId = bookBox.getSelectedItem().toString().split(" - ")[0];

            Member m = system.findMemberById(memberId);
            Book b = system.findBookById(bookId);

            try {
                m.borrowBook(b);
                JOptionPane.showMessageDialog(this, "Book issued successfully!");
                LendingHistoryLogger.log("Issued", m, b);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private void showReturnBookDialog() {
        JComboBox<String> memberBox = new JComboBox<>();

        for (Member m : system.getMembers())
            memberBox.addItem(m.getMemberId() + " - " + m.getName());

        Object[] fields1 = {
                "Select Member:", memberBox
        };

        int result1 = JOptionPane.showConfirmDialog(this, fields1, "Select Member", JOptionPane.OK_CANCEL_OPTION);
        if (result1 != JOptionPane.OK_OPTION) return;

        String memberId = memberBox.getSelectedItem().toString().split(" - ")[0];
        Member m = system.findMemberById(memberId);

        JComboBox<String> bookBox = new JComboBox<>();
        for (String bookId : m.getBorrowedBookIds()) {
            Book b = system.findBookById(bookId);
            if (b != null) bookBox.addItem(b.getBookId() + " - " + b.getTitle());
        }

        if (bookBox.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "This member has not borrowed any books.");
            return;
        }

        Object[] fields2 = {
                "Select Book to Return:", bookBox
        };

        int result2 = JOptionPane.showConfirmDialog(this, fields2, "Return Book", JOptionPane.OK_CANCEL_OPTION);
        if (result2 == JOptionPane.OK_OPTION) {
            String bookId = bookBox.getSelectedItem().toString().split(" - ")[0];
            Book b = system.findBookById(bookId);
            try {
                m.returnBook(b);
                JOptionPane.showMessageDialog(this, "Book returned successfully!");
                LendingHistoryLogger.log("Returned", m, b);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showSearchDialog() {
        JTextField searchField = new JTextField();
        Object[] fields = {
                "Enter keyword to search:", searchField
        };

        int result = JOptionPane.showConfirmDialog(this, fields, "Search", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String keyword = searchField.getText().toLowerCase();
            StringBuilder sb = new StringBuilder("Search Results:\n\n");

            boolean found = false;

            for (Book b : system.getBooks()) {
                if (b.getBookId().toLowerCase().contains(keyword) ||
                        b.getTitle().toLowerCase().contains(keyword) ||
                        b.getAuthor().toLowerCase().contains(keyword)) {
                    sb.append("Book: ").append(b.getBookId()).append(" - ")
                            .append(b.getTitle()).append(" by ").append(b.getAuthor()).append("\n");
                    found = true;
                }
            }

            for (Member m : system.getMembers()) {
                if (m.getMemberId().toLowerCase().contains(keyword) ||
                        m.getName().toLowerCase().contains(keyword)) {
                    sb.append("Member: ").append(m.getMemberId()).append(" - ")
                            .append(m.getName()).append("\n");
                    found = true;
                }
            }

            if (!found) sb.append("No matches found.");

            JOptionPane.showMessageDialog(this, sb.toString());
        }
    }

    private void showLendingHistory() {
        try {
            JTextArea area = new JTextArea(20, 50);
            area.read(new java.io.FileReader("history.txt"), null);
            area.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(area);
            JOptionPane.showMessageDialog(this, scrollPane, "Lending History", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "No history found.");
        }
    }

    private void resetDatabase(){
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to reset the ALL DATA? This cannot be undone.",
                "Confirm Reset", JOptionPane.YES_NO_OPTION);

        if(confirm == JOptionPane.YES_OPTION){
            try{
                new PrintWriter("books.txt").close();
                new PrintWriter("members.txt").close();
                new PrintWriter("history.txt").close();

                system.getBooks().clear();
                system.getMembers().clear();

                JOptionPane.showMessageDialog(this, "Database reset successfully.");
            }
            catch(Exception e){
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error resetting database.");
            }
        }
    }
}
