package com.km.cameraapp;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.util.Objects;
import java.util.Random;


public class MyUploadService extends IntentService {
    private static final String TAG = "MyDownloadService";


    private static final String ACTION_START_UPLOAD = "com.km.cameraapp.ACTION_START_UPLOAD";
    private static final String INTENT_DATA_URL = "INTENT_DATA_URL";
    private static final String INTENT_DATA_NAME = "INTENT_DATA_NAME";
    private static final String INTENT_DATA_ID = "INTENT_DATA_ID";

    private String name;
    private int perc=0;
    private int notifId;
    private String downloadPath;

    NotificationsUtil myNotifications;

    public static void startUploadFile(Context context, File file){
        Log.d(TAG, "startUploadFile: "+file.getAbsolutePath() );

        Intent intent=new Intent(context, MyUploadService.class);
        intent.setAction(ACTION_START_UPLOAD);
        //intent.putExtra(INTENT_DATA_URL,FileUtils.getUriForFile(context,BuildConfig.APPLICATION_ID,file));
        intent.putExtra(INTENT_DATA_URL,"file:///"+file.getAbsolutePath());
        //String name = id+"."+ext;
        String name = file.getName();
        int randomId=new Random().nextInt(100000)+1000;

        intent.putExtra(INTENT_DATA_NAME,name);
        intent.putExtra(INTENT_DATA_ID,randomId);
        ContextCompat.startForegroundService(context,intent);

        NotificationsUtil myNotif=new NotificationsUtil(context.getApplicationContext(),name,randomId);
        myNotif.updateNotification(context.getString(R.string.InQueue),true);

    }


    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public MyUploadService(String name) {
        super(name);
    }
    public MyUploadService() {
        super("download service");
    }

    @Override
    public void onDestroy() {
        Log.w(TAG, "onDestroy: " );
        super.onDestroy();
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        Log.d(TAG, "onHandleIntent: action="+intent.getAction()+" intent="+intent);
        if(intent!=null && Objects.equals(intent.getAction(), ACTION_START_UPLOAD)) {
            String dataurl=intent.getStringExtra(INTENT_DATA_URL);
            notifId=intent.getIntExtra(INTENT_DATA_ID,5241);

            name=intent.getStringExtra(INTENT_DATA_NAME);
            Log.d(TAG, "onHandleIntent: url="+dataurl);
            uploadFile(dataurl);
        }
        return ;
    }


    private void uploadFile(final String dataurl){
        perc=0;
        myNotifications=new NotificationsUtil(this.getApplicationContext(),name,notifId);
        myNotifications.updateNotification(getString(R.string.uploading_file),true);

        Log.d(TAG, "downloadUrl: url="+dataurl+" notifid :"+myNotifications.getNotifId());
        startForeground(myNotifications.getNotifId(),myNotifications.getNotification());

        String path = "images/" + System.currentTimeMillis() + ".jpg";
        FIrebaseStorageUtils.uploadImage(
                getApplicationContext(), path,dataurl, 1024,
                new FIrebaseStorageUtils.onUploadProgressListener() {

                    @Override
                    public void onSuccess(String downloadUrl) {
                        Prefs.Companion.setStringData(getApplicationContext(),Prefs.LAST_IMAGE,downloadUrl);
                        myNotifications.updateNotification(getString(R.string.uploaded),false);
                    }

                    @Override
                    public void onError(Exception e) {
                        myNotifications.updateNotification(getString(R.string.error),false,true);
                    }
                });

        /*
        final StorageReference storageReference= FirebaseStorage.getInstance().getReference().child(path);
        UploadTask uploadTask = storageReference.put(b);
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                // Continue with the task to get the download URL
                return storageReference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {

                if(task.isSuccessful()){
                    Uri downloadUrl=task.getResult();
                    String link=downloadUrl.toString();
                    listener.onSuccess(link);
                }else{
                    // Handle unsuccessful uploads
                    Log.e(TAG, "onFailure: ",task.getException());
                    listener.onError(task.getException());

                }
            }
        });
*/
        onDownloadFinished();

    }

    private void onDownloadFinished(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_DETACH);
        }else{
            stopForeground(false);
        }
    }

}
