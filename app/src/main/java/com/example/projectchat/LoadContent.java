package com.example.projectchat;

import android.content.Context;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoadContent extends Worker {
    String lineend = "\r\n";
    String twohyphens = "--";
    final String boundary = "*****";
    URL url = null;
    HttpURLConnection urlConnection = null;
    String urlPic = "http://192.168.0.27/addContent.php/";
    //String urlPic="http://62.183.83.166:55010/addAvatar.php";
    DataOutputStream outputStream = null;

    public LoadContent(Context context, WorkerParameters parameters) {
        super(context, parameters);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            Log.d("myworkerAvatar", "myworkerstart");
            Log.d("myUserJidW", "" + getInputData().getString("userjid"));
            InputStream inputStream = null;
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            File file1 = new File(getInputData().getString("result"));
            Log.d("file", "" + file1.getName() + file1.exists());
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
            outputStream.writeBytes("Content-Type: image/jpeg" + "\r\n");
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
                json.put("contentfolder", getInputData().getString("userjid"));
                json.put("contentname", getInputData().getString("filename"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            outputStream.writeBytes("Content-Disposition: form-data; name=\"content\";" + lineend);
            outputStream.writeBytes(lineend + json + lineend);
            outputStream.writeBytes(lineend);
            outputStream.writeBytes(twohyphens + boundary + twohyphens + lineend);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            inputStream = urlConnection.getInputStream();
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

