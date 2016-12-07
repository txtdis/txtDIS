package ph.txtdis.printer;

public class MatrixMain {
    public static void main(String[] args) {
        ESCPrinter escp = new ESCPrinter("\\\\mgdc_dm\\LX-300", false); // create
                                                                        // ESCPrinter
                                                                        // on
                                                                        // network
                                                                        // location
                                                                        // \\computer\sharename,
                                                                        // 9pin
                                                                        // printer
        if (escp.initialize()) {
            escp.setCharacterSet(ESCPrinter.USA);
            escp.select15CPI(); // 15 characters per inch printing
            escp.advanceVertical(5); // go down 5cm
            escp.setAbsoluteHorizontalPosition(5); // 5cm to the right
            escp.bold(true);
            escp.print("Let's print some matrix text ;)");
            escp.bold(false);
            escp.advanceVertical(1);
            escp.setAbsoluteHorizontalPosition(5); // back to 5cm on horizontal
                                                   // axis
            escp.print("Very simple and easy!");
            escp.formFeed(); // eject paper
            escp.close(); // close stream
        } else
            System.out.println("Couldn't open stream to printer");
    }
}
