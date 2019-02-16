package com.hwatong.radio.util;
import java.text.DecimalFormat;
import com.hwatong.radio.constant.DtConstant;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class StringUtil {
   
	public static String changeToString(int frequence) {
		
		double f = frequence / 100.0;
		DecimalFormat df = new DecimalFormat("#.0");
		return df.format(f);
	}
	
	public static String getCarSettingValue(Context context,String name) {

		String value = null;
		try {
			
			String[] select = new String[] {"value"};
			ContentResolver contentResolver = context.getContentResolver();
			Cursor cursor = contentResolver.query(DtConstant.CONTENT_URI, select,"name=?", new String[]{name}, null);

			if (cursor == null) {
				
				return null;
			}
			
			if (cursor.moveToFirst()) {
				
				value = cursor.getString(0);
			}
			
			cursor.close();
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		return value;
	}

	public static boolean setCarSettingValue(Context context,String name, String value) {

		try {
			
			String[] select = new String[] {"value"};
			ContentResolver contentResolver = context.getContentResolver();
			Cursor cursor = contentResolver.query(DtConstant.CONTENT_URI, select,"name=?", new String[]{name}, null);
			if (cursor != null && cursor.moveToFirst()) {
				
				if (cursor != null) {
					
					cursor.close();
				}
				
				ContentValues values = new ContentValues();
				values.put("value", value);
				contentResolver.update(DtConstant.CONTENT_URI, values, "name=?",new String[]{name});
			} else {
				
				if (cursor != null) {
					
					cursor.close();
				}
				
				ContentValues values = new ContentValues();
				values.put("name", name);
				values.put("value", value);
				contentResolver.insert(DtConstant.CONTENT_URI, values);
			}
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		
		return true;
	}
}
