import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import tcpclient.TCPClient;

import java.io.*;

public class ConcHTTPAsk {
    public static void main( String[] args) throws IOException {
        
        //Initializing all the variables

        boolean online = true;
        int port = Integer.parseInt(args[0]);
        ServerSocket serverSocket = new ServerSocket(port);
        
        try {
            while(online){
            Socket client = serverSocket.accept();
            Runnable clientThread = new MyRunnable(client);
            new Thread(clientThread).start();
            }
        } catch(Exception e){
            System.err.print(e.getMessage());
            serverSocket.close();  
        }     
    }   
}

