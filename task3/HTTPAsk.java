import java.net.*;
import tcpclient.TCPClient;

import java.io.*;

public class HTTPAsk {
    public static void main( String[] args) throws IOException {
        
        //Initializing all the variables
        String hostname = "";
        String portNumber = "";  //Has to be a string because of the later methods
        String stringToSend = "";
        boolean shutdown = false;
        Integer timeout = null;
        Integer limit = null;
        boolean online = true;
        int port = Integer.parseInt(args[0]);
        ServerSocket serverSocket = new ServerSocket(port);

        StringBuilder urlbuilder = new StringBuilder();
        int input;

        while(online){
            
            //System.out.println("Waiting for socket"); //Initialize streams
            Socket client = serverSocket.accept();
            //System.out.println("Socket accpeted");
            InputStream inputStream = client.getInputStream();
            OutputStream outputStream = client.getOutputStream();
            String HTTPHeader = "HTTP/1.1 200 OK\r\n";
            String BadRequest = "HTTP/1.1 400 Bad Request\r\n\r\n";
            String NotFound =   "HTTP/1.1 404 Not Found\r\n\r\n";
            try {
                client.setSoTimeout(10000);    //Prevent loop if stuck
                while(((input = inputStream.read()) != '\n')){  //Read toserver data
                    urlbuilder.append((char)input);
                }
                String url = urlbuilder.toString();
                String[] requestLines = url.split("\\r?\\n");
                String[] requestLine = requestLines[0].split(" ");
                String requestMethod = requestLine[0];
                String requestURI = requestLine[1];
                System.out.println(requestURI);


                if (!requestURI.startsWith("/ask?")) {
                    outputStream.write(NotFound.getBytes());
                    continue;
                }
                
                if (!requestMethod.equals("GET")) {
                    outputStream.write(BadRequest.getBytes());
                    continue;
                }

                if (requestMethod.equals("GET") && requestURI.startsWith("/ask?")) {
                    // Extract parameters from request URI
                    String[] urlparam = requestURI.split("\\?")[1].split("&");
                

                for (String kv : urlparam) {             //Each part of the paramter is a key value pair
                    String[] pair = kv.split("=");       //Split at equals, ex limit = 500

                    if (pair[0].equals("hostname")) {
                        hostname = pair[1];
                    } else if (pair[0].equals("port")) {
                        portNumber = pair[1];
                    } else if (pair[0].equals("string")) {
                        stringToSend = pair[1];
                    } else if (pair[0].equals("shutdown")) {
                        shutdown = true;
                    } else if (pair[0].equals("timeout")) {
                        timeout = Integer.parseInt(pair[1]);
                    } else if (pair[0].equals("limit")) {
                        limit = Integer.parseInt(pair[1]);
                    }
                }
                // Init TCPclient and responsearray
                TCPClient tcpClient = new TCPClient(shutdown, timeout, limit);
                byte[] response = tcpClient.askServer(hostname, Integer.parseInt(portNumber), stringToSend.getBytes());
                outputStream.write(HTTPHeader.getBytes());   // Send HTTP response to client
                outputStream.write(response);
            }
            } catch (SocketException e){
                System.err.println(e.getMessage());
            } finally {
                client.close();
            }
        }
        serverSocket.close();
    }   
}

