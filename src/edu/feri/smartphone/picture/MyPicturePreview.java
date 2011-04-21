package edu.feri.smartphone.picture;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import edu.feri.smartphone.R;

public class MyPicturePreview extends Activity {
	    private FrameLayout layout;
	    private ImageView iv;
	    private EditText edt;
	    private int mySetting;
	    private MyPictureFaceView mpv;

	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.picture_preview);
	        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

	        // Hide the window title.
	        //requestWindowFeature(Window.FEATURE_NO_TITLE);

	        // Create our Preview view and set it as the content of our activity.
	        try {
	            layout = (FrameLayout)this.findViewById(R.id.picturelayout);
	            iv = (ImageView)findViewById(R.id.imageView1);
	            mySetting = 0;
	            
	           
	        } catch (Exception e) {
	            e.printStackTrace();
	            new AlertDialog.Builder(this).setMessage(e.getMessage()).create().show();
	        }
	    }

		public void onClickRec(View v) {
			try {
				mpv = new MyPictureFaceView(this);
	            layout.addView(mpv);
				mySetting = Integer.parseInt(edt.getText().toString());
				mpv.setMySettings(mySetting);
			} catch (Exception e) {
			}
		}
	}