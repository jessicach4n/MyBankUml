package bank;

import java.util.ArrayList;
import java.util.List;

public class Search {

    public static List<Object> all(String things, String field, String condition, Object value) {
        List<Object> out = new ArrayList<>();

        if (things.equals("users")) {
            for (Users.User u : Users.get()) {
                if (match(get_field(u, field), condition, value)) out.add(u);
            }
            return out;
        }

        if (things.equals("accounts")) {
            for (Users.User u : Users.get()) {
                for (Users.Account a : u.accounts()) {
                    if (match(get_field(a, field), condition, value)) out.add(a);
                }
            }
            return out;
        }

        if (things.equals("transactions")) {
            for (Users.User u : Users.get()) {
                for (Users.Account a : u.accounts()) {
                    for (Users.Transaction t : a.transactions()) {
                        if (match(get_field(t, field), condition, value)) out.add(t);
                    }
                }
            }
            return out;
        }

        return out;
    }

    private static Object get_field(Object o, String f) {
        if (o instanceof Users.User u) {
            if (f.equals("id")) return u.id();
            if (f.equals("name")) return u.name();
            if (f.equals("role")) return u.role();
            if (f.equals("password")) return u.password();
        }

        if (o instanceof Users.Account a) {
            if (f.equals("type")) return a.type();
            if (f.equals("number")) return a.number();
            if (f.equals("balance")) return a.balance();
        }

        if (o instanceof Users.Transaction t) {
            if (f.equals("date")) return t.date();
            if (f.equals("amount")) return t.amount();
            if (f.equals("details")) return t.details();
            if (f.equals("to_account")) return t.to_account();
            if (f.equals("from_account")) return t.from_account();
            if (f.equals("recipient_id")) return t.recipient_id();
            if (f.equals("recipient_name")) return t.recipient_name();
        }

        return null;
    }

    private static boolean match(Object a, String c, Object b) {
        if (a instanceof Number n1 && b instanceof Number n2) {
            long x = n1.longValue();
            long y = n2.longValue();
            if (c.equals("<")) return x < y;
            if (c.equals("=")) return x == y;
            if (c.equals(">")) return x > y;
            return false;
        }

        if (a instanceof String s1 && b instanceof String s2) {
            if (c.equals("=")) return s1.equals(s2);
            return false;
        }

        return false;
    }
}