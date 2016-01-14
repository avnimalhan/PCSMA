import java.net.*;
import java.io.*;

public class Server extends Thread
{
   private ServerSocket serverSocket;
   
   public Server(int port) throws IOException
   {
      serverSocket = new ServerSocket(port);
      serverSocket.setSoTimeout(1000000);
   }

   public void run()
   {
      while(true)
      {
         try
         {
            System.out.println("Waiting for client on port no." +
            serverSocket.getLocalPort() + "...");
            Socket server = serverSocket.accept();
            System.out.println("Connected");
            
            String value = "";
	    //System.out.println("Client says--");
    	  

	DataInputStream dis = new DataInputStream(server.getInputStream());
		FileOutputStream fos = new FileOutputStream("D://PCSMA_A1.csv");
		byte[] buffer = new byte[4096];
		
		int filesize = 15123; 
		int read = 0;
		int totalRead = 0;
		int remaining = filesize;
		while((read = dis.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
			totalRead += read;
			remaining -= read;
			System.out.println("read " + totalRead + " bytes.");
			fos.write(buffer, 0, read);
		}
		
		fos.close();
		dis.close();
            System.out.println("File successfully transferred.");
            
            server.close();
         }catch(SocketTimeoutException s)
         {
            System.out.println("Socket timed out!");
          
         }
	catch(EOFException e)
         {
           
         }
	catch(IOException e)
         {
            e.printStackTrace();
            
         }
	 
      }
   }
   public static void main(String [] args)
   {
      int port = Integer.parseInt(args[0]);
      try
      {
         Thread t = new Server(port);
         t.start();
      }catch(IOException e)
      {
         e.printStackTrace();
      }
   }
}