package com.mycompany.botcontrol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by rhishi on 28/3/15.
 */
public class clientSocket {


    public Socket initSocket(final String ipAddress ){
        Socket socket = null;
        final int serverPort = 23000;
        try {
            InetAddress host = InetAddress.getByName(ipAddress);
            //System.out.println("Connecting to server on port " + serverPort);
            socket = new Socket(host, serverPort);
        }
         catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return socket;
    }

    public String exchange_data(final Socket sock, final String data){
        String receivedString = "";
        PrintWriter toServer = null;
        BufferedReader fromServer = null;

        try {
            toServer = new PrintWriter(sock.getOutputStream(), true);
            fromServer = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            toServer.println(data);
            receivedString = fromServer.readLine();
            //System.out.println("Client received: " + receivedString + " from Server");

        }
        catch (IOException e) {
            closeStreams(toServer, fromServer);
            e.printStackTrace();
        }
        finally {
            closeStreams(toServer, fromServer);
            closeStreams(sock);
        }
        return receivedString;
    }

    private void closeStreams(PrintWriter toServer, BufferedReader fromServer) {
        try {
            toServer.close();
            fromServer.close();
        }
        catch (IOException e)
        {

        }
    }

    public void closeStreams(Socket sock) {
        try {
            sock.close();
        }
        catch (IOException e)
        {

        }
    }
}
