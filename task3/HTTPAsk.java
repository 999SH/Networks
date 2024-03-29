import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

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
            DataOutputStream dataout = new DataOutputStream(outputStream);
            String HTTPHeader = "HTTP/1.1 200 OK\r\n\r\n";
            String BadRequest = "HTTP/1.1 400 Bad Request\r\n\r\n";
            String NotFound =   "HTTP/1.1 404 Not Found\r\n\r\n";
            byte[] stringToBytes = new byte[0];
            try {
                client.setSoTimeout(10000);    //Prevent loop if stuck
                while(((input = inputStream.read()) != '\n')){  //Read toserver data
                    urlbuilder.append((char)input);
                }
                String url = urlbuilder.toString();
                //String url = "GET /ask?hostname=java.lab.ssvl.kth.se&string=HelloServer&port=7 HTTP/1.1";
                //String url = "GET /ask?hostname=localhost&port=10002&string=The22 HTTP/1.1";
                //String url = "GET /ask?hostname=whois.iis.se&string=kth.se&port=43 HTTP/1.1";
                String[] requestLines = url.split("\\r?\\n");
                String[] requestLine = requestLines[0].split(" ");
                String requestMethod = requestLine[0];
                String requestURI = requestLine[1];

                //System.out.println("FULL URL: "+url);
                //System.out.println("Requestmethod: "+requestMethod);
                //System.out.println("URL: "+requestURI);

                if (!requestURI.startsWith("/ask?")) {
                    dataout.writeBytes(NotFound);
                    continue;
                }
                

                if (requestMethod.equals("GET") && requestURI.startsWith("/ask?")) {
                    try {
                        // Extract parameters from request URI
                    String[] urlparam = requestURI.split("\\?")[1].split("&");
                    for (String kv : urlparam) {             //Each part of the paramter is a key value pair
                     String[] pair = kv.split("=");          //Split at equals, ex limit = 500

                        if (pair[0].equals("hostname")) {
                            hostname = pair[1];
                            //System.out.println("Hostname "+hostname);
                        } else if (pair[0].equals("port")) {
                            portNumber = pair[1];
                            //System.out.println("Port "+portNumber);
                        } else if (pair[0].equals("string")) {
                            stringToSend = pair[1];
                            //System.out.println("Stringtosend "+stringToSend);
                         } else if (pair[0].equals("shutdown")) {
                            shutdown = true;
                        } else if (pair[0].equals("timeout")) {
                            timeout = Integer.parseInt(pair[1]);
                        } else if (pair[0].equals("limit")) {
                            limit = Integer.parseInt(pair[1]);
                        }
                    }
                    
                    // Init TCPclient and responsearray
                    if (stringToSend != null){
                        stringToBytes = (stringToSend+'\n').getBytes(StandardCharsets.UTF_8);
                    }
                    try {
                        TCPClient tcpClient = new TCPClient(shutdown, timeout, limit);
                        //System.out.println("Client created");
                        byte[] response = tcpClient.askServer(hostname, Integer.parseInt(portNumber), stringToBytes);
                        //System.out.println(hostname+ Integer.parseInt(portNumber)+ Arrays.toString(stringToBytes));
                        String stringresponse = new String(response);
                        //System.out.println("Server response: "+HTTPHeader+stringresponse);
                        dataout.writeBytes(HTTPHeader);
                        dataout.write(response);
                    }
                    catch(Exception e) { 
                        //System.err.println(e.getMessage());
                        //System.out.println("Caught bad request inside client try");
                        //System.out.println("To server = "+BadRequest);
                        dataout.writeBytes(BadRequest);
                    }
                    
                    
                } catch (Exception e){
                    //System.out.println("Caught not found");
                    //System.out.println("To server = "+NotFound);
                    dataout.writeBytes(NotFound);
                }
            } else {
                //System.out.println("Caught bad request outside if");
                //System.out.println("To server = "+BadRequest);
                dataout.writeBytes(BadRequest);
            }
        }   catch (Exception e){
            System.err.println("Exception cast: " + e);
            } finally {
                client.close();
            }
        }
        serverSocket.close();
    }   
}

