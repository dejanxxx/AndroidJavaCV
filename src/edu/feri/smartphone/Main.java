package edu.feri.smartphone;

import edu.feri.smartphone.camera.FacePreview;
import edu.feri.smartphone.picture.MyPicturePreview;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

// testface1.jpg source:
// http://www.wrinklecreamreview.net/wp-content/uploads/2010/08/wrinkle-free-face1.jpg

public class Main extends Activity implements OnClickListener {
	Button btnPicture;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        btnPicture = (Button) findViewById(R.id.button2);
        btnPicture.setOnClickListener(this);
    }
    
    public void onCameraClick(View v) {
    	startActivity(new Intent(this.getApplicationContext(), FacePreview.class));
    }
    
    public void onPictureClick(View v) {
    	Intent i = new Intent(this.getApplicationContext(), MyPicturePreview.class);
    	startActivity(i);
    }
    
    @Override
	public void onClick(View arg0) {
		if (arg0.getId()==R.id.button2) {
			Intent i = new Intent(this.getApplicationContext(), MyPicturePreview.class);
	    	startActivity(i);
		}
    }
}