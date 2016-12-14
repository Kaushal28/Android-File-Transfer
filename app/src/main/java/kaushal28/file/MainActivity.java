package kaushal28.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity
{
    private Button serverTransmitButton;
    private Button clientReceiveButton;
    private Button serverUDPButton;
    private Button clientUDPButton;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TCP
        // from: http://www.rgagnon.com/javadetails/java-0542.html
        serverTransmitButton = (Button) findViewById(R.id.button_TCP_server);
        serverTransmitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i("Start Server Clicked", "yipee");

//////////////////////////////////////////////

                first f = new first(MainActivity.this,MainActivity.this);
                f.execute();

            }
        });


        clientReceiveButton = (Button) findViewById(R.id.button_TCP_client);
        clientReceiveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i("Read Button Clicked", "yipee");

                second s = new second(MainActivity.this,MainActivity.this);
                s.execute();

            }
        });

        // UDP
//        // from: http://www.helloandroid.com/tutorials/simple-udp-communication-example
//        serverUDPButton = (Button) findViewById(R.id.button_UDP_server);
//        serverUDPButton.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//
//                udpFirst uf = new udpFirst();
//                uf.execute();
//
//            }
//        });
//
//        clientUDPButton = (Button) findViewById(R.id.button_UDP_client);
//        clientUDPButton.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//
//                udpSecond us = new udpSecond(MainActivity.this);
//                us.execute();
//
//            }
//        });
    }
}