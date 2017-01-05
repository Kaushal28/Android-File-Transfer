package kaushal28.file;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.preference.PreferenceManager;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class NameService extends Service {

    private volatile boolean running;
    private volatile String myName;
    private volatile ServerSocket serverSocket;

    public NameService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            serverSocket = new ServerSocket();
            serverSocket.bind(new InetSocketAddress(5006));
            serverSocket.setReuseAddress(true);
            System.out.println("name is published!");
          //  serverSocket.setSoTimeout(2000);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //Use shared preferences to save the name of the client!
//        myName = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
//                .getString("NAME_STRING", "TEST.NAME");

       myName = PreferenceManager.getDefaultSharedPreferences(this).getString("name", "Kaushal28");

        //Temporarily this is kaushal28.
       // myName = "Kaushal28";

        if (!running)
        {
            running = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (running)
                    {
                        try {
                            Socket socket = serverSocket.accept();
                            PrintWriter writer = new PrintWriter(new BufferedWriter(
                                    new OutputStreamWriter(socket.getOutputStream())),
                                    true);
                            writer.println(myName);

                            writer.close();
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }).start();
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        running = false;
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}