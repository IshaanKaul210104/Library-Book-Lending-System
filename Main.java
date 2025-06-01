public class Main {
    public static void main(String[] args) {
        LibrarySystem system = new LibrarySystem();
        new LibraryGUI(system); // GUI class to be implemented with Swing
        Runtime.getRuntime().addShutdownHook(new Thread(system::saveData));
    }
}