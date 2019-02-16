package com.hwatong.radio.defineview;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

public class FontButton extends Button{

	public FontButton(Context context) {
		
		this(context,null);
		
	}

	public FontButton(Context context, AttributeSet attrs) {
		
		this(context, attrs,-1);
	}
	
	public FontButton(Context context, AttributeSet attrs, int defStyle) {
		
		super(context, attrs, defStyle);
		setTypeface(RadioApplication.getmInstance().getTypeFace());
	}
}
