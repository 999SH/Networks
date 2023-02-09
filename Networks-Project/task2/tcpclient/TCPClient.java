package tcpclient;
import java.net.*;
import java.io.*;

public class TCPClient {
    static int BUFFERSIZE = 1024;
    
    boolean shutdown;  //Declare all variable as "global" so that they can be used within the scope of askserver despite getting their value in tcpclient
    Integer timeout;   //Wrapper class that can handle null
    Integer limit;

    
    public TCPClient(boolean shutdown, Integer timeout, Integer limit) {
        if (timeout != null){
            this.timeout = timeout; //incase of failed null recognition it is prefereable to have no timeout rather than the client breaking at socket.setSoTimeout
        } else {
            this.timeout = 0;       //0 means no timeout
        }

        if (limit != null){
            this.limit = limit;        
        } else {
            this.limit = Integer.MAX_VALUE;  //If null then there is no limit 
        }

        this.shutdown = shutdown;       //Default is false, should only matter if true. 

    }

    public byte[] askServer(String hostname, int port, byte [] toServerBytes) throws IOException {

            Socket socket = new Socket(hostname, port);                         //Create the socket
            OutputStream outputStream = socket.getOutputStream();               //Creates the outputstream used to write data to a socket
            outputStream.write(toServerBytes);                                  //Output toserverbytes array to server socket bytearray
            if (shutdown == true){
                socket.shutdownOutput();          //Close outputstream if shutdown true 
            }

            InputStream inputStream = socket.getInputStream();                  //Initalize the inputstream used to read the response from the server
            ByteArrayOutputStream bytearrayout = new ByteArrayOutputStream();   //Initialize byteArr outputstream, BAOS is a dynamically allocated array
            byte[] fixedfromServerBuffer = new byte[BUFFERSIZE];                //Initial fixed in-memory buffer
            int length;                                                         //Changes each iteration of the while loop, represents the number of bytes read in the buffer
            int totalLength = 0;
            int l = limit;   //Cast Integer to int
            
            socket.setSoTimeout(timeout);                                       //Set timeout and try to read if not null, if 0 is set it removes the timeout limit. 
            try {
            while (((length = inputStream.read(fixedfromServerBuffer)) != -1) && (totalLength <= l)){
                int i = 0;
                while (totalLength + i + 1 <= l && i + 1 <= length){
                    bytearrayout.write(fixedfromServerBuffer, i, 1);
                    i++;
                }      
                totalLength += length;                                                                   
            }
            return bytearrayout.toByteArray(); 
            } catch(SocketException error) {
                System.err.println("Socket timeout reached" +error.getMessage());
                throw error;

            } catch(IOException error) {
                System.err.println("IO error" + error.getMessage());
                throw error;
                
            }finally { 
                socket.close();  //Ensure socket is always closed
            }
        }
        public byte[] askServer(String hostname, int port) throws IOException { //incase no toserverbytes
            return askServer(hostname, port, new byte[0]);
    }
}
