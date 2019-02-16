package com.hwatong.radio.base;
import java.lang.ref.WeakReference;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;

public abstract class RadioBaseHandler<T extends Activity> extends Handler {
   
	protected WeakReference<T> mActivityRefs;
	
	public RadioBaseHandler(T activity) {
		
		mActivityRefs = new WeakReference<>(activity);
	}
	
	@Override
	public void handleMessage(Message msg) {
		
		super.handleMessage(msg);
		
		T activity = mActivityRefs.get();
		if (activity == null) {
			
			return;
		}
		
		toHandleMessage(activity,msg);
	}
	
	public abstract void toHandleMessage(T activity,Message msg);
}
