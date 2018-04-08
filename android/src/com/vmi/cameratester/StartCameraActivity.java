package com.vmi.cameratester;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;  //In gradle you need compile 'com.android.support:support-v4:25.3.1'
//import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.net.Uri;
import android.widget.ImageView;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

//public class StartCameraActivity extends Activity {

//com.vmi.cameratester.StartCameraActivity
public class StartCameraActivity extends org.qtproject.qt5.android.bindings.QtActivity {
    private static final String TAG = StartCameraActivity.class.getName();
    public String lastCameraFileUri;
    static final int REQUEST_OPEN_CAMERA =1;
    static final int REQUEST_PERMISSIONS = 2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        Log.d(TAG, "Enters StartCameraActivity.onCreate");
        Log.d(TAG, "getApplicationContext().getPackageName()!!: " + getApplicationContext().getPackageName());

        Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
        Thread[] threadArray = threadSet.toArray(new Thread[threadSet.size()]);
        Log.d(TAG, "All threads: "+threadArray);
        dispatchTakePictureIntent();
    }
    //Denna anropas alltid när en activity är typ klar. Den funkar osm ett slot i QT.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if  (requestCode == REQUEST_OPEN_CAMERA) {
            Log.d(TAG,"We arrived to read the file");
            Log.d(TAG,"This should be the lastCameraFileUri: "+ lastCameraFileUri);
            //och vad gör männskann här. När hen har fått tillbaka en bild. Jo
            Log.d(TAG, "If we arrive here, we could focus on how to make use of uri. funning finish()");

            Intent output = new Intent();
            output.putExtra("photoURI", lastCameraFileUri); //Send an uri back through the intent-extra-pipeline
            setResult(RESULT_OK, output);
            finish();

        } else if (requestCode == REQUEST_PERMISSIONS) {
            Log.d(TAG, "permission jox har körts");
        }
    }

    //This will start up the Native OEM-camera
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there’s a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go

            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.d(TAG, "ERROR ex.getLocalizedMessage() : " + ex.getLocalizedMessage());
                Log.d(TAG, "ERROR ex.getMessage()          : " + ex.getMessage());
            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                //The getApplicationContext().getPackageName() should return com.vmi;
                Log.d(TAG, "Before Uri photoURI...");
                Uri photoURI = FileProvider.getUriForFile(this,
                        getApplicationContext().getPackageName() + ".fileprovider",
                        photoFile);
                Log.d(TAG, "AFTER Uri photoURI...");

                lastCameraFileUri = photoFile.toString();
                Log.d(TAG, "photoFile.toString(): " + photoFile.toString());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_OPEN_CAMERA);

            }
        } else {
            Log.d(TAG, "ERROR. Problems with your camera?!");
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        String imageFileName = "XBalancer" + timeStamp;

        //Here we get the path where all pictures are stored
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        Log.d(TAG, "This is storageDir.toString() : " + storageDir.toString());

        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        Log.d(TAG, "This is image.toString()      : " + image.toString());

        return image;
    }


  @Override
  protected void onStop() {
      Log.w(TAG, "Activity stopped");

      super.onStop();
  }

  @Override
  protected void onDestroy() {
      Log.w(TAG, "Activity destroyed");

      super.onDestroy();
  }


}
