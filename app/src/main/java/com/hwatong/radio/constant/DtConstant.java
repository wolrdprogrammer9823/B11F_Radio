package com.hwatong.radio.constant;
import android.net.Uri;

public interface DtConstant {
    
	/**目标屏幕宽高**/
	int SCREEN_W = 1024;
	int SCREEN_H = 600;
	int OHTER_SH = 900;
	
	/**收音机相关的常量**/
	int DEF_FREQUENCE_FM = 8750;
	int MIN_FREQUENCE_FM = 8750;
	int MAX_FREQUENCE_FM = 10800;
	int DEF_FREQUENCE_AM = 612;
	int MIN_FREQUENCE_AM = 531;
	int MAX_FREQUENCE_AM = 1602;
	
	/**频道数组**/
	int[] CURRENTCHANNEL = {DEF_FREQUENCE_FM,DEF_FREQUENCE_AM};
	
	/**场景相关的常量**/
	String CINEMA = "cinema";
	String CUSTOM = "custom";
	String STANDARD = "standard";
	String MUSICHALL = "musichall";
	
	/**内容提供者uri常量**/
	String CONTENT_URI_PARAM = "content://car_settings/content";
	Uri CONTENT_URI = Uri.parse(CONTENT_URI_PARAM);
	
	/**平衡器模式常量**/
	String EQUALIZER_MODE = "equalizer_mode";
	
	/**handler相关的常量**/
	int MSG_SETTINGS_CHANGED = 0x0001;
	int MSG_STATUS_CHANGED = 0x0102;
	int MSG_CHANNELLIST_CHANGED = 0x0103;
	int MSG_FAVORCHANNELLIST_CHANGED = 0x0104;
	int MSG_CHANNEL_CHANGED = 0x0105;
	int MSG_DISPLAY_CHANGED = 0x106;
	
	int MSG_TUNE_TO = 0x2001;
	int MSG_TUNE_DN = 0x2003;
	int MSG_TUNE_UP = 0x2004;
	int MSG_ENDSEEK = 0x2011;
	int MSG_DISMISSPOPWINDOW = 0x2012;
	int MSG_DELAYCLICK = 0x2013;
	
	/**广播相关的action**/
	String RADIO_SERVICE = "com.hwatong.radio.service";
	String MEDIA_START = "com.hwatong.media.START";
	/**语音命令关闭收音机**/
	String VOICE_CMD_CLOSE_FMAM = "com.hwatong.voice.CLOSE_FM";
	/**语音命令打开收音机**/
	String VOICE_CMD_OPEN_FMAM = "com.hwatong.OPEN_FM_VOICE";
	
	/**主界面切换action**/
	String AUX = "com.hwatong.auxui";
	String IPOD = "com.hwatong.ipod.ui";
	String USB = "com.hwatong.mediaplayer";
	String BTMUSIC = "com.hwatong.btmusic.ui";
	
	/**字体样式--微软雅黑*/
	String WEIRUANYAHEI = "weiruanyahei";
	/**粗细体**/
	String FONT_XI = "font_xi";
	String FONT_CHU = "font_chu";
	
	/**电台改变整型常量值**/
	int CURR_FREQ_CHANGED = 0x3020;
	/**处理seekbar相关的常量**/
	int SEEKBAR_CHANGE_INFO = 0x3030;
	/**计数值**/
	int COUNT_TIME = 10 * 1000;
	
	/**FM常规类型与收藏类型**/
	int NORMAL_FM = 0x3040;
	int COLLECTION_FM = 0x3050;
}
