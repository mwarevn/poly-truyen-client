package com.example.poly_truyen_client.socket;

import com.example.poly_truyen_client.api.ConnectAPI;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketManager {

    private static SocketManager instance;
    private Socket socket;

    private SocketManager(String apiUrl) {
        try {
            socket = IO.socket(apiUrl);
            socket.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized SocketManager getInstance(String apiUrl) {
        if (instance == null) {
            instance = new SocketManager(apiUrl);
        }
        return instance;
    }

    public Socket getSocket() {
        return socket;
    }

}
