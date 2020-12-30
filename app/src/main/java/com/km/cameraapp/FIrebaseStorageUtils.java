package com.km.cameraapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class FIrebaseStorageUtils {
    private static final String TAG = "FIrebaseStorageUtils";

    public static void uploadImage(Context context, String path, String imagetoBeUploaded, int uploadSize,
                                   final onUploadProgressListener listener) {
        byte[] b=null;
        try {
            Bitmap bitmap = BitmapUtils.decodeSampledBitmapFromUri(context, Uri.parse(imagetoBeUploaded),
                    uploadSize, uploadSize);
            b = BitmapUtils.getByteArrayFromBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
            listener.onError(new Exception("decodeSampledBitmapFromUri error"));
        }
        if(b==null){
            listener.onError(new Exception("bitmap empty after decode"));
            return;
        }
        final StorageReference storageReference= FirebaseStorage.getInstance().getReference().child(path);
        UploadTask uploadTask = storageReference.putBytes(b);
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

    }



    public interface onUploadProgressListener{
        void onSuccess(String downloadUrl);
        void onError(Exception e);
    }

}
