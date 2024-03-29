package com.stegfy.utils.p2p;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

public class CommonMethods {
	
	public static String Tag = "Steganos";

	public static String getPath(Uri uri, Context context) {
		if (uri == null) {
			CommonMethods.e("", "URI is null!");
			return null;
		}
		// this will only work for images selected from gallery
		String[] projection = { MediaStore.Video.VideoColumns.DATA };
		Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
		CommonMethods.e("", "get path method->> after cursor init");
		if (cursor != null) {
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Video.VideoColumns.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		}
		CommonMethods.e("", "get path method->> after cursor");
		CommonMethods.e("", "get path method->> " + uri.getPath());
		return uri.getPath();
	}
	


	public static void DisplayToast(Context context, String msg){
    	Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
    public static void e(String tag, String msg){
    	Log.e(tag, msg);
    }
   
    

}
