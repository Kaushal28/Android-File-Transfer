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
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity
{
    private Button serverTransmitButton;
    private Button clientReceiveButton;
    private Button serverUDPButton;
    private Button clientUDPButton;
    private int PICKFILE_REQUEST_CODE = 100;
    private String filePath="";
    private String wholePath="";

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TCP
        serverTransmitButton = (Button) findViewById(R.id.button_TCP_server);
        serverTransmitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.i("Start Server Clicked", "yipee");

//////////////////////////////////////////////

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("file/*");
                startActivityForResult(intent, PICKFILE_REQUEST_CODE);



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


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        filePath = data.getDataString();

        Uri uri = data.getData();
        String uriString = uri.toString();
        File myFile = new File(uriString);
        String path = myFile.getAbsolutePath();

        if (uriString.startsWith("content://")) {
            Cursor cursor = null;
            try {
                cursor = this.getContentResolver().query(uri, null, null, null, null);
                if (cursor != null && cursor.moveToFirst()) {
                    filePath = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    Toast.makeText(this,filePath,Toast.LENGTH_LONG).show();
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        } else if (uriString.startsWith("file://")) {
            filePath = myFile.getName();
            Toast.makeText(this,filePath,Toast.LENGTH_LONG).show();
        }


        Uri Selected = data.getData();

        wholePath = getRealPathFromURI(Selected);

        Toast.makeText(this,wholePath,Toast.LENGTH_LONG).show();


        first f = new first(MainActivity.this,MainActivity.this,filePath,wholePath);
        f.execute();

        //TODO handle your request here
        super.onActivityResult(requestCode, resultCode, data);
    }


    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

}
