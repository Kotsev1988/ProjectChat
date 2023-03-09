package com.example.projectchat.loadAvatar;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.projectchat.App;
import com.example.projectchat.MainActivity;
import com.example.projectchat.R;
/*import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;*/

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.pubsub.AccessModel;
import org.jivesoftware.smackx.pubsub.ConfigureForm;
import org.jivesoftware.smackx.pubsub.Item;
import org.jivesoftware.smackx.pubsub.ItemPublishEvent;
import org.jivesoftware.smackx.pubsub.LeafNode;
import org.jivesoftware.smackx.pubsub.PayloadItem;
import org.jivesoftware.smackx.pubsub.PubSubException;
import org.jivesoftware.smackx.pubsub.PubSubManager;
import org.jivesoftware.smackx.pubsub.PublishModel;
import org.jivesoftware.smackx.pubsub.SimplePayload;
import org.jivesoftware.smackx.pubsub.listener.ItemEventListener;
import org.jivesoftware.smackx.time.EntityTimeManager;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;
import org.jivesoftware.smackx.xdata.Form;
import org.jivesoftware.smackx.xdata.packet.DataForm;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class Avatar extends AppCompatActivity {
    static final int GALLERY_REQUEST_AVATAR = 1;
    static final int GALLERY_REQUEST_NEWS = 2;
    Uri selectImage;
    String result;
    String userjid;
    WorkManager workManager;
    String servertime= null;
    ImageView avaCheck;
    Button loadAva, avaOk;
   // private StorageReference mStorageRef;
    //private DatabaseReference mDataBaseRef;
   // private StorageTask mUploadTask;


    Uri getPathUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loadavatar);
        avaCheck=(ImageView)findViewById(R.id.avaCheck);
        loadAva=(Button)findViewById(R.id.loadMainAvatar);
        avaOk=(Button)findViewById(R.id.LoadOk);
        avaOk.setVisibility(View.INVISIBLE);
        Completable.fromAction(new Action() {
            @Override
            public void run() throws Exception {
                userjid = App.getComponent().database().myLoginsDao().my_user_name();
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.io()).subscribe();


        loadAva.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("getAvatar", "getAvatarClick");
                Intent photoGetIntent = new Intent(Intent.ACTION_PICK);
                photoGetIntent.setType("image/*");
                startActivityForResult(photoGetIntent, GALLERY_REQUEST_AVATAR);
                loadAva.setVisibility(View.INVISIBLE);
                avaOk.setVisibility(View.VISIBLE);
            }
        });

        avaOk.setOnClickListener(new View.OnClickListener() {
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
                data.putString("userjid", userjid);
                data.putString("filename", servertime+".jpeg");

                OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(LoadPicAvatar.class).setInputData(data.build()).build();
                workManager = WorkManager.getInstance(getApplicationContext());
                workManager.enqueue(oneTimeWorkRequest);
                workManager.getWorkInfoByIdLiveData(oneTimeWorkRequest.getId()).observe(Avatar.this, workInfo -> {

                    if (workInfo != null) {
                        WorkInfo.State state = workInfo.getState();
                        Log.d("state", "state = " + state.name());

                        if (state.name().matches("SUCCEEDED")) {
                            ConfigureForm form = new ConfigureForm(DataForm.Type.submit);

                            try {
                                LeafNode node = null;
                                node = (LeafNode) manager.getNode(App.getComponent().userauth().getS().getUser().asEntityBareJidString());

                                SimplePayload payload = new SimplePayload("<data xmlns='https://myproject'>Avatar"+servertime+".jpeg"+"</data>");


                                PayloadItem<SimplePayload> itemWithNodeId = new PayloadItem<>(App.getComponent().userauth().getS().getUser().asEntityBareJidIfPossible().toString(), "avatarphoto", payload);
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
                            Intent intent = new Intent(Avatar.this, MainActivity.class);
                            startActivity(intent);
                        }
                    }
                });
            }
        });

    }


   /* public void loadcontent(View v) {
        if (mUploadTask != null && mUploadTask.isInProgress()) {
            Log.d("Upload in progress", "Upload in progress");

        } else {
            Log.d("getAvatar", "getAvatarClick");
            Intent photoGetContent = new Intent(Intent.ACTION_PICK);
            photoGetContent.setType("image/*");
            startActivityForResult(photoGetContent, GALLERY_REQUEST_NEWS);
        }

    }*/

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
            avaCheck.setImageBitmap(bitmap);
        }

    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {
      /*  if (getPathUri != null) {
            StorageReference fileReference = mStorageRef.child(System.currentTimeMillis() + "." + getFileExtension(getPathUri));


            mUploadTask = fileReference.putFile(getPathUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.d("uploadSuccess", "uploadSuccess");

                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Upload upload = new Upload("new content", uri.toString());
                            String uploadid = mDataBaseRef.push().getKey();
                            mDataBaseRef.child(uploadid).setValue(upload);
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("uploadFailure", "uploadFailure");
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());

                }
            });
        } else {
            Log.d("noFile", "no File Selected");
        }*/

    }
}
