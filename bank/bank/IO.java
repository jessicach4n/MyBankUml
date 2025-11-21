package org.example;
import java.util.List;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Main {

    private static final Gson GSON = new GsonBuilder()
            .serializeNulls().setPrettyPrinting().create();

    public record Users(
            List<User> users
    ) {
        public record User(
                long id,
                String type,
                String first,
                String last,
                List<Account> accounts
        ) {}
        public record Account(
                String type,
                double balance,
                String number,
                List<Transaction> history
        ) {}
        public record Transaction(
                double amount,
                String date,
                String state,
                String merchant,
                long merchant_id,
                String description
        ) {}
    }
    public static Users read_users()
    {
        Path path = Path.of("users.json");
        try (Reader r = Files.newBufferedReader(path))
        {
            Users doc = GSON.fromJson(r, Users.class);
            if (doc == null || doc.users() == null) return new Users(List.of());
            return doc;
        } catch (Exception e) {
            return new Users(List.of());
        }
    }
    public static void save_users(Users users)
    {
        Path path = Path.of("users.json");
        try (Writer w = Files.newBufferedWriter(path)) {
            GSON.toJson(users, w);
        } catch (Exception ignored) {}
    }
    public static void main(String[] args)
    {
        Users users = read_users();
        System.out.println(GSON.toJson(users));
        save_users(users);
    }
}