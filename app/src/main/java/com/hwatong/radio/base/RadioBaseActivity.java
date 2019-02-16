package com.hwatong.radio.base;
import com.hwatong.radio.mvppresenter.BasePresenter;
import android.app.Activity;
import android.os.Bundle;

public abstract class RadioBaseActivity<V,P extends BasePresenter<V>> extends Activity {
   
	protected P presenter;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		presenter = createPresenter();
		presenter.onCreate((V)this);
	}
	
	@Override
	protected void onDestroy() {
		
		super.onDestroy();
		presenter.onDestroy();
	}
	
	public abstract P createPresenter();
} 
