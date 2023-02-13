package tcpclient;
import java.net.*;
import java.io.*;

public class TCPClient {
    
    static int BUFFERSIZE = 1024;

    public TCPClient() {
        
    }

    public byte[] askServer(String hostname, int port, byte [] toServerBytes) throws IOException {
        
            Socket socket = new Socket(hostname, port);

            try {   

                OutputStream outputStream = socket.getOutputStream();                //Creates the outputstream used to write data to a socket    
                
                outputStream.write(toServerBytes);                                   //Output toserverbytes array to server socket bytearray     
                                                                                        
                InputStream inputStream = socket.getInputStream();                  //Initalize the inputstream used to read the response from the server
                
                ByteArrayOutputStream bytearrayout = new ByteArrayOutputStream();   //Initialize byteArr outputstream, BAOS is a dynamically allocated array
                
                byte[] fixedfromServerBuffer = new byte[BUFFERSIZE];                //Initial fixed in-memory buffer
    
                int length;                                                         //Changes each iteration of the while loop, represents the number of bytes read in the buffer
                                       
                while ((length = inputStream.read(fixedfromServerBuffer)) != -1) {   //Reads 1024 bytes per loop iteration into the buffer
                    bytearrayout.write(fixedfromServerBuffer, 0, length);            //Inputstream.read returns -1 when there is no more data to read
                }                                                                    //used to store the response of the server even if bigger than 1024 bytes
                return bytearrayout.toByteArray();                                   //Return all of the data from the server in a single array

            } catch(IOException error) {
                System.err.println("Catch error" + error.getMessage());  
                throw error;
              
            }finally {
                socket.close();  //Ensure socket is always closed
            }

        
    }
    public byte[] askServer(String hostname, int port) throws IOException {
        
        Socket socket = new Socket(hostname, port);

        try {                                                                                        
            InputStream inputStream = socket.getInputStream();                  //Initalize the inputstream used to read the response from the server
            
            ByteArrayOutputStream bytearrayout = new ByteArrayOutputStream();   //Initialize byteArr outputstream, BAOS is a dynamically allocated array
            
            byte[] fixedfromServerBuffer = new byte[BUFFERSIZE];                //Initial fixed in-memory buffer

            int length;                                                         //Changes each iteration of the while loop, represents the number of bytes read in the buffer
                                   
            while ((length = inputStream.read(fixedfromServerBuffer)) != -1) {   //Reads 1024 bytes per loop iteration into the buffer
                bytearrayout.write(fixedfromServerBuffer, 0, length);            //Inputstream.read returns -1 when there is no more data to read
            }                                                                    //used to store the response of the server even if bigger than 1024 bytes
            return bytearrayout.toByteArray();                                   //Return all of the data from the server in a single array

        } catch(IOException error) {
            System.err.println("Catch error" + error.getMessage());  
            throw error;
          
        }finally {
            socket.close();  //Ensure socket is always closed
        }

    
}
}
