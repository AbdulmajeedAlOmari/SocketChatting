import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class Server implements Runnable {  
   private static final int MAX_NUMBER_OF_CLIENTS = 10;

   private ArrayList<ServerThread> clients = new ArrayList<>();
   private Thread thread = null;
   private ServerSocket server = null;

   public static void main(String args[]) {
      Server server = null;
      if (args.length != 1) {
         System.out.println("Usage: java Server [port]");
      } else {
         server = new Server(Integer.parseInt(args[0]));
      }
   }

   public Server(int portNumber) {
      try {
         System.out.println("Creating server..");
         server = new ServerSocket(portNumber);  
         System.out.println("Server was initialized!\nfull info:");
         System.out.println(server.toString());
         start();
      } catch(Exception e) {
         e.printStackTrace();
      }
   }

   public void run() {  
      while (thread != null) {  
         try {
            addThread(server.accept());
         } catch(IOException e) { 
            e.printStackTrace();
         }
      }
   }

   public void start() {
      if (thread == null) {
         thread = new Thread(this); 
         thread.start();
      }
   }

   public void stop() {
      if (thread != null) {
         thread.stop(); 
         thread = null;
      }
   }

   public synchronized void handle(int clientId, String input) {  
      // retrieve client from list
      ServerThread client = findClient(clientId); 

      if (input.equals("!exit")) {
         // End connection with client
         if(client != null) {
            client.send("Good bye, " + client.getID() + "!");
            remove(clientId);
         }
      } else {
         for (ServerThread element : clients) {
            element.send(client.getID() + ": " + input);
         }
      }
   }

   public synchronized void remove(int clientId)
   {  
      ServerThread client = findClient(clientId);
      if (client != null) {  
         System.out.println("Removing client thread (" + clientId + ")");
         this.clients.remove(client);
         try {
            // Stop reading/writing with the client (thread)
            client.close();
         } catch(Exception e) {
            e.printStackTrace();
         }

         client.stop(); 
      }
   }

   private void addThread(Socket socket) {  
      if (clients.size() == MAX_NUMBER_OF_CLIENTS) {
         System.out.println("Client was refueds\nThe server is already serving " 
            + MAX_NUMBER_OF_CLIENTS + " clients.");
         return;
      }

      System.out.println("Client was accepted");

      ServerThread client = new ServerThread(this, socket);
      try {
         client.open();
         client.start();
         clients.add(client);
      } catch(Exception e) {
         e.printStackTrace();
      }
   }

   // ==============
   // Helper methods
   // ==============

   /***
   * returns a client using his ID
   */
   private ServerThread findClient(int clientId) {
      for(ServerThread client : clients) {
         if(client.getID() == clientId)
            return client;
      }

      return null;
   }
}

