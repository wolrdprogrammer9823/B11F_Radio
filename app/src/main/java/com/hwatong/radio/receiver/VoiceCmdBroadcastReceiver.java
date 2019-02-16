package com.hwatong.radio.receiver;
import com.hwatong.radio.constant.DtConstant;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class VoiceCmdBroadcastReceiver extends BroadcastReceiver{
	
	public VoiceCmdBroadcastReceiver(){}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		  String action = intent.getAction();
		  if (action.equals(DtConstant.VOICE_CMD_CLOSE_FMAM)) {
			  
			  if (fmamVoiceCmd != null) {
				  
				  fmamVoiceCmd.closeFmam();
			  }
			  
		  } else if (action.equals(DtConstant.VOICE_CMD_OPEN_FMAM)) {
			  
			  if (fmamVoiceCmd != null) {
				  
				  fmamVoiceCmd.openFmam();
			  }
		}
	}
    
	public void setFmamVoiceCmd(FmamVoiceCmd fmamVoiceCmd) {
		
		this.fmamVoiceCmd = fmamVoiceCmd;
	}
    
	public static VoiceCmdBroadcastReceiver getInstance() {
		
		return LazyHoldInstance.instance;
	}
	
	public interface FmamVoiceCmd {
		
		/**关闭收音机**/
		void closeFmam();
		/**打开收音机**/
		void openFmam();
	}
	
	/**内部类模式单例模式**/
	private static class LazyHoldInstance {
		
		private static final VoiceCmdBroadcastReceiver instance = new VoiceCmdBroadcastReceiver();
	}
	
	private FmamVoiceCmd fmamVoiceCmd;
}
