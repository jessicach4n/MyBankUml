package bank;

public class Convert {

    public static long date(String date) 
    {
        String[] num = date.split("-");
        long y = Long.parseLong(num[0]);
        long m = Long.parseLong(num[1]);
        long d = Long.parseLong(num[2]);
        return y * 10000 + m * 100 + d;
    }

    public static String date(long date) 
    {
        long y = date / 10000;
        long m = (date / 100) % 100;
        long d = date % 100;
        return String.format("%04d-%02d-%02d", y, m, d);
    }

    public static long balance(String balance) 
    {
        String[] num = balance.split("\\.");
        long d = Long.parseLong(num[0]);
        long c = Long.parseLong(num[1]);
        return 100 * d + c;
    }

    public static String balance(long balance) 
    {
        long d = balance / 100;
        long c = balance % 100;
        return String.format("%d.%02d", d, c);
    }

    public static String initials(String name) 
    {
        return "" + name.charAt(0) + name.charAt(name.lastIndexOf(' ') + 1);
    }
}