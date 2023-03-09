package com.example.projectchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.projectchat.loadAvatar.Avatar;
import com.example.projectchat.loadAvatar.LoadPicAvatar;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.pubsub.ConfigureForm;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.PayloadItem;
import org.jivesoftware.smackx.pubsub.PubSubException;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.pubsub.SimplePayload;
import org.jivesoftware.smackx.time.EntityTimeManager;
import org.jivesoftware.smackx.xdata.packet.DataForm;

import java.io.IOException;
import java.text.SimpleDateFormat;

public class AddMyContent extends AppCompatActivity {
TextView contentText;
    Button loadMyContent, ContentOk;
    Uri selectImage;
    String result;
    Uri getPathUri;
    ImageView ImageContent;
    WorkManager workManager;
    String servertime= null;
    String myUserJid;
    EditText mycontentText;
    static final int GALLERY_REQUEST_AVATAR = 1;
    static final int GALLERY_REQUEST_NEWS = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_my_content);
        ImageContent =(ImageView)findViewById(R.id.loadmyImage);
        contentText=(TextView)findViewById(R.id.textMyContent);
        loadMyContent=(Button)findViewById(R.id.loadMyContent);
        ContentOk=(Button)findViewById(R.id.LoadOkMyContent);
        mycontentText=(EditText)findViewById(R.id.textMyContent) ;

        ContentOk.setVisibility(View.INVISIBLE);

        myUserJid=App.getComponent().userauth().getS().getUser().asEntityBareJidString();
        loadMyContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("getAvatar", "getAvatarClick");
                Intent photoGetIntent = new Intent(Intent.ACTION_PICK);
                photoGetIntent.setType("image/*");
                startActivityForResult(photoGetIntent, GALLERY_REQUEST_AVATAR);
                loadMyContent.setVisibility(View.INVISIBLE);
                ContentOk.setVisibility(View.VISIBLE);
            }
        });

        ContentOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PubSubManager manager=App.getComponent().userauth().getPubSubManager();
                EntityTimeManager tim=EntityTimeManager.getInstanceFor(App.getComponent().userauth().getS());
                Log.d("Timestamp ", ""+tim);
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");


                long timestampgroup= 0;
                try {
                    timestampgroup = tim.getTime(App.getComponent().userauth().getS().getXMPPServiceDomain()).getTime().getTime();
                } catch (SmackException.NoResponseException e) {
                    e.printStackTrace();
                } catch (XMPPException.XMPPErrorException e) {
                    e.printStackTrace();
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                servertime = simpleDateFormat.format(timestampgroup);
                Data.Builder data = new Data.Builder();
                data.putString("result", result);
                data.putString("userjid", myUserJid);
                data.putString("filename", servertime+".jpeg");

                OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(LoadContent.class).setInputData(data.build()).build();
                workManager = WorkManager.getInstance(getApplicationContext());
                workManager.enqueue(oneTimeWorkRequest);
                workManager.getWorkInfoByIdLiveData(oneTimeWorkRequest.getId()).observe(AddMyContent.this, workInfo -> {

                    if (workInfo != null) {
                        WorkInfo.State state = workInfo.getState();
                        Log.d("state", "state = " + state.name());

                        if (state.name().matches("SUCCEEDED")) {
                            ConfigureForm form = new ConfigureForm(DataForm.Type.submit);

                            try {
                                LeafNode node = null;
                                node = (LeafNode) manager.getNode(App.getComponent().userauth().getS().getUser().asEntityBareJidString());

                                SimplePayload payload = new SimplePayload( "<message xmlns='https://mycontent'><body>" + mycontentText.getText() + "</body><from>"+myUserJid+"</from><time>"+servertime+"</time><file>Content"+servertime+".jpeg</file></message>");

                                PayloadItem<SimplePayload> itemWithNodeId = new PayloadItem<>(servertime, "avatarphoto", payload);

                                node.publish(itemWithNodeId);


                            } catch (SmackException.NoResponseException e) {
                                e.printStackTrace();
                            } catch (XMPPException.XMPPErrorException e) {
                                e.printStackTrace();
                            } catch (SmackException.NotConnectedException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }catch (PubSubException.NotAPubSubNodeException e) {
                                e.printStackTrace();
                            }
                            //Intent intent = new Intent(Avatar.this, MainActivity.class);
                            //startActivity(intent);
                            finish();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imegeReturn) {
        super.onActivityResult(requestCode, resultCode, imegeReturn);
        Bitmap bitmap = null;

        Log.d("requestCode", "" + requestCode);

        if (resultCode == Activity.RESULT_OK) {


            switch (requestCode) {
                case GALLERY_REQUEST_AVATAR:
                    selectImage = imegeReturn.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), selectImage);
                        Log.d("picture", "=" + selectImage.toString());
                        Log.d("requestCode", "=" + requestCode);
                        getRealPath(selectImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case GALLERY_REQUEST_NEWS:
                    getPathUri = imegeReturn.getData();

                    break;
            }
        }
    }

    private void getRealPath(Uri contentURI) {

        Cursor cursor = getApplicationContext().getContentResolver().query(contentURI, null, null, null, null);

        if (cursor == null) {
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 8;
            Bitmap bitmap = BitmapFactory.decodeFile(result, options);
            ImageContent.setImageBitmap(bitmap);
        }

    }

}