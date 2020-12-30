package com.km.cameraapp;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;


public class FileUtils {
    private static final String TAG = "FileUtils";

    public static boolean isFileDownloaded(Context context, String fileNameWithExt){
        File folder = getDownloadFileFolder(context);
        File newFile = new File(folder,fileNameWithExt);
        boolean exists= newFile.exists();
        return exists;
    }

    public static File getDownloadedFile(Context context, String fileNameWithExt){
        File folder = getDownloadFileFolder(context);
        return new File(folder,fileNameWithExt);
    }
    public static String getDownloadedFilePath(Context context, String fileNameWithExt){
        return getDownloadedFile(context,fileNameWithExt).getAbsolutePath();
    }
    public static String getNameFromUrl(String url){
        if(url==null || url.length()==0 ){
            return null;
        }
        return URLUtil.guessFileName(url, null, null);
    }

    public static File getDownloadFileFolder(Context context){
        //File downloadedfile = new File(getExternalStorageDirectory(),getString(R.string.app_name));
        File downloadedfile = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),context.getString(R.string.downloadFolderName));

        if(!downloadedfile.exists()){
            downloadedfile.mkdirs();
        }
        return downloadedfile;
    }

    public static String getInternalStorageRootPath(Context pContext){
        String[] rv=getStorageDirectories(pContext);
        String internalStorageDirectory= Environment.getExternalStorageDirectory().getAbsolutePath();
        String finalpath=null;
        for(String s:rv){
            if(internalStorageDirectory.startsWith(s)){
                finalpath=s;
                break;
            }
        }
        return finalpath;
    }
    public static String[] getStorageDirectories(Context pContext){
        // Final set of paths
        final Set<String> rv = new HashSet<>();

        //Get primary & secondary external device storage (internal storage & micro SDCARD slot...)
        File[]  listExternalDirs = ContextCompat.getExternalFilesDirs(pContext, null);
        for(int i=0;i<listExternalDirs.length;i++){
            if(listExternalDirs[i] != null) {
                String path = listExternalDirs[i].getAbsolutePath();
                int indexMountRoot = path.indexOf("/Android/data/");
                if(indexMountRoot >= 0 && indexMountRoot <= path.length()){
                    //Get the root path for the external directory
                    rv.add(path.substring(0, indexMountRoot));
                }
            }
        }
        return rv.toArray(new String[rv.size()]);
    }


    //BuildConfig.APPLICATION_ID
    public static Uri getUriForFile(Context context,String APPLICATION_ID,File file){
        if(file==null){
            return null;
        }
        Uri photoURI = FileProvider.getUriForFile(context,
                APPLICATION_ID + ".provider",
                //CommonUtilsConfig.authority_name,
                file);
        //for older devices
        //Uri.fromFile(file);
        return photoURI;
    }
    public static String getExtOfFile(String url){
        String e= MimeTypeMap.getFileExtensionFromUrl(url);
        return e;
    }
    public static String getExtOfFile(String url, String defaultExt){
        String e=getExtOfFile(url);
        if(e!=null && e.length()>0){
            return e;
        }
        return defaultExt;
    }

    /*
    convert application/pdf to pdf
     */
    public static String getExtensionFromMimeType(String mimeType){
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
    }

    /*
    convert pdf to application/pdf
     */
    public static String getMimeTypeFromExtension(String ext){
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
    }

    private static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            type = mime.getMimeTypeFromExtension(extension);
        }
        return type;
    }
    public static String getMimeTypeOfUri(Context context,Uri uri) {
        String extension;
        String type=null;

        //Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

        }
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public static boolean saveBitmapAsPngImage(File file, Bitmap bitmap){
        FileOutputStream fileOutputStream = null;
        boolean saved=false;
        try {
            fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            saved=true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return saved;
    }

    public static Float getMbFromBytes(float bytes){
        return Float.parseFloat(String.format(Locale.US,"%.2f",bytes/(1024f*1024f)));
    }


}
