import java.io.*;
import java.net.Socket;

public class TicTacToeClient {
    public static void main(String[] args) throws Exception {
        try (Socket socket = new Socket("18.221.102.182",38006)) {
            int[][] board = new int[3][3];
            System.out.println("Connected to server.");
            InputStream is = socket.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            BufferedReader brIS = new BufferedReader(new InputStreamReader(System.in));
            PrintStream out = new PrintStream((socket.getOutputStream()),true,"UTF-8");

            ByteArrayOutputStream bO = new ByteArrayOutputStream();
            ObjectOutputStream oO = new ObjectOutputStream(bO);

            //BoardMessage Size is 32x based on status
            //ERROR MESSAGE SIZE is ~175 + String size of error

            oO.writeObject(new ConnectMessage("Player"));
            out.write(bO.toByteArray());

            byte[] temp = new byte[340];
            is.read(temp);

            ByteArrayInputStream bI = new ByteArrayInputStream(temp);
            ObjectInputStream oI = new ObjectInputStream(bI);
            BoardMessage msg = (BoardMessage) oI.readObject();
        }
    }
}
