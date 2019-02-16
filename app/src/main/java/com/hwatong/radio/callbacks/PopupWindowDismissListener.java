package com.hwatong.radio.callbacks;
import android.widget.PopupWindow;

public abstract class PopupWindowDismissListener implements PopupWindow.OnDismissListener {
    
	private int windowType;
	
	public PopupWindowDismissListener(int windowType) {

       this.windowType = windowType;
	}
	
	@Override
	public void onDismiss() {
		
		whenDismiss(windowType);
	}
	
	public abstract void whenDismiss(int windowType);
}
