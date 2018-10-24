import java.net.*;
import java.io.*;

public class Client implements Runnable {  
   private Socket socket              = null;
   private Thread thread              = null;
   private DataInputStream  console   = null;
   private DataOutputStream streamOut = null;
   private ClientThread client    = null;

   public static void main(String args[]) {  
      Client client = null;
      if (args.length != 2)
         System.out.println("Usage: java Client [host name] [port number]");
      else
         client = new Client(args[0], Integer.parseInt(args[1]));
   }

   public Client(String serverName, int serverPort) {  
      System.out.println("Trying to connect to server...\nplease wait..");
      try {  
         socket = new Socket(serverName, serverPort);
         System.out.println("Connected to the server:" + socket);
         start();
      } catch(Exception e) {
         e.printStackTrace(); 
      }
   }

   public void run() {  
      while (thread != null) {  
         try {  
            streamOut.writeUTF(console.readLine());
            streamOut.flush();
         } catch(Exception e) { 
            e.printStackTrace();
         }
      }
   }

   public void handle(String msg) {  
      if (msg.equals("!exit")) {  
         System.out.println("Good bye!");
         stop();
      } else {
         System.out.println(msg);
      }
   }

   public void start() throws Exception {  
      console = new DataInputStream(System.in);
      streamOut = new DataOutputStream(socket.getOutputStream());
      if (thread == null) {  
         client = new ClientThread(this, socket);
         thread = new Thread(this);                   
         thread.start();
      }
   }

   public void stop() {  
      if (thread != null) {  
         thread.stop();  
         thread = null;
      }

      try {  
         if (console != null)
            console.close();
         if (streamOut != null) 
            streamOut.close();
         if (socket != null) 
            socket.close();
      } catch(Exception e) {
         e.printStackTrace();
      }

      client.close();  
      client.stop();
   }
}