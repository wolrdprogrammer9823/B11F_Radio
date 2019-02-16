package com.hwatong.radio.callbacks;
import com.hwatong.radio.constant.DtConstant;
import android.os.Handler;
import android.os.Message;

public class RadioCallbackStub<T extends Handler> extends com.hwatong.radio.ICallback.Stub {
   
	private T t;
	
	public RadioCallbackStub(T t) {
		
		this.t = t;
	}
	
	@Override
	public void onDisplayChanged(int band, int freq) {
		
        Message m = Message.obtain(t, DtConstant.MSG_DISPLAY_CHANGED, band, freq);
        t.sendMessage(m);
        
	}

    @Override
	public void onStatusChanged() {
		
    	t.removeMessages(DtConstant.MSG_STATUS_CHANGED);
    	t.sendEmptyMessage(DtConstant.MSG_STATUS_CHANGED);
	}
	
    @Override
	public void onChannelListChanged(int band) {
	
        Message m = Message.obtain(t, DtConstant.MSG_CHANNELLIST_CHANGED, band, 0);
        t.sendMessage(m);
	}

    @Override
	public void onFavorChannelListChanged() {
		
    	t.removeMessages(DtConstant.MSG_FAVORCHANNELLIST_CHANGED);
    	t.sendEmptyMessage(DtConstant.MSG_FAVORCHANNELLIST_CHANGED);
	}

    @Override
	public void onChannelChanged() {
		
    	t.removeMessages(DtConstant.MSG_CHANNEL_CHANGED);
    	t.sendEmptyMessage(DtConstant.MSG_CHANNEL_CHANGED);
	}
}
