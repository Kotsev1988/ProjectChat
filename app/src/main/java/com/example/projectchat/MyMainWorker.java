package com.example.projectchat;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.ListenableWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.google.common.util.concurrent.ListenableFuture;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

import javax.net.SocketFactory;

public class MyMainWorker extends Worker {
    Socket clientSocket;


    public MyMainWorker(@NonNull Context appContext, @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
        try {
            clientSocket= SocketFactory.getDefault().createSocket();
            InetSocketAddress inetSocketAddress=new InetSocketAddress("10.30.10.102", 4005);
            clientSocket.connect(inetSocketAddress, 10000);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @NonNull
    @Override
    public Result doWork() {
        BufferedReader in;
        BufferedWriter out;
        JSONObject json;
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

            json = new JSONObject();
            json.put("message", getInputData().getString("message"));
            json.put("user", getInputData().getString("userjid"));
            json.put("text", getInputData().getString("text"));
            json.put("phone", getInputData().getString("number"));
            json.put("fromm", getInputData().getString("from"));
            json.put("deviceToken", MainActivity.myToken);

            out.write(json + "\n");
            out.flush();

            String serverWord = in.readLine();
            Log.d("serverWord", "" + serverWord);
            in.close();
            out.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public void onStopped() {
        super.onStopped();
        Log.d("onStop", "onStop");
        //Result.success();
    }
}
