package com.hwatong.radio.defineview;
import com.hwatong.radio.constant.DtConstant;
import android.app.Application;
import android.graphics.Typeface;

public class RadioApplication extends Application {
	
	private static RadioApplication mInstance;
	private Typeface typeFace;
	
	@Override
	public void onCreate() {
		
		super.onCreate();
		mInstance = (RadioApplication) getApplicationContext();
		String path = "fonts/" + DtConstant.FONT_XI +".ttf";
		typeFace = Typeface.createFromAsset(mInstance.getAssets(), path);
	}
    
	public static RadioApplication getmInstance() {

		return mInstance;
	}

	public Typeface getTypeFace() {
		
		return typeFace;
	}

	public void setTypeFace(Typeface typeFace) {
		
		this.typeFace = typeFace;
	}
}
