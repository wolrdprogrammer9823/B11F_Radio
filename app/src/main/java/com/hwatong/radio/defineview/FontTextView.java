package com.hwatong.radio.defineview;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class FontTextView extends TextView {

	public FontTextView(Context context) {
		
		this(context,null);
	}

	public FontTextView(Context context, AttributeSet attrs) {
		
		this(context, attrs,-1);
	}
	
	public FontTextView(Context context, AttributeSet attrs, int defStyle) {
		
		super(context, attrs, defStyle);
		setTypeface(RadioApplication.getmInstance().getTypeFace());
	}
}
