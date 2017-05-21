package qin.imgvid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    @Override
    public void onWindowFocusChanged(boolean hasFocas) {
        super.onWindowFocusChanged(hasFocas);
        View decorView = getWindow().getDecorView();
        if (hasFocas) {
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);//Make a full screen android app
        }
    }

    private Button mRecord;
    private Button mPlay;
    private VideoView mVideoView;
    private int ACTIVITY_START_CAMERA_APP = 0;
    private Button mButton;
    private String mImageFileLocation = "";
    private ImageView mPhotoCapturedImageView;
    private Button mWelcomeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Display the layout on the screen based on activity_main.xml

        mRecord = (Button) findViewById(R.id.recordButton);
        //mRecord (Java File) Represents Button -> id:recordButton (xml Layout File)
        mPlay = (Button) findViewById(R.id.playButton);
        //mPlay (Java File) Represents Button -> id:playButton (xml Layout File)
        mVideoView = (VideoView) findViewById(R.id.videoView);
        //mVideoView (Java File) Represents VideoView -> id:videoView (xml Layout File)
        mButton = (Button) findViewById(R.id.photoButton);
        //mButton (Java File) Represents Button (xml Layout File)
        mPhotoCapturedImageView = (ImageView) findViewById(R.id.capturePhotoImageView);
        //mPhotoCapturedImageView (Java File) Represents ImageView (xml Layout File)
        mWelcomeButton = (Button) findViewById(R.id.welcomeButton);
        //mWelcomeButton (Java File) Represents Button -> id:welcomeButton (xml Layout File)

        mRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callVideoAppIntent = new Intent();
                callVideoAppIntent.setAction(MediaStore.ACTION_VIDEO_CAPTURE);
                //Camera Intent: When we call callVideoAppIntent,
                //means we want Android phone to open its own
                //camera and take a video

                startActivityForResult(callVideoAppIntent, ACTIVITY_START_CAMERA_APP);
                //After clicking mButton, we set ACTIVITY_START_CAMERA_APP = 0
                //means start the camera intent (callVideoAppIntent)
                //Basically, Android Phone will do four things here:
                //           First: Capture a video
                //           Second: Store the photo to Movies
                //           Third: If the video is taken successfully, RESULT_OK = -1
                //           Fourth: Produce intent data
            }
        });

        mPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mVideoView.start();
                //After clicking mPlay, the video captured before starts displaying in mVideoView(VideoView)
            }
        });

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Intent callcameraApplicationIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //Camera Intent: When we call callcameraApplicationIntent,
                //               means we want Android phone to open its own
                //               camera and take a photo

                File photoFile = null;
                try {
                    photoFile = createImageFile();//photoFile = image
                    //So photoFile has two parts too
                    //First, it has unique name (imageFileName)
                    //Second, it has unique place to store (storageDirectory)
                } catch (IOException e) {
                    e.printStackTrace();
                }
                callcameraApplicationIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                //Uri points to the photoFile's unique store place
                //After taking a photo, We tell android phone where should store the full
                //resolution photo

                startActivityForResult(callcameraApplicationIntent, ACTIVITY_START_CAMERA_APP);
                //After clicking mButton, we set ACTIVITY_START_CAMERA_APP = 0
                //means start the camera intent (callcameraApplication)
                //Basically, Android Phone will do four things here:
                //           First: Take a photo
                //           Second: store the photo to PICTURE
                //           Third:  if the photo is taken successfully, RESULT_OK = -1
                //           Fourth: produce intent data

            }
        });

        mWelcomeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WelcomeMessage();//After clicking mWelcomeButton, two toasts will show on the screen
            }
        });
    }

    void WelcomeMessage() {
        Toast.makeText(this,"Videos saved in: files-> Homepages-> Movies", Toast.LENGTH_LONG).show();
        //Make a toast to tell users the path which stores videos captured
        Toast.makeText(this,"Photos saved in: files-> Home-> Pictures", Toast.LENGTH_LONG).show();
        //Make a toast to tell users the path which stores photo captured
    }

    File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyMMdd_HHmmss").format(new Date());
        //Create a unique timeStamp string using time.
        //For example: timeStamp = "20160515_122315"
        String imageFileName = "IMAGE_" + timeStamp + "_";
        //Create a unique imageFileName string using timeStamp
        //For example: imageFileName = "IMAGE_20160515_122315_"
        File storageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        //storageDirectory is the place to store the photo captured
        //In this case, we store the captured photo in PICTURES

        File image = File.createTempFile(imageFileName,".jpg",storageDirectory);
        //Remember: image has two parts.
        //First, it has unique name (imageFileName)
        //Second, it has unique place to store (storageDirectory)
        mImageFileLocation = image.getAbsolutePath();
        //mImageFileLocation is the full path which image is stored

        return image;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == ACTIVITY_START_CAMERA_APP && resultCode == RESULT_OK) {
            Uri videoUri = data.getData();
            //After capturing a video successfully, videoUri represents the unique place which stores video captured before
            mVideoView.setVideoURI(videoUri);
            //The video which will be displayed in mVideoView can be found by the path videoUri
            //Toast.makeText(this,"The video has been successfully saved", Toast.LENGTH_SHORT).show();
            //Toast.makeText(this,"Press Play Button to preview", Toast.LENGTH_LONG).show();

            setReducedImageSize();
            //If the camera intent (callcameraApplication) is working successfully then go to method setReducedImageSize
            //This method rescales the photo captured to make it fits ImageView and displays on the screen
        }
    }

    void setReducedImageSize() {
        int targetImageViewWidth = mPhotoCapturedImageView.getWidth();//Get the Width of ImageView
        int targetImageViewHeight = mPhotoCapturedImageView.getHeight();//Get the Height of ImageView

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mImageFileLocation, bmOptions);
        int cameraImageWidth = bmOptions.outWidth;
        int cameraImageHeight = bmOptions.outHeight;

        int scaleFactor = Math.min(cameraImageWidth/targetImageViewWidth, cameraImageHeight/targetImageViewHeight);
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inJustDecodeBounds = false;

        Bitmap photoReducedSizeBitmap = BitmapFactory.decodeFile(mImageFileLocation, bmOptions);
        //Now photoReducedSizeBitmap = rescaled captured photo
        mPhotoCapturedImageView.setImageBitmap(photoReducedSizeBitmap);
        //Make rescaled photo (photoReducedSizeBitmap) show on the ImageView(mPhotoCapturedImageView)
        //Toast.makeText(this, "Photo  has  been  taken  and  stored  successfully.  Great !!! ", Toast.LENGTH_LONG).show();
        // Make a toast to deliver success massage after done
    }
}
