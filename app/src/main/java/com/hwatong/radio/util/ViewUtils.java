package com.hwatong.radio.util;
import com.hwatong.radio.constant.DtConstant;
import android.app.Activity;
import android.graphics.Point;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ViewUtils {
    
	/**设置文本**/
	public static void setTextViewEllipsize(TextView textView, String text, TextUtils.TruncateAt where) {
		
		String ellipsizeStr = (String) TextUtils.ellipsize(text, (TextPaint) textView.getPaint(),312 - 32, where);
		textView.setText(ellipsizeStr);
		textView.setEllipsize(where);
	}
	
   /**适配布局界面**/
   public static void adaptLayout(Activity activity,View convertView,int viewsId) {
		
		Display display = activity.getWindowManager().getDefaultDisplay();
		Point point = new Point();
		display.getSize(point);
		
		if (point.x == DtConstant.SCREEN_W && point.y == DtConstant.SCREEN_H) {
			
			return;
		}
		
		View view;	
		RelativeLayout.LayoutParams layoutParams;
		view = convertView.findViewById(viewsId);
		layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
		layoutParams.height = layoutParams.height * point.y / 600;
		view.setLayoutParams(layoutParams);
	}
   
    public static void adaptLayout(Activity activity,View convertView,int[] viewsIds) {
	   
	   Display display = activity.getWindowManager().getDefaultDisplay();
	   Point point = new Point();
	   display.getSize(point);
	   
	   if (point.x == DtConstant.SCREEN_W && point.y == DtConstant.SCREEN_H) {
			
			return;
	   }
	   
	   View view;	
	   RelativeLayout.LayoutParams layoutParams;
	   int viewIdsLength = viewsIds.length;
	   for (int i = 0; i < viewIdsLength; i++) {
		
		   view = convertView.findViewById(viewsIds[i]);
		   layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
		   layoutParams.height = layoutParams.height * point.y / 600;
		   view.setLayoutParams(layoutParams);
	   }
    }	
}
