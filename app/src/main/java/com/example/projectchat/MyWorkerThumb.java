package com.example.projectchat;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.os.CancellationSignal;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;

import androidx.annotation.NonNull;
import androidx.work.ListenableWorker;
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

public class MyWorkerThumb extends Worker {

    String lineend = "\r\n";
    String twohyphens = "--";
    final String boundary = "*****";
    URL url = null;
    HttpURLConnection urlConnection = null;
    String urlPic = "http://192.168.0.27/addthumbnail.php/";
    //String urlPic="http://62.183.83.166:55010/add.php";
String filePath=getInputData().getString("result");
    DataOutputStream outputStream = null;
    File file1;
    Bitmap bitmapvideo;
    String filenameVideo;
    String type;
    public MyWorkerThumb(Context context, WorkerParameters parameters) {
        super(context, parameters);
    }

    @NonNull
    @Override
    public ListenableWorker.Result doWork() {
        try {
            Log.d("myworker", "myworkerstart");
            InputStream inputStream = null;
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            Log.d("fileThumboriginal", "" + getInputData().getString("result"));
            type=getInputData().getString("type");

            if (type.matches(".mp4")){
                file1=new File(getInputData().getString("result"));
                Size size=new Size(190,190);
                CancellationSignal cancellationSignal=new CancellationSignal();
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    try {
                         bitmapvideo= ThumbnailUtils.createVideoThumbnail(file1, size, cancellationSignal);
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                } else {
                     bitmapvideo= ThumbnailUtils.createVideoThumbnail(getInputData().getString("result"), MediaStore.Images.Thumbnails.MINI_KIND);

                }
                FileOutputStream out=null;
                filenameVideo=getFileName();
                try{
                    out=new FileOutputStream(filenameVideo);
                    bitmapvideo.compress(Bitmap.CompressFormat.JPEG, 80, out);
                }catch(FileNotFoundException exception){
                    exception.printStackTrace();
                }
                file1=new File(filenameVideo);
            }else {
                String fileOut = compressImage(getInputData().getString("result"));
                 file1 = new File(fileOut);
            }
            Log.d("fileThumb", "" + file1.getName() + file1.exists());
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
                json.put("groupname", getInputData().getString("userjid"));
                json.put("dopName", file1.getName());
               // json.put("type", "jpg");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            outputStream.writeBytes("Content-Disposition: form-data; name=\"groupname\";" + lineend);
            outputStream.writeBytes(lineend + json + lineend);
            outputStream.writeBytes(lineend);
            outputStream.writeBytes(twohyphens + boundary + twohyphens + lineend);

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            inputStream = urlConnection.getInputStream();
            Log.d("inputstreamThumb", "" + inputStream.toString());
            String imputLine = null;
            StringBuffer responce = new StringBuffer();
            while ((imputLine = bufferedReader.readLine()) != null) {
                responce.append(imputLine);
            }
            Log.d("responceThumb", "" + responce.toString());

            inputStream.close();
            fileInputStream.close();
            outputStream.flush();
            outputStream.close();
            urlConnection.disconnect();
            file1.delete();
        } catch (IOException e) {
            e.printStackTrace();
            return ListenableWorker.Result.failure();
        }
        return ListenableWorker.Result.success();

    }

    @Override
    public void onStopped() {
        Log.d("stoped", "WorkManager Stopped");
    }

public String compressImage(String imageUri){
       Bitmap scaledBitmap = null;

       BitmapFactory.Options options=new BitmapFactory.Options();
       options.inJustDecodeBounds=true;
       Bitmap bmp=BitmapFactory.decodeFile(imageUri, options);
       int actualHeight=options.outHeight;
       int actualWidth=options.outWidth;

       float maxHeight=150.0f;
       float maxWidth=150.0f;

       float imageRatio=actualWidth/actualHeight;
       float maxRatio=maxWidth/maxHeight;

       if (actualHeight>maxHeight || actualWidth>maxWidth){
           if (imageRatio<maxRatio){
               imageRatio=maxHeight/actualHeight;
               actualWidth=(int)(imageRatio*actualWidth);
               actualHeight=(int)maxHeight;
           } else if (imageRatio>maxRatio){
               imageRatio=maxWidth/actualWidth;
               actualHeight=(int)(imageRatio*actualHeight);
               actualWidth=(int)maxWidth;
           }else {
               actualHeight=(int)maxHeight;
               actualWidth=(int)maxWidth;
           }
       }

       options.inSampleSize=caluclateInSampleSize(options, actualWidth, actualHeight);
       options.inJustDecodeBounds=false;
       options.inPurgeable=true;
       options.inInputShareable=true;
       options.inTempStorage=new byte[16*1024];
       try{
           bmp=BitmapFactory.decodeFile(filePath, options);
       }catch(OutOfMemoryError exception){
           exception.printStackTrace();
       }
       try{
           scaledBitmap=Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
       }catch(OutOfMemoryError exception){
           exception.printStackTrace();
       }

       float ratioX=actualWidth/(float)options.outWidth;
       float ratioY=actualHeight/(float)options.outHeight;
       float middleX=actualWidth/2.0f;
       float middleY=actualHeight/2.0f;

    Matrix scaleMatrix=new Matrix();
    scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

    Canvas canvas=new Canvas(scaledBitmap);
    canvas.setMatrix(scaleMatrix);
    canvas.drawBitmap(bmp, middleX - bmp.getWidth()/2, middleY-bmp.getHeight()/2, new Paint(Paint.FILTER_BITMAP_FLAG));
    ExifInterface exif;
    try{
        exif=new ExifInterface(filePath);
        int orientation=exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,0);
        Matrix matrix=new Matrix();
        if (orientation==6){
            matrix.postRotate(90);
        } else if (orientation==3){
            matrix.postRotate(180);
        } else if (orientation==8){
            matrix.postRotate(270);
        }
        scaledBitmap=Bitmap.createBitmap(scaledBitmap, 0,0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,true);

    }catch(IOException exception){
        exception.printStackTrace();
    }
    FileOutputStream out=null;
    String filename=getFileName();
    try{
        out=new FileOutputStream(filename);
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
    }catch(FileNotFoundException exception){
        exception.printStackTrace();
    }
        return filename;
}

public String getFileName(){
        File file =new File(Environment.getExternalStorageDirectory()+File.separator, "mfchat");
    String uriString;
        if (!file.exists()){
            file.mkdir();
        }

        if (type.matches(".mp4") || type.matches(".heic")) {
             uriString = (file.getAbsolutePath() + "/" + getInputData().getString("name") + ".jpg");
        }else{
             uriString = (file.getAbsolutePath() + "/" + getInputData().getString("name") + type);
        }

        return uriString;
}
    public static int caluclateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight){
        final int height=options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height>reqHeight || width>reqWidth){
            final int halfHeight=height/2;
            final int halfWidth = width/2;
            while ((halfHeight/inSampleSize)>=reqHeight && (halfWidth/inSampleSize)>=reqWidth) {
                inSampleSize*=2;
            }
        }
        return inSampleSize;
    }


}

