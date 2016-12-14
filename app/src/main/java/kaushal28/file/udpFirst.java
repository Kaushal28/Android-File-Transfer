package kaushal28.file;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by kaushal28 on 13/12/16.
 */

public class udpFirst extends AsyncTask<Void,Void,Void> {
    @Override
    protected Void doInBackground(Void... voids) {


        try {
            String messageStr="Hello Android!";
            int server_port = 6668;
            // TODO: fill in UDP Client IP
            InetAddress local = InetAddress.getByName("192.168.43.139");
            DatagramPacket p = new DatagramPacket(messageStr.getBytes(),
                    messageStr.length(),
                    local,server_port);
            Log.i("UDP server about to: ", "send");
            DatagramSocket s = new DatagramSocket();
            s.send(p);
            s.close();
            Log.i("***** UDP server: ", "Done sending");
        } catch ( SocketException e) {
            Log.i("***** UDP server has: ", "Socket Exception");
        } catch ( UnknownHostException e ) {
            Log.i("***** UDP server has: ", "UnknownHostException");
        } catch (IOException e){
            Log.i("UDP server has Exc", "e: " + e);
        }

        return null;
    }
}
