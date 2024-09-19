package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.logging.Logger;

public class CalcReflexFacade {
    public static void main(String[] args) throws IOException, URISyntaxException {
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(36000);
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

            URI requery = getReqURI(firstLine);

            if(requery.getPath().startsWith("/computar")){
                outputLine = HttpConnectionExample.getResponse("/compreflex?" + requery.getQuery());
            }else {
                outputLine =gethtmlClient();
            }


            out.println(outputLine);
            out.close();
            in.close();
            clientSocket.close();
        }
        serverSocket.close();

    }

    public static String gethtmlClient(){
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
                "        <h1>Form with GET</h1>\n" +
                "        <form action=\"/hello\">\n" +
                "            <label for=\"name\">Name:</label><br>\n" +
                "            <input type=\"text\" id=\"comando\" name=\"comando\" value=\"max(1,0,2.0)\"><br><br>\n" +
                "            <input type=\"button\" value=\"Submit\" onclick=\"loadGetMsg()\">\n" +
                "        </form> \n" +
                "        <div id=\"getrespmsg\"></div>\n" +
                "\n" +
                "        <script>\n" +
                "            function loadGetMsg() {\n" +
                "                let nameVar = document.getElementById(\"comando\").value;\n" +
                "                const xhttp = new XMLHttpRequest();\n" +
                "                xhttp.onload = function() {\n" +
                "                    document.getElementById(\"getrespmsg\").innerHTML =\n" +
                "                    this.responseText;\n" +
                "                }\n" +
                "                xhttp.open(\"GET\", \"/computar?comando=\"+nameVar);\n" +
                "                xhttp.send();\n" +
                "            }\n" +
                "        </script>\n" +

                "    </body>\n" +
                "</html>";
        return htmlCode;
    }

    public static URI getReqURI(String firstline) throws URISyntaxException {

        String ruri = firstline.split(" ")[1];

        return new URI(ruri);

    }
}