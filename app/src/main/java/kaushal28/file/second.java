package kaushal28.file;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by kaushal28 on 13/12/16.
 */

public class second extends AsyncTask<Void,Void,Void> {


    private Context context;
    private Activity activity;
    private boolean xceptionFlag = false;
    private ServerSocket ss;

    second(Context c, Activity a){
        this.context = c;
        this.activity = a;

    }

    @Override
    protected Void doInBackground(Void... voids) {


        try {

//            ServerSocket ss = new ServerSocket(5004);

            //this is done isntead of above line because it was givind error of address is already in use.
            ss = new ServerSocket();
            ss.setReuseAddress(true);
            ss.bind(new InetSocketAddress(5004));

            System.out.println("waiting");

            Socket socket = ss.accept();
            System.out.println("Accepted!");
            DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
//read the number of files from the client
            int number = dis.readInt();
            ArrayList<File>files = new ArrayList<File>(number);
            System.out.println("Number of Files to be received: " +number);

            ArrayList<Long> fileSize = new ArrayList<>(number);


            for(int i = 0; i < number ;i++){
                long size = dis.readLong();
                System.out.println(size);
                fileSize.add(size);
            }

            //read file names, add files to arraylist
            for(int i = 0; i< number;i++){
                File file = new File(dis.readUTF());
                files.add(file);
            }
            int n = 0;
            byte[]buf = new byte[4092];

            //outer loop, executes one for each file
            for(int i = 0; i < files.size();i++){

                System.out.println("Receiving file: " + files.get(i).getName());

                //Create new Folder for our app, if it is not there and store received files there in our separate folder.
                File folder = new File(Environment.getExternalStorageDirectory() +
                        File.separator + "File");
                boolean success = true;
                if (!folder.exists()) {
                    success = folder.mkdirs();
                }
                if (success) {
                    // Do something on success
                } else {
                    // Do something else on failure
                }


                //create a new fileoutputstream for each new file
                FileOutputStream fos = new FileOutputStream("mnt/sdcard/File/" +files.get(i).getName());
                //read file

                while (fileSize.get(i) > 0 && (n = dis.read(buf, 0, (int)Math.min(buf.length, fileSize.get(i)))) != -1)
                {
                    fos.write(buf,0,n);
                    long x = fileSize.get(i);
                    x = x-n;
                    fileSize.set(i,x);
                }
                fos.close();

//                while((n = dis.read(buf)) != -1){
//                    fos.write(buf,0,n);
//                    fos.flush();
//                }
//                fos.close();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            xceptionFlag = true;
            e.printStackTrace();

        }
        ////////////////////
        Log.i("== the end of read ====", "==");
        try{
            if(!ss.isClosed()){
                ss.close();
            }
        }
        catch (Exception e){
            xceptionFlag = true;
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (!xceptionFlag){
            Toast.makeText(context,"files Received Successfully!!",Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(context,"Something went wrong.",Toast.LENGTH_LONG).show();
        }

    }
}
