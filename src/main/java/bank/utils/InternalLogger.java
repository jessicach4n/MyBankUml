package bank.utils;

import java.util.ArrayList;
import java.util.List;

public class InternalLogger {

    private final List<String> history;

    // ANSI escape codes for colors
    private static final String RESET = "\u001B[0m";
    private static final String BLUE = "\u001B[34m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RED = "\u001B[31m";

    public InternalLogger() {
        this.history = new ArrayList<>();
    }

    public void clear() {
        history.clear();
    }

    public void info(String message) {
        String text = "[INFO] " + message;
        history.add(text);
        System.out.println(BLUE + text + RESET);
    }

    public void warn(String message) {
        String text = "[WARNING] " + message;
        history.add(text);
        System.out.println(YELLOW + text + RESET);
    }

    public void error(String message) {
        String text = "[ERROR] " + message;
        history.add(text);
        System.out.println(RED + text + RESET);
    }

    public List<String> getHistory() {
        return history;
    }

    public static void main(String[] args) {
        InternalLogger logger = new InternalLogger();

        logger.info("This is an info message (should be blue)");
        logger.warn("This is a warning message (should be yellow)");
        logger.error("This is an error message (should be red)");

        System.out.println("\nHistory of logs:");
        logger.getHistory().forEach(System.out::println);
    }
}
