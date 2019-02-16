package com.hwatong.radio.util;
import com.hwatong.radio.ui.R;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class ToastUtil {
  
	public static void showChannelAlert(Context context,ViewGroup viewGroup,String name) {
			
		 View layout = inflateView(context,viewGroup,R.layout.custom_toast);
		 TextView toastText = (TextView) layout.findViewById(R.id.ifhaschannel);
		 Toast toast = new Toast(context);
		 toast.setGravity(Gravity.CENTER, 0, 0);
		 toast.setDuration(Toast.LENGTH_SHORT);
		 toast.setView(layout);
		 toastText.setText(name);
		 toast.show();
	}
	
	/**获取View方法**/
	public  static  View inflateView(Context context,ViewGroup viewGroup,int resId) {
		
		return LayoutInflater.from(context).inflate(resId, viewGroup,false);
	}
}
