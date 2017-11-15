
import java.io.*;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.Scanner;

public class TicTacToeClient {
    public static void main(String[] args) throws Exception {
        try (Socket socket = new Socket("18.221.102.182",38006)) {
            System.out.println("Connected to server.");
            InputStream is = socket.getInputStream();
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
            Scanner scanner = new Scanner(System.in);

            //ERROR MESSAGE DETECTION
            Runnable listen = () ->{
                while(true){
                    try{
                        byte[] tempE= new byte[200];
                        is.read(tempE);
                        ByteArrayInputStream bIE = new ByteArrayInputStream(tempE);
                        ObjectInputStream oIE = new ObjectInputStream(bIE);
                        ErrorMessage error = (ErrorMessage) oIE.readObject();
                        System.out.println(error.getError());
                    }
                    catch (Exception e){

                    }
                }
            };

            Thread listeningThread = new Thread(listen);
            listeningThread.start();

            while(msg.getStatus().equals(BoardMessage.Status.IN_PROGRESS)){
                printBoard(msg);
                System.out.println("Please choose the number corresponding to the action you would like to take:");
                System.out.println("1. Continue the game, 2. Surrender, 3. Exit, or 4. Start a New Game");
                System.out.println("Entering a number that isn't one of the above choices will default to continuing the game.");
                int choice=0;
                while(true) {
                    try {
                        choice = scanner.nextInt();
                    } catch (InputMismatchException e) {
                        System.out.println("Please enter one of the integers 1,2,3,4 corresponding to your choice.");
                    }
                    break;
                }
                if(choice==2){
                    ByteArrayOutputStream bOCM = new ByteArrayOutputStream();
                    ObjectOutputStream oOCM = new ObjectOutputStream(bOCM);
                    oOCM.writeObject(new CommandMessage(CommandMessage.Command.SURRENDER));
                    out.write(bOCM.toByteArray());
                }
                else if(choice==3){
                    ByteArrayOutputStream bOCM = new ByteArrayOutputStream();
                    ObjectOutputStream oOCM = new ObjectOutputStream(bOCM);
                    oOCM.writeObject(new CommandMessage(CommandMessage.Command.EXIT));
                    out.write(bOCM.toByteArray());
                }
                else if(choice==4){
                    ByteArrayOutputStream bOCM = new ByteArrayOutputStream();
                    ObjectOutputStream oOCM = new ObjectOutputStream(bOCM);
                    oOCM.writeObject(new CommandMessage(CommandMessage.Command.NEW_GAME));
                    out.write(bOCM.toByteArray());
                }
                byte r=0;
                byte c =0;
                while(true) {
                    System.out.println("Please enter your move based on a 0 index, e.g 0-2, in regards to the row.");


                    while(true) {
                        try {
                            r = scanner.nextByte();
                        } catch (InputMismatchException e) {
                            System.out.println("Please enter one of the one of the bytes 0,1,2 corresponding to your choice of row.");
                        }
                        break;
                    }


                    while (r < 0 || r > 2) {
                        System.out.println("Nonexistent row, please enter another row.");


                        while(true) {
                            try {
                                r = scanner.nextByte();
                            } catch (InputMismatchException e) {
                                System.out.println("Please enter one of the one of the bytes 0,1,2 corresponding to your choice of row.");
                            }
                            break;
                        }


                    }
                    System.out.println("Please enter your move based on a 0 index, e.g 0-2, in regards to the col.");



                    while(true) {
                        try {
                            c = scanner.nextByte();
                        } catch (InputMismatchException e) {
                            System.out.println("Please enter one of the one of the bytes 0,1,2 corresponding to your choice of col.");
                        }
                        break;
                    }



                    while (c < 0 || c > 2) {
                        System.out.println("Nonexistent row, please enter another col.");



                        while(true) {
                            try {
                                c = scanner.nextByte();
                            } catch (InputMismatchException e) {
                                System.out.println("Please enter one of the one of the bytes 0,1,2 corresponding to your choice of col.");
                            }
                            break;
                        }
                    }



                    if(msg.getBoard()[r][c]!=0){
                        System.out.println("Square designated by row and column is already filled, please try again.");
                    }
                    else{
                        break;
                    }
                }
                ByteArrayOutputStream bOM = new ByteArrayOutputStream();
                ObjectOutputStream oOM = new ObjectOutputStream(bOM);
                oOM.writeObject(new MoveMessage(r,c));
                out.write(bOM.toByteArray());
            }
            System.out.println(msg.getStatus());
            printBoard(msg);
            is.close();
            socket.close();
            System.out.println("Disconnected from server.");
        }
    }

    public static void printBoard(BoardMessage msg){
        for(int row=0;row<3;row++){
            for(int col=0;col<3;col++){
                if(msg.getBoard()[row][col]==0){
                    System.out.print("[   ] ");
                }
                else if(msg.getBoard()[row][col]==0) {
                    System.out.print("[ X ] ");
                }
                else{
                    System.out.print("[ O ] ");
                }
            }
            System.out.println("");
        }
    }
}
