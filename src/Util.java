import java.util.Random;
import java.lang.StringBuilder;

public class Util {

    private final static Random rand = new Random();
    private static int idLength = 8;    
    private static char[] idChars;

    static {
        if(idLength % 4 != 0) {
            idLength += (4 - idLength % 4);
        }

        StringBuilder tmp = new StringBuilder();
        for (char ch = '0'; ch <= '9'; ch++) {
            tmp.append(ch);
        }
        for (char ch = 'A'; ch <= 'Z'; ch++) {
            tmp.append(ch);
        }
        idChars = tmp.toString().toCharArray();
    }

    public static String genRandomId() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < idLength; i++) {
            builder.append(idChars[rand.nextInt(idChars.length)]);
            if ((i + 1) % 4 == 0  && i != idLength - 1) {
                builder.append('-');
            }
        }

        return builder.toString();
    }
}