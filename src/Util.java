import java.util.Random;
import java.lang.StringBuilder;
import java.io.*;

public class Util {

    private final static Random rand = new Random();
    private static int idLength = 8;

    public static String MARKET_ID;
    static {
        StringBuilder tmp = new StringBuilder();
        if(idLength % 4 != 0) {
            idLength += (4 - idLength % 4);
        }
        for(int i = 0; i < idLength; i++) {
            tmp.append('0');
            if ((i + 1) % 4 == 0  && i != idLength - 1) {
                tmp.append('-');
            }
        }
        MARKET_ID = tmp.toString();
    }

    private static char[] idChars;
    static {
        StringBuilder tmp = new StringBuilder();
        for(char ch = '0'; ch <= '9'; ch++) {
            tmp.append(ch);
        }
        for(char ch = 'A'; ch <= 'Z'; ch++) {
            tmp.append(ch);
        }
        idChars = tmp.toString().toCharArray();
    }

    public static String genRandomId() {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < idLength; i++) {
            builder.append(idChars[rand.nextInt(idChars.length)]);
            if((i + 1) % 4 == 0  && i != idLength - 1) {
                builder.append('-');
            }
        }

        return builder.toString();
    }

    public static void print(String tag, String str)  {
        System.out.println("[" + tag + "] " + str);
    }

    public static void addArrayAsColumn(double[] array, double[][] matrix, int col) {
        for(int i = 0; i < array.length; i++) {
            matrix[i][col] = array[i];
        }
    }

    public static void writeMatrixToFile(double[][] matrix, String file) {
        try {
            FileWriter fstream = new FileWriter(file + ".csv");
            BufferedWriter out = new BufferedWriter(fstream);

            for(int row = 0; row < matrix.length; row++) {
                for(int col = 0; col < matrix[row].length; col++) {
                    out.write(Double.toString(matrix[row][col]));
                    if(col != matrix[row].length -1) {
                        out.write(",");
                    }
                }
                out.write("\n");
            }

            out.close();
        }
        catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public static double randomDouble(double min, double max) {
        return min + (max - min) * rand.nextDouble();
    }
}
