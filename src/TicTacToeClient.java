
import java.io.*;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.Scanner;

public class TicTacToeClient {
    public static void main(String[] args) throws Exception {
        try (Socket socket = new Socket("18.221.102.182",38006)) {
            System.out.println("Connected to server.");
            ObjectOutputStream sendMessage = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream receiveMessage = new ObjectInputStream(socket.getInputStream());
            sendMessage.writeObject(new ConnectMessage("Player"));
            sendMessage.writeObject(new CommandMessage(CommandMessage.Command.NEW_GAME));
            Message msg =(Message) receiveMessage.readObject();
            if(msg.getType().equals(MessageType.ERROR)) {
                System.out.println("Error Detected!");
                sendMessage.writeObject(new CommandMessage(CommandMessage.Command.EXIT));
                socket.close();
                System.out.println("Disconnected from server.");
                return;
            }
            Scanner scanner = new Scanner(System.in);

            while(((BoardMessage) msg).getStatus().equals(BoardMessage.Status.IN_PROGRESS)){
                printBoard((BoardMessage) msg);
                System.out.println("Please choose the number corresponding to the action you would like to take:");
                System.out.println("1. Continue the game, 2. Surrender, or 3. Exit.");
                System.out.println("Entering a number that isn't one of the above choices will default to continuing the game.");
                byte choice;
                while(true) {
                    try {
                        choice = scanner.nextByte();
                        break;
                    } catch (InputMismatchException e) {
                        System.out.println("Please enter one of the numbers 1,2,3 corresponding to your choice.");
                    }
                }
                if(choice==2){
                    sendMessage.writeObject(new CommandMessage(CommandMessage.Command.SURRENDER));
                }
                else if(choice==3){
                    sendMessage.writeObject(new CommandMessage(CommandMessage.Command.EXIT));
                    socket.close();
                    System.out.println("Disconnected from server.");
                    return;
                }
                else {
                    byte r;
                    byte c;
                    while (true) {
                        System.out.println("Please enter your move based on a 0 index, e.g 0-2, in regards to the row.");
                        r = getRow(scanner);
                        while (r < 0 || r > 2) {
                            System.out.println("Nonexistent row, please enter another row.");
                            r = getRow(scanner);
                        }
                        System.out.println("Please enter your move based on a 0 index, e.g 0-2, in regards to the col.");
                        c = getCol(scanner);
                        while (c < 0 || c > 2) {
                            System.out.println("Nonexistent col, please enter another col.");
                            c = getCol(scanner);
                        }
                        if (((BoardMessage) msg).getBoard()[r][c] != 0) {
                            System.out.println("Square designated by row and column is already filled, please try again.");
                        } else {
                            break;
                        }
                    }
                    sendMessage.writeObject(new MoveMessage(r, c));
                }
                msg = (Message) receiveMessage.readObject();
                if(msg.getType().equals(MessageType.ERROR)) {
                    System.out.println("Error Detected!");
                    sendMessage.writeObject(new CommandMessage(CommandMessage.Command.EXIT));
                    socket.close();
                    System.out.println("Disconnected from server.");
                    return;
                }
            }
            System.out.println(((BoardMessage) msg).getStatus());
            printBoard(((BoardMessage) msg));
            socket.close();
            System.out.println("The game is over.");
            System.out.println(" Disconnected from server.");
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

    public static byte getRow(Scanner scanner){
        byte r;
        while(true) {
            try {
                r = scanner.nextByte();
                return r;
            } catch (InputMismatchException e) {
                System.out.println("Please enter one of the one of the bytes 0,1,2 corresponding to your choice of row.");
            }
        }
    }

    public static byte getCol(Scanner scanner){
        byte c;
        while(true) {
            try {
                c = scanner.nextByte();
                return c;
            } catch (InputMismatchException e) {
                System.out.println("Please enter one of the one of the bytes 0,1,2 corresponding to your choice of col.");
            }
        }
    }
}
