package ph.txtdis.printer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import javafx.scene.image.Image;

import org.apache.commons.lang3.StringUtils;

public class NetPrinter {
    private int yLoop, width;
    private int[][] value;
    protected boolean printed;
    protected OutputStream os;
    protected PrintStream ps;

    // Decimal ASCII values for ESC/P commands
    protected static final char ESC = 27; // escape
    protected static final char AT = 64; // @
    protected static final char EXCLAMATION = 33; // !
    protected static final char WIDE = 0b0000_0000; // 5x7
    protected static final char HUGE = 0b0011_0001; // double width & height
    protected static final char N = 78; // N
    protected static final char CHAR_PER_LINE = 4; // character per line
    protected static final char NARROW = 1; // 42 cpl
    protected static final char DLE = 16; // DLE
    protected static final char EOT = 4; // EOT
    protected static final char STATUS = 1; // printer status
    private static final char ASTERISK = 42; // *
    private static final char J = 74; // J

    protected static final char COLUMN_WIDTH = 42;

    public NetPrinter() {
    }

    protected void setPrinter() {
        try {
            os = new FileOutputStream("\\\\mgdc_dm\\LX-300");
            ps = new PrintStream(os, true);
            setLogo();
            printed = print();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // close port
            try {
                if (os != null)
                    os.close();
                if (ps != null)
                    ps.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setLogo() {
        String string;
        Image image = new Image(this.getClass().getResourceAsStream("/image/magnum.bmp"));
        int height = (int) image.getHeight();
        int yOffset = height % 8 / 2;
        int pixel;
        yLoop = height / 8;
        width = (int) image.getWidth();
        value = new int[yLoop][width];
        for (int i = 0; i < yLoop; i++) {
            for (int x = 0; x < width; x++) {
                string = "";
                for (int y = 0; y < 8; y++) {
                    pixel = image.getPixelReader().getArgb(x, yOffset + y + i * 8);
                    string += pixel < Integer.MAX_VALUE / 2 ? "0" : "1";
                }
                value[i][x] = Integer.parseInt(string, 2);
            }
        }
    }

    protected void printLogo() {
        try {
            for (int i = 0; i < yLoop; i++) {
                os.write(ESC);
                os.write(ASTERISK);
                os.write((byte) 0); // m
                os.write((byte) width); // n1
                os.write((byte) 0); // n2
                for (int j = 0; j < width; j++)
                    os.write((byte) value[i][j]);
                os.write(ESC);
                os.write(J);
                os.write((byte) 14); // n/144" feed
                os.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void printNormal() throws IOException {
        os.write(ESC);
        os.write(N);
        os.write(CHAR_PER_LINE);
        os.write(NARROW);
        os.write(ESC);
        os.write(AT);
        ps.println();
    }

    protected void printHuge() throws IOException {
        os.write(ESC);
        os.write(EXCLAMATION);
        os.write(HUGE);
    }

    protected void printDash() {
        ps.println(StringUtils.leftPad("", COLUMN_WIDTH, "-"));
    }

    protected void printPageEnd() {
        ps.println("________________________________________");
        ps.println();
        ps.println();
        ps.println();
        ps.println();
    }

    protected boolean print() throws IOException {
        return false;
    }

    public boolean isPrinted() {
        return printed;
    }

    public static void main(String[] args) {
        new NetPrinter().setPrinter();
    }
}
