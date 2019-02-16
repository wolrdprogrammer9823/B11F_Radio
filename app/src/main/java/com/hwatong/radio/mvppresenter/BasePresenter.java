package com.hwatong.radio.mvppresenter;
import java.lang.ref.WeakReference;

public class BasePresenter<T> {
    
	protected WeakReference<T> mRefs;
	
	public void onCreate(T t) {
		
		mRefs = new WeakReference<>(t);
	}
	
	public void onDestroy() {
		
		mRefs.clear();
	}
}
