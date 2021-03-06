package com.example.hello.inspirationboard;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by admin on 8/27/15.
 */
public class ViewEditPictureActivity extends Activity {


	private final static String TAG = "View Picture Activity";

	//long pictureDB_ID;

	DatabaseManager mDBManager;

	private Picture mPicture;

	private ImageView mPictureView;
	private EditText hashTagsEditText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_view_picture);

		long pictureDBID = getIntent().getLongExtra(InspirationList.PICTURE_DB_ID, -1);

		mDBManager = DatabaseManager.getInstance(this);   //TODO Working with same DatabaseManager but different context. is this ok?

		mPicture = mDBManager.getPicture(pictureDBID);

		displayPicture();

	}

	private void displayPicture() {

		if (mPicture == null) {
			Toast.makeText(getApplicationContext(), "No picture data found in database", Toast.LENGTH_LONG).show();
		}

		mPictureView = (ImageView) findViewById(R.id.picture_imageview);

		//TOOD load from URI

		Log.i(TAG, "About to load" + mPicture.getUriAsString());

		loadPicture();


		TextView dateCreated = (TextView) findViewById(R.id.picture_view_date_created);
		dateCreated.setText(mPicture.getDateCreatedAsString());

		TextView dateModified = (TextView) findViewById(R.id.picture_view_date_modifed);
		dateModified.setText(mPicture.getDateModifiedAsString());

		hashTagsEditText = (EditText) findViewById(R.id.picture_view_hashtags);
		hashTagsEditText.setText(mPicture.getHashtagsAsString());


		Button saveHashTags = (Button) findViewById(R.id.save_hashtags_button);
		saveHashTags.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				//Save hashtags and tell DB to update this Picture's entry.
				mPicture.setHashtags(hashTagsEditText.getText().toString());
				mDBManager.updatePicture(mPicture);
				setResult(RESULT_OK);
				finish();

			}
		});

		Button cancelSaveHashTags = (Button) findViewById(R.id.cancel_save_hashtags_button);
		cancelSaveHashTags.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();   //back to previous Activity.
			}
		});

	}


	@Override
	public void onBackPressed(){
		
		//Save hashtags and tell DB to update this Picture's entry.
		mPicture.setHashtags(hashTagsEditText.getText().toString());
		mDBManager.updatePicture(mPicture);
		setResult(RESULT_OK);
		finish();


	}

	private void loadPicture() {

		//Fetch picture by URI, scale to display.

		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;   //todo look these up see drawonapicture
		String photoPath = mPicture.getFileUri().getPath();
		BitmapFactory.decodeFile(photoPath);

		int pictureH = bmOptions.outHeight;
		int pictureW = bmOptions.outWidth;

		//scale the image


		//imageView size  //fixme this is a layout problem

		int imageViewWidth = 200; //= imageView.getWidth();   //fixme - read from UI
		int imageViewHeight = 200; //= imageView.getHeight();  //fixme

		int scaleFactor = Math.min(pictureH / imageViewHeight, pictureW/imageViewWidth);

		bmOptions.inJustDecodeBounds = false;

		bmOptions.inSampleSize = scaleFactor;  //TODO Is this the scale factor that is only powers of two?
		bmOptions.inPurgeable = true; //TODO deprecated is needed?

		Bitmap bitmap = BitmapFactory.decodeFile(photoPath);

		mPictureView.setImageBitmap(bitmap);

	}


}
