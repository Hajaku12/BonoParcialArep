package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.*;
import java.util.logging.Logger;

public class CalcReflexBEServer {
    public static void main(String[] args) throws IOException, URISyntaxException {
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(35000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }
        boolean running = true;
        while(running) {


            Socket clientSocket = null;
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }

            PrintWriter out = new PrintWriter(
                    clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            String inputLine, outputLine;
            boolean isFirstline = true;
            String firstLine="";
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Recibí: " + inputLine);
                //Sacando la primera linea con el metodo del encabezado
                if (isFirstline){
                    firstLine = inputLine;
                    isFirstline = false;
                }
                if (!in.ready()) {
                    break;
                }
            }

            URI requri = getReqURI(firstLine);

            if(requri.getPath().startsWith("/compreflex")){
                outputLine = "HTTP/1.1 200 OK\r\n"
                        + "Content-Type: application/json\r\n"
                        + "\r\n"
                        + "'{\"name\":\"John\", \"age\":30, \"car\":null}'";
            }else {
                outputLine =getDefaultResponse();
            }


            out.println(outputLine);
            out.close();
            in.close();
            clientSocket.close();
        }
        serverSocket.close();

    }

    public static String getDefaultResponse(){
        String htmlCode= "HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/html\r\n"
                + "\r\n"
                +"<!DOCTYPE html>\n" +
                "<html>\n" +
                "    <head>\n" +
                "        <title>Form Example</title>\n" +
                "        <meta charset=\"UTF-8\">\n" +
                "        <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    </head>\n" +
                "    <body>\n" +
                "        <h1>Method not found</h1>\n" +
                "    </body>\n" +
                "</html>";
        return htmlCode;
    }

    public static URI getReqURI(String firstline) throws URISyntaxException {

        String ruri = firstline.split(" ")[1];

        return new URI(ruri);

    }

    public static String computeMathCommand(String command) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class c = Math.class;
        Class[] parameterTypes={double.class};
        Method m = c.getDeclaredMethod("abs", parameterTypes);
        Object [] params = {-2.0};
        String resp = m.invoke(null, (Object) params).toString();
        return "";
    }
}