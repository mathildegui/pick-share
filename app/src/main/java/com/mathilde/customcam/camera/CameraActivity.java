package com.mathilde.customcam.camera;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.OrientationEventListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mathilde.customcam.R;
import com.mathilde.customcam.custom_pick.CustomPickActivity;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by gui on 24/07/2014.
 */

public class CameraActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.camera, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements View.OnClickListener {

        private static String TAG = "PlaceholderFragment";
        private Camera mCamera;
        private FrameLayout mPreview;
        private CameraPreview mCameraPreview;
        private MediaRecorder mMediaRecorder;

        private Button mSwitchCameraButton;
        private Button mGridButton;
        private Button mFlashButton;
        private Button mSwitchVideoButton;
        private ImageView mTakePictureButton;
        private ImageView mParamsButton;

        private Boolean mIsGrid = true;
        private Boolean mIsBack = true;
        private Boolean mIsCamera = true;
        private Boolean mIsRecording = false;
        private Boolean mOnProgress = false;
        private Boolean mIsTimeLapse = false;
        private GridLines mGridLines;
        private SaveFile mSaveFile;

        private int mOrientation;

        private OrientationEventListener mOrientationEventListener;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_camera, container, false);
            View v = initView(rootView);
            showOverLay();
            mOrientationEventListener = new OrientationEventListener(getActivity(), SensorManager.SENSOR_DELAY_NORMAL) {
                @Override
                public void onOrientationChanged(int iAngle) {
                    // 0  15 30 45 60 75, 90 105, 120, 135, 150, 165, 180, 195, 210, 225, 240, 255, 270, 285, 300, 315, 330, 345
                    final int iLookup[] = {0, 0, 0, 90, 90, 90, 90, 90, 90, 180, 180, 180, 180, 180, 180, 270, 270, 270, 270, 270, 270, 0, 0, 0}; // 15-degree increments
                    if (iAngle != ORIENTATION_UNKNOWN) {
                        int iNewOrientation = iLookup[iAngle / 15];
                        if (mOrientation != iNewOrientation) mOrientation = iNewOrientation;
                    }
                }
            };
            // To display if orientation detection will work and enable it
            if (mOrientationEventListener.canDetectOrientation())
                mOrientationEventListener.enable();
            return v;
        }

        public View initView(View v) {
            mPreview = (FrameLayout) v.findViewById(R.id.camera_preview);
            //Create the instance of the camera
            mGridButton = (Button) v.findViewById(R.id.grid);
            mSwitchCameraButton = (Button) v.findViewById(R.id.switch_camera);
            mGridLines = (GridLines) v.findViewById(R.id.grid_lines);
            mFlashButton = (Button) v.findViewById(R.id.flash);
            mTakePictureButton = (ImageView) v.findViewById(R.id.camera);
            mSwitchVideoButton = (Button) v.findViewById(R.id.switch_to_video);
            mParamsButton = (ImageView)v.findViewById(R.id.params);
            mSaveFile = new SaveFile(getActivity());

            initFloatingMenu();
            mSwitchCameraButton.setOnClickListener(this);
            mGridButton.setOnClickListener(this);
            mFlashButton.setOnClickListener(this);
            mTakePictureButton.setOnClickListener(this);
            mSwitchVideoButton.setOnClickListener(this);
            return v;
        }

        public Camera getCameraInstance(int cameraId) {
            Camera c = null;
            try {
                c = Camera.open(cameraId);
            } catch (Exception e) {
                //Kill if cannot access to camera
                Log.e(TAG, e.getMessage());
                Toast.makeText(getActivity().getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
            return c;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.grid:
                    displayGrid(mIsGrid);
                    break;
                case R.id.switch_camera:
                    switchCamera(mIsBack);
                    break;
                case R.id.flash:
                    switchFlash();
                    break;
                case R.id.camera:
                    if (mIsCamera) {
                        //prevent the double click on the camera button
                        if (!mOnProgress) {
                            mOnProgress = true;
                            mCamera.takePicture(null, null, mPicture);
                        }
                    }
                    else recordVideo();
                    break;
                case R.id.switch_to_video:
                    switchToVideo();
                    break;
            }
        }

        public void switchCamera(Boolean isBack) {
            releaseCamera();
            mIsBack = !isBack;
            if (mIsBack) {
                mCamera = getCameraInstance(Camera.CameraInfo.CAMERA_FACING_BACK);
            } else {
                mCamera = getCameraInstance(Camera.CameraInfo.CAMERA_FACING_FRONT);
            }
            mCameraPreview.update(mCamera);
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        public void displayGrid(Boolean isGrid) {
            mIsGrid = !isGrid;
            if (mIsGrid) {
                mGridLines.setVisibility(View.VISIBLE);
                mGridButton.setBackground(getResources().getDrawable(R.drawable.grid_on));
            } else {
                mGridLines.setVisibility(View.GONE);
                mGridButton.setBackground(getResources().getDrawable(R.drawable.grid_off));
            }
        }
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        public void switchFlash() {
            Camera.Parameters p = mCamera.getParameters();

            if (mIsBack) {
                if (p.getFlashMode().equals(Camera.Parameters.FLASH_MODE_AUTO)) {
                    p.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
                    mFlashButton.setBackground(getResources().getDrawable(R.drawable.ic_action_flash_on));
                } else if (p.getFlashMode().equals(Camera.Parameters.FLASH_MODE_ON)) {
                    p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    mFlashButton.setBackground(getResources().getDrawable(R.drawable.ic_action_flash_off));
                } else if (p.getFlashMode().equals(Camera.Parameters.FLASH_MODE_OFF)) {
                    p.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                    mFlashButton.setBackground(getResources().getDrawable(R.drawable.ic_action_flash_automatic));
                }
            } else
                mFlashButton.setEnabled(false);

            mCamera.setParameters(p);
        }

        private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

            public void onPictureTaken(byte[] data, Camera camera) {
                File pictureFile = mSaveFile.getOutputMediaFile(SaveFile.MEDIA_TYPE_IMAGE);
                if (pictureFile == null) {
                    Log.d(TAG, "Error creating media file, check storage permissions");
                    return;
                }

                /**
                 * TODO
                 * Check if is valid for all devices
                 */
                byte[] pictureBytes;
                BitmapFactory.Options opt;
                //Avoid outofmemory error in some devices
                opt = new BitmapFactory.Options();
                opt.inTempStorage = new byte[16 * 1024];
                Camera.Parameters parameters = camera.getParameters();
                Camera.Size size = parameters.getPictureSize();
                int height11 = size.height;
                int width11 = size.width;
                float mb = (width11 * height11) / 1024000;
                if (mb > 4f)
                    opt.inSampleSize = 4;
                else if (mb > 3f)
                    opt.inSampleSize = 2;
                Bitmap thePicture = BitmapFactory.decodeByteArray(data, 0, data.length,opt);

                Matrix m = new Matrix();
                if (mOrientation != 270 && Build.VERSION.SDK_INT != 20)
                    m.preRotate(mOrientation + 90);
                if(!mIsBack)m.preRotate(mOrientation + 180); //for the front camera - need to rotate

                thePicture = Bitmap.createBitmap(thePicture, 0, 0, thePicture.getWidth(), thePicture.getHeight(), m, false);

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                thePicture.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                pictureBytes = bos.toByteArray();

                //recycle bitmap to avoid outofmemeory error
                thePicture.recycle();
                FileOutputStream fs;
                try {
                    fs = new FileOutputStream(pictureFile);
                    fs.write(pictureBytes);
                    fs.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mOnProgress = false;
                releaseCamera();
                startActivity(new Intent(getActivity(), CustomPickActivity.class)
                        .putExtra("uri",Uri.fromFile(pictureFile))
                .putExtra("path",pictureFile.getAbsolutePath()));
            }
        };

        @Override
        public void onPause() {
            Log.d(TAG, "onPause");
            super.onPause();
            releaseMediaRecorder();       // if you are using MediaRecorder, release it first
            releaseCamera();
        }

        @Override
        public void onStop() {
            Log.d(TAG, "onStop");
            super.onStop();
            releaseMediaRecorder();       // if you are using MediaRecorder, release it first
            releaseCamera();
        }

        @Override
        public void onResume() {
            Log.d(TAG, "onResume");
            super.onResume();
            //TODO : diplay preview when user come back

            mCamera = getCameraInstance(Camera.CameraInfo.CAMERA_FACING_BACK);
            initParamsCamera();
            mCameraPreview = new CameraPreview(getActivity().getBaseContext(), mCamera);
            mPreview.addView(mCameraPreview, 0);
        }

//        public void setImage(File file, int rotate) {
//            if (rotate != 0) {
//                Bitmap myBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
//                int w = myBitmap.getWidth();
//                int h = myBitmap.getHeight();
//
//                // Setting pre rotate
//                Matrix mtx = new Matrix();
//                mtx.preRotate(rotate);
//
//                // Rotating Bitmap & convert to ARGB_8888, required by tess
//                myBitmap = Bitmap.createBitmap(myBitmap, 0, 0, w, h, mtx, false);
//                myBitmap = myBitmap.copy(Bitmap.Config.ARGB_8888, true);
//                mPreviewImageView.setImageBitmap(myBitmap);
//
//                //recycle bitmap to avoid outofmemeory error
//                if(myBitmap!=null) myBitmap.recycle();
//            } else
//                mPreviewImageView.setImageURI(Uri.fromFile(file));
//        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        public void recordVideo() {
            if (mIsRecording) {
                mMediaRecorder.stop();  // stop the recording
                releaseMediaRecorder(); // release the MediaRecorder object
                mCamera.lock();
                mIsRecording = false;
                mTakePictureButton.setBackground(getResources().getDrawable(R.drawable.round_button));
            } else {
                if (initMediaRecorder()) {
                    mMediaRecorder.start();
                    // inform the user that recording has started
                    mIsRecording = true;
                    mTakePictureButton.setBackground(getResources().getDrawable(R.drawable.round_button_recording));
                } else {
                    // prepare didn't work, release the camera
                    releaseMediaRecorder();
                    mTakePictureButton.setBackground(getResources().getDrawable(R.drawable.round_button));
                }
            }
        }

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        public void switchToVideo() {
            if (mIsCamera)
                mSwitchVideoButton.setBackground(getResources().getDrawable(R.drawable.ic_action_camera));
            else
                mSwitchVideoButton.setBackground(getResources().getDrawable(R.drawable.ic_action_video));
            mIsCamera = !mIsCamera;
        }

        public boolean initMediaRecorder() {

            mCamera.unlock();

            mMediaRecorder = new MediaRecorder();
            mMediaRecorder.setCamera(mCamera);

            if (!mIsTimeLapse) mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

            //Check the Android version
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
                mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
                mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.DEFAULT);

            } else {
                if (mIsTimeLapse) {
                    CamcorderProfile camcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_TIME_LAPSE_HIGH);
                    mMediaRecorder.setProfile(camcorderProfile);
                    // Step 5.5: Set the video capture rate to a low number
                    // capture a frame every 10 seconds
                } else if(mIsBack){
                    CamcorderProfile camcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
                    mMediaRecorder.setProfile(camcorderProfile);
                }else{
                    CamcorderProfile camcorderProfile = CamcorderProfile.get(Camera.CameraInfo.CAMERA_FACING_FRONT, CamcorderProfile.QUALITY_LOW);
                    mMediaRecorder.setProfile(camcorderProfile);
                }
                mMediaRecorder.setVideoSize(640, 480);
            }

            mMediaRecorder.setOutputFile(mSaveFile.getOutputMediaFile(SaveFile.MEDIA_TYPE_VIDEO).toString());
            mMediaRecorder.setPreviewDisplay(mCameraPreview.getHolder().getSurface());
            if (mIsTimeLapse) mMediaRecorder.setCaptureRate(0.1);

            /**
             * TODO
             * Check if is valid for all devices
             */
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                mMediaRecorder.setOrientationHint(90);
            try {
                mMediaRecorder.prepare();
            } catch (IllegalStateException e) {
                Log.d(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
                releaseMediaRecorder();
                return false;
            } catch (IOException e) {
                Log.d(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
                releaseMediaRecorder();
                return false;
            }
            return true;

        }

        private void releaseMediaRecorder() {
            if (mMediaRecorder != null) {
                mMediaRecorder.reset();   // clear recorder configuration
                mMediaRecorder.release(); // release the recorder object
                mMediaRecorder = null;
                mCamera.lock();           // lock camera for later use
            }
        }

        private void releaseCamera() {
            if (mCamera != null) {
                mCamera.release();        // release the camera for other applications
                mCamera = null;
                mCameraPreview.getHolder().removeCallback(mCameraPreview);
            }
        }

        /**
         * init the menu to the time lapse mode
         */
        public void initFloatingMenu(){
            SubActionButton.Builder itemBuilder = new SubActionButton.Builder(getActivity());

            ImageView itemIcon = new ImageView(getActivity());
            ImageView itemIcon2 = new ImageView(getActivity());
            itemIcon.setImageDrawable(getResources().getDrawable(R.drawable.small_circle_red));
            TextView itemText = new TextView(getActivity());
            itemText.setText("T.L.");
            itemIcon.setImageDrawable(getResources().getDrawable(R.drawable.small_circle_orange));
            SubActionButton button1 = itemBuilder.setContentView(itemIcon).build();
            SubActionButton button2 = itemBuilder.setContentView(itemIcon2).build();
            SubActionButton button3 = itemBuilder.setContentView(itemText).build();

            FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(getActivity())
                    .setStartAngle(-70)
                    .setEndAngle(-20)
                    .addSubActionView(button1)
                    .addSubActionView(button2)
                    .addSubActionView(button3)
                    .attachTo(mParamsButton)
                    .build();
            button3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
                    mIsTimeLapse = true;
                    mIsCamera = false;
                }
            });
        }

        /**
         * Init the initial camera parameters
         */
        public void initParamsCamera(){
            Camera.Parameters params = mCamera.getParameters();
            params.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            params.setPictureFormat(ImageFormat.JPEG);
            params.setJpegQuality(100);
            mCamera.setParameters(params);
        }

        public void showOverLay(){
            final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
            dialog.setContentView(R.layout.overlay_view);
            LinearLayout layout = (LinearLayout) dialog.findViewById(R.id.overlayLayout);
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }
}