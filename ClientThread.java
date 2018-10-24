import java.net.*;
import java.io.*;

public class ClientThread extends Thread {  
   private Socket socket = null;
   private Client client = null;
   private DataInputStream streamIn = null;

   public ClientThread(Client client, Socket socket) {  
      this.client   = client;
      this.socket   = socket;

      // Start listening to client
      open();
      start();
   }

   public void open() {  
      try {  
         streamIn  = new DataInputStream(socket.getInputStream());
      } catch(Exception e) {  
         e.printStackTrace();
         client.stop();
      }
   }

   public void close() {  
      try {  
         if (streamIn != null) streamIn.close();
      }
      catch(Exception e) {  
         e.printStackTrace();
      }
   }

   public void run() {  
      while (true) {  
         try {  
            client.handle(streamIn.readUTF());
         } catch(EOFException e) {
            System.out.println("Press ENTER to exit the program");
            client.stop();
         } catch(Exception e) {  
            e.printStackTrace();
            client.stop();
         }
      }
   }
}
