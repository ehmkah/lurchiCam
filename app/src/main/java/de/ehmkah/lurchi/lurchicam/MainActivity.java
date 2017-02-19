package de.ehmkah.lurchi.lurchicam;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private final static long WARTEZEIT_IN_MILLISEKUNDEN = 1000L;
    private final static long AMPLITUDE_TRIGGER = 30000;
    private final static String TAG = "LURCHI_CAM";

    private boolean hasCamera;
    private Camera mCamera;

    private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {


        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            File pictureFile = getOutputMediaFile();
            if (pictureFile == null) {
                Log.d("TAG", "error creating media type.");
                return;
            }

            try {
                FileOutputStream outputStream = new FileOutputStream(pictureFile);
                outputStream.write(data);
                outputStream.close();
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }
        }

        private File getOutputMediaFile() {
            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "LurchiCam");

            if (!mediaStorageDir.exists()) {
                mediaStorageDir.mkdir();
            }

            String timeStamp = new SimpleDateFormat("yyyyMMDD_HHmmss").format(new Date());
            File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");

            return mediaFile;

        }
    };
    private MediaRecorder mediaRecorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hasCamera = checkCameraHardware(getApplicationContext());
        if (hasCamera) {
            mCamera = getCameraInstance();
        }

    }

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        }

        return false;
    }

    public void klick(View v) {
        System.out.println("HUHU");
        takePhoto();
    }

    private void takePhoto() {
        mCamera.startPreview();
        mCamera.takePicture(null, null, mPictureCallback);
    }

    public void stopRecord(View v) {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder = null;
        }
    }

    public void startRecord(View v) {
        System.out.println("AUFNAHME");
        if (mediaRecorder == null) {
            try {

                mediaRecorder = new MediaRecorder();
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                String mediaFile = getExternalCacheDir().getAbsolutePath() + "/audiorecordtest.3gp";
                mediaRecorder.setOutputFile(mediaFile);
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                mediaRecorder.prepare();
                mediaRecorder.start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            int maxAmplitude = mediaRecorder.getMaxAmplitude();
            Log.d(TAG, "max aplitude" + maxAmplitude);
            if (maxAmplitude > AMPLITUDE_TRIGGER) {
                takePhoto();
            }
        }
    }

    public static Camera getCameraInstance() {
        Camera result = null;
        try {
            result = Camera.open();
        } catch (Exception e) {
            // should not happen
            System.out.println(e);
        }
        return result;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCamera == null) {
            mCamera = getCameraInstance();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        mCamera.release();
        mCamera = null;

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }


}
