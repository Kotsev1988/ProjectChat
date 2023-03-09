package com.example.projectchat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.nfc.Tag;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import kotlin.Metadata;

public class MyWorker extends Worker {

    String lineend = "\r\n";
    String twohyphens = "--";
    final String boundary = "*****";
    URL url = null;
    HttpURLConnection urlConnection = null;
    String urlPic = "http://192.168.0.27/add.php/";
    //String urlPic="http://62.183.83.166:55010/add.php";

    DataOutputStream outputStream = null;
    String type;
    String filePath;
    String filenameVideo;

    File file1;
    public MyWorker(Context context, WorkerParameters parameters) {
        super(context, parameters);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            Log.d("myworker", "myworkerstart");
            InputStream inputStream = null;
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            Log.d("MimeWorker", getInputData().getString("mime"));

            type=getInputData().getString("type");
            filePath=getInputData().getString("result");



                file1 = new File(getInputData().getString("result"));

            FileInputStream fileInputStream = new FileInputStream(file1);
            url = new URL(urlPic);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setChunkedStreamingMode(0);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Connection", "Keep-Alive");
            urlConnection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            urlConnection.setReadTimeout(30000);
            urlConnection.setConnectTimeout(20000);

            outputStream = new DataOutputStream(urlConnection.getOutputStream());
            outputStream.writeBytes(twohyphens + boundary + lineend);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"" + "uploadfile" + "\"; filename=\"" + file1.getName() + "\"" + lineend);
            outputStream.writeBytes("Content-Type: "+getInputData().getString("mime") + "\r\n");
            outputStream.writeBytes("Content-Transfer-Encoding: binary" + "\r\n");
            outputStream.writeBytes("\r\n");
            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, 1024);
            buffer = new byte[bufferSize];
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            while (bytesRead > 0) {
                outputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, 1024);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }
            outputStream.writeBytes(lineend);
            outputStream.writeBytes(twohyphens + boundary + lineend);
            outputStream.writeBytes("Content-Type: application/json" + "\r\n");
            JSONObject json = new JSONObject();
            try {
                json.put("groupname", getInputData().getString("userjid"));
                json.put("dopName", getInputData().getString("name"));
                if (type.matches(".heic")){
                    json.put("type", ".jpeg");
                }else {
                    json.put("type", getInputData().getString("type"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            outputStream.writeBytes("Content-Disposition: form-data; name=\"groupname\";" + lineend);
            outputStream.writeBytes(lineend + json + lineend);
            outputStream.writeBytes(lineend);
            outputStream.writeBytes(twohyphens + boundary + twohyphens + lineend);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            inputStream = urlConnection.getInputStream();
            Log.d("inputstream", "" + inputStream.toString());
            String imputLine = null;
            StringBuffer responce = new StringBuffer();
            while ((imputLine = bufferedReader.readLine()) != null) {
                responce.append(imputLine);
            }
            Log.d("responce", "" + responce.toString());

            inputStream.close();
            fileInputStream.close();
            outputStream.flush();
            outputStream.close();
            urlConnection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
            return Result.failure();
        }
        return Result.success();

    }

    @Override
    public void onStopped() {
        Log.d("stoped", "WorkManager Stopped");
    }




}
