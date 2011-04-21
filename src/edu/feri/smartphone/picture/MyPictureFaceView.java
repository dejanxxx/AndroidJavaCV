package edu.feri.smartphone.picture;

import static com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
import static com.googlecode.javacv.cpp.opencv_core.cvClearMemStorage;
import static com.googlecode.javacv.cpp.opencv_core.cvGetSeqElem;
import static com.googlecode.javacv.cpp.opencv_core.cvLoad;
import static com.googlecode.javacv.cpp.opencv_objdetect.CV_HAAR_DO_CANNY_PRUNING;
import static com.googlecode.javacv.cpp.opencv_objdetect.cvHaarDetectObjects;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.cpp.opencv_objdetect;
import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_objdetect.CvHaarClassifierCascade;

public class MyPictureFaceView extends View {
	public static final int SUBSAMPLING_FACTOR = 4;
	private int iPic=0;
	private ExecutorService processingExecutor;
	private IplImage grayImage;
	private CvHaarClassifierCascade classifier;
	private CvMemStorage storage;
	private CvSeq faces;
	private int mySettings;
	private ImageView iv;

	/**
	 * 1. initializes classifier from xml file (standard face CV recognition file)
	 * @param context
	 * @throws IOException
	 */
	public MyPictureFaceView(MyPicturePreview context) throws IOException {
		super(context);
		this.processingExecutor = Executors.newSingleThreadExecutor();

		File classifierFile = Loader.extractResource(getClass(),
				"/com/googlecode/javacv/facepreview/haarcascade_frontalface_alt.xml",
				context.getCacheDir(), "classifier", ".xml");
		if (classifierFile == null || classifierFile.length() <= 0) {
			throw new IOException("Could not extract the classifier file.");
		}
		classifierFile.deleteOnExit();

		// Preload the opencv_objdetect module to work around a known bug.
		Loader.load(opencv_objdetect.class);
		classifier = new CvHaarClassifierCascade(cvLoad(classifierFile.getAbsolutePath()));
		if (classifier.isNull()) {
			throw new IOException("Could not load the classifier file.");
		}
		storage = CvMemStorage.create();

		setMySettings(0); // default
	}



	public void setIv2(ImageView iv) {
		this.iv = iv;
		Bitmap bm;
		try 
		{
			AssetManager am = getContext().getAssets();
			String list[] = am.list("");
			int count_files = list.length;
			
			while (true)
			{
				if (list[iPic].contains(".png")||list[iPic].contains(".bmp")||list[iPic].contains(".jpg")) {
					BufferedInputStream buf = new BufferedInputStream(am.open(list[iPic]));
					Bitmap bitmap = BitmapFactory.decodeStream(buf);
					iv.setImageBitmap(bitmap);
					iv.invalidate();
					buf.close();
					//bitmap.
					//byte[] bitmapdata = bitmap.;
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					bitmap.compress(CompressFormat.PNG, 0 , bos);
					//bitmap.
					//byte[] bitmapdata = bos.toByteArray();

					int w =bitmap.getWidth();
					int h =bitmap.getHeight();
					//Log.e("TEST", bitmapdata.length+" w:"+w+" h:"+h+" "+(w*h)+" Diff:"+(bitmapdata.length-(w*h)));
					onPreviewFrame2(bitmap, w, h);
					
					iPic++;
					iPic = iPic%count_files;
					break;
				}
				iPic++;
				iPic = iPic%count_files;
				
			}
		}   
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	
	}

	public void setIv(ImageView iv) {
		this.iv = iv;

		URL url;
		Bitmap bm;
		try {
			url = new URL("http://zhoyakatsuki.files.wordpress.com/2010/01/emmanuelle-chriqui.png");
			URLConnection conn=url.openConnection();
			conn.connect();
			InputStream is = conn.getInputStream();
			BufferedInputStream bis = new BufferedInputStream(is);
			bm = BitmapFactory.decodeStream(bis);
			bis.close();
			is.close();

			iv.setImageBitmap(bm);

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			bm.compress(CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
			byte[] bitmapdata = bos.toByteArray();

			int w =bm.getWidth();
			int h =bm.getHeight();
			Log.e("TEST", bitmapdata.length+" w:"+w+" h:"+h+" "+(w*h)+" Diff:"+(bitmapdata.length-(w*h)));

			onPreviewFrame(bitmapdata, w, h);
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		//        FileInputStream in;
		//        BufferedInputStream buf;
		//        try {
		//       	    in = new FileInputStream("/sdcard/test2.png");
		//            buf = new BufferedInputStream(in);
		//            Bitmap bMap = BitmapFactory.decodeStream(buf);
		//            iv.setImageBitmap(bMap);
		//            if (in != null) {
		//         	in.close();
		//            }
		//            if (buf != null) {
		//         	buf.close();
		//            }
		//            
		//        } catch (Exception e) {
		//            Log.e("Error reading file", e.toString());
		//        }

		//		Bitmap image = ((BitmapDrawable)iv.getDrawable()).getBitmap();
		//		
		//		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		//		bm.compress(CompressFormat.JPEG, 0 /*ignored for PNG*/, bos);
		//		byte[] bitmapdata = bos.toByteArray();
		//		
		////		Bitmap mIcon1 = BitmapFactory.decodeStream(url_value.openConnection().getInputStream());
		////        iv.setImageBitmap(mIcon1);
		//		Bitmap bMap = BitmapFactory.decodeFile("testface1.jpg");
		//        //image.setImageBitmap(bMap);
		//		
		//		ByteBuffer bf = ByteBuffer.allocate(image.getRowBytes()*image.getHeight());
		//		image.copyPixelsToBuffer(bf);
		//		onPreviewFrame(bitmapdata, image.getWidth(), image.getHeight());
	}



	public ImageView getIv() {
		return iv;
	}

	public void onPreviewFrame(final byte[] data, final int width, final int height) {
		processingExecutor.execute(new Runnable() {
			public void run() {
				processImage(data, width, height);
			}
		});
	}
	public void onPreviewFrame2(final Bitmap data, final int width, final int height) {
		processingExecutor.execute(new Runnable() {
			public void run() {
				processImage2(data, width, height);
			}
		});
	}

	/**
	 * 
	 * @param data
	 * @param width
	 * @param height
	 */
	protected void processImage(byte[] data, int width, int height) {
		// First, downsample our image and convert it into a grayscale IplImage
		int f = SUBSAMPLING_FACTOR;
		if (grayImage == null || grayImage.width() != width/f || grayImage.height() != height/f) {
			grayImage = IplImage.create(width/f, height/f, IPL_DEPTH_8U, 1);
		}
		int imageWidth  = grayImage.width();
		int imageHeight = grayImage.height();
		int dataStride = f*width;
		int imageStride = grayImage.widthStep();
		ByteBuffer imageBuffer = grayImage.getByteBuffer();
		for (int y = 0; y < imageHeight; y++) {
			int dataLine = y*dataStride;
			int imageLine = y*imageStride;
			for (int x = 0; x < imageWidth; x++) {
				imageBuffer.put(imageLine + x, data[dataLine + f*x]);
			}
		}

		faces = cvHaarDetectObjects(grayImage, classifier, storage, 1.1, 3, CV_HAAR_DO_CANNY_PRUNING);
		postInvalidate();  // refresh view
		//        iv.invalidate();
		//        invalidate();
		cvClearMemStorage(storage);
	}
	public Bitmap toGrayscale(Bitmap bmpOriginal)
	{        
	    int width, height;
	    height = bmpOriginal.getHeight();
	    width = bmpOriginal.getWidth();    

	    Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
	    Canvas c = new Canvas(bmpGrayscale);
	    Paint paint = new Paint();
	    ColorMatrix cm = new ColorMatrix();
	    cm.setSaturation(0);
	    ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
	    paint.setColorFilter(f);
	    c.drawBitmap(bmpOriginal, 0, 0, paint);
	    return bmpGrayscale;
	}
	protected void processImage2(Bitmap data, int width, int height) {
		// First, downsample our image and convert it into a grayscale IplImage
		int f = 1; //SUBSAMPLING_FACTOR;
		data = toGrayscale(data);
//		if (grayImage == null){ //|| grayImage.width() != width/f || grayImage.height() != height/f) {
			grayImage = IplImage.create(width/f, height/f, IPL_DEPTH_8U, 1);
//		}
		int imageWidth  = grayImage.width();
		int imageHeight = grayImage.height();
		ByteBuffer imageBuffer = grayImage.getByteBuffer();
		for (int y = 0; y < imageHeight; y++) {
			for (int x = 0; x < imageWidth; x++) {
				imageBuffer.put((byte) data.getPixel(x, y));
			}
		}

		faces = cvHaarDetectObjects(grayImage, classifier, storage, 1.1, 3, CV_HAAR_DO_CANNY_PRUNING);
		postInvalidate();  // refresh view
		cvClearMemStorage(storage);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Paint paint = new Paint();
		paint.setColor(Color.RED);
		paint.setTextSize(20);

		String s = "FacePreview - This side up.";
		float textWidth = paint.measureText(s);
		canvas.drawText(s, (getWidth()-textWidth)/2, 20, paint);

		if (faces != null && faces.total()!=0) {
			paint.setStrokeWidth(2);
			paint.setStyle(Paint.Style.STROKE);
			float scaleX = 1;//(float)getWidth()/grayImage.width();
			float scaleY = 1;//(float)getHeight()/grayImage.height();
			int total = faces.total();
			for (int i = 0; i < total; i++) {
				CvRect r = new CvRect(cvGetSeqElem(faces, i));
				int x = r.x(), y = r.y(), w = r.width(), h = r.height();
				canvas.drawRect(x*scaleX, y*scaleY, (x+w)*scaleX, (y+h)*scaleY, paint);
			}
		}
	}

	public void setMySettings(int mySettings) {
		this.mySettings = mySettings;
	}

	public int getMySettings() {
		return mySettings;
	}
}