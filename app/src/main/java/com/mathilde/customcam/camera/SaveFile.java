package com.mathilde.customcam.camera;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.mathilde.customcam.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by mathilde on 28/07/14.
 */
public class SaveFile {
    private static final String TAG = "SaveFile";

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private Context mContext;
    private String mAppName;

    public SaveFile(Context context){
        mContext = context;
        if(mContext.getResources().getIdentifier("app_name", "string", mContext.getPackageName()) != 0){
            mAppName = mContext.getResources().getString(R.string.app_name);
        }else{
            mAppName = "defaultAppName";
        }
    }

    /** Create a file Uri for saving an image or video */
    public Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    public File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), mAppName);

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d(mAppName, "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }
        return mediaFile;
    }
}
