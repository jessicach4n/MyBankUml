package bank.utils;

import java.util.ArrayList;
import java.util.List;

public class InternalLogger {

    private final List<String> history;

    public InternalLogger() {
        this.history = new ArrayList<>();
    }

    public void clear() {
        history.clear();
    }

    public void info(String message) {
        String text = "[INFO] " + message;
        history.add(text);
        System.out.println(text);
    }

    public void warn(String message) {
        String text = "[WARNING] " + message;
        history.add(text);
        System.out.println(text);
    }

    public void error(String message) {
        String text = "[ERROR] " + message;
        history.add(text);
        System.out.println(text);
    }

    public List<String> getHistory() {
        return history;
    }
}
