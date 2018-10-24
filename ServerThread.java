import java.net.*;
import java.io.*;

public class ServerThread extends Thread {  
   private Socket socket = null;
   private Server server = null;
   private DataOutputStream streamOut = null;
   private DataInputStream streamIn =  null;
   private int clientId = -1;
   private String username = "Anonymous";

   public ServerThread(Server server, Socket socket) {  
      clientId = socket.getPort();
      this.server = server;  
      this.socket = socket;
   }

   public void run() {
      System.out.println("Server thread for the client port (" + clientId + ") is running.");
      while (true) {  
         try {  
            server.handle(clientId, streamIn.readUTF());
         } catch(Exception e) {
            e.printStackTrace();
            server.remove(clientId);
            stop();
         }
      }
   }

   public void send(String msg) {
      try {
         streamOut.writeUTF(msg);
         streamOut.flush();
      } catch(Exception e) {
         e.printStackTrace();
         server.remove(clientId);
         stop();
      }
   }

   public void open() throws Exception {
      BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
      streamIn = new DataInputStream(bis);

      BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
      streamOut = new DataOutputStream(bos);
   }

   public void close() throws Exception {
      if (streamOut != null) streamOut.close();
      if (streamIn != null)  streamIn.close();
      if (socket != null)    socket.close();
   }

   // Setters / Getters
   public int getID() { return clientId; }
   public String getUsername() { return username; }

   public void setUsername(String username) {
      this.username = username;
   }
}

