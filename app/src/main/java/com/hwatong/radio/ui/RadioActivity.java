package com.hwatong.radio.ui;
import java.util.ArrayList;
import java.util.List;
import com.hwatong.radio.Channel;
import com.hwatong.radio.IService;
import com.hwatong.radio.adapter.CollectionFmAdapter;
import com.hwatong.radio.adapter.DisplayFmListAdapter;
import com.hwatong.radio.base.RadioBaseActivity;
import com.hwatong.radio.base.RadioBaseHandler;
import com.hwatong.radio.callbacks.PopupWindowDismissListener;
import com.hwatong.radio.callbacks.RadioCallbackStub;
import com.hwatong.radio.constant.DtConstant;
import com.hwatong.radio.defineview.FancyCoverFlow;
import com.hwatong.radio.defineview.FontButton;
import com.hwatong.radio.defineview.FontTextView;
import com.hwatong.radio.entity.RadioFmItem;
import com.hwatong.radio.mvppresenter.RadioFmItemPresenter;
import com.hwatong.radio.mvpview.IRadioFmItemView;
import com.hwatong.radio.util.StringUtil;
import com.hwatong.radio.util.ToastUtil;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.ColorStateList;
import android.database.ContentObserver;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class RadioActivity extends RadioBaseActivity<IRadioFmItemView,RadioFmItemPresenter<IRadioFmItemView>> 
                            implements IRadioFmItemView,DisplayFmListAdapter.IFmItemClickListener{
    
	/**屏幕像素密度**/
	private float density;
	/**进度条滚动监听事件监听器**/
	private long mSeekBarTuneTime;
	/**当前电台值**/
	private String currFreq = "";
	
	/**fmcount计数器  来模拟特殊电台87.5MHz存台与已经存台的过程**/
	private int fmcount = 0;
	/**amcount计数器  来模拟特殊电1602KHz存台与已经存台的过程**/
	private int amcount = 0;
	/**seekbar阴影控制部分**/
	private int list_height;
	private int deleteChannelId = -1;
	private int currentBand = -1;
	private int currentItemPosition = -1;
	private static final int BEING_POPUPWINDOW = 0x1010;
	private static final int COLLECT_POPUPWINDOW = 0x1020;
	
	private boolean isSeek = false;
	/**标志位  用于记录收音机的开关状态**/
	private boolean isFmAmOn = true;
	/**标志位  记录当前电台是否发生了变化*/
	private boolean isChanged = true;
	/**标志位  是否允许进行下一次搜存**/
	private boolean isAllowed = true;
	private boolean isCollectionList = false;
	/**标志位  延时显示进度条**/
	private boolean delayDismissSeekbar = true;
	
	private RadioHandler mRadioHandler = new RadioHandler(this);
	private MixtureMsgHandler mixtureMsgHandler = new MixtureMsgHandler(this);
	private List<RadioFmItem> normalFmItemsList = new ArrayList<>();
	private List<RadioFmItem> collectFmItemsList = new ArrayList<>();
	private final Uri URI = Uri.withAppendedPath(DtConstant.CONTENT_URI, DtConstant.EQUALIZER_MODE);
	private RadioCallbackStub<RadioHandler> callbackStub = new RadioCallbackStub<>(mRadioHandler);
	
	/**View对象**/
	View popupView;
	
	/**显示提示弹框**/
	private Dialog initChannDialog;
	private Dialog deleteItemDialog;
	private PopupWindow beingpopupwindow;
	private PopupWindow collectpopupwindow;
		
	/**Button对象**/
	private Button ensureDel;
	private Button cancleDel;
	private FontButton btn_fm;
	private Button btn_eq_film;
	private Button btn_eq_music;
	private Button fmamBtnOnOff;
	private Button btn1_popList;
	private Button btn2_popList;
	private Button btn_eq_normal;
	
	/**TextView对象**/
	private TextView txt_search;
	private FontTextView txt_fm;
	private FontTextView txt_mhz;
	private TextView txt_seekbar;
	private TextView txNoChannel;
	private TextView txNoSuchChannel;
	private FontTextView txt_curchannel;
    
	/**RelativeLayout对象**/
	private RelativeLayout seekBarBg;
	private RelativeLayout btn_singlesave; 
	private RelativeLayout btn_mainbar_aux;
	private RelativeLayout btn_mainbar_usb;
	private RelativeLayout btn_mainbar_ipod;
    private RelativeLayout btn_mainbar_radio;
	private RelativeLayout btn_mainbar_btmusic;
	
	/**LinearLayout对象**/
	private LinearLayout parentView;
	
	/**ImageView对象**/
	private ImageView iv_pre;
	private ImageView iv_next;
	private ImageView iv_lists;
	private ImageView iv_shadow;
	private ImageView iv_shadow2;
	private ImageView iv_shadow3;
	private ImageView ivHighlight;
	private ImageView iv_animation;
	private ImageView iv_lists_back;
	private ImageView ivinit_animation;
	
	private SeekBar seekBar;
	private ListView collectionListView;
	private FancyCoverFlow fancyCoverFlow;
	
	private CollectionFmAdapter collectFmItemAdapter;
	private DisplayFmListAdapter normalFmGalleryAdapter;
    
	private LinearInterpolator lin;
	private Animation music_cd_rotate;
    
	private IService service;
	private CurrFreqThread currThread;
	private MyContentObserver contentObserver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		/**全屏模式转换为非全屏模式时 xml布局文件内容不改变 只加载状态栏**/
		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
		
		setContentView(R.layout.main);
		/**适配布局页面**/
		adapterLayout();
		/**调用相关的初始化方法**/
		initView();
		initDialog();
		initPopupWindows();
		initBeingProgrePopupWindow();
		
        /**给View对象设置事件监听**/
		setListener();
        /**注册内容观察者**/
		contentObserver = new MyContentObserver(mRadioHandler);
		getContentResolver().registerContentObserver(URI, true, contentObserver);
        /**通过设置界面中的相关设置改变相关内容**/
		applySettings();
		
        /**启动服务**/
		Intent radioServiceIntent = new Intent(DtConstant.RADIO_SERVICE);
		startService(radioServiceIntent);
		/**广播接收者监控媒体启动**/
    	IntentFilter filter = new IntentFilter();
        filter.addAction(DtConstant.MEDIA_START);
        registerReceiver(mReceiver, filter);
	}

	@Override
	protected void onResume() {
		
		super.onResume();
		/**绑定服务**/
		bindService(new Intent(DtConstant.RADIO_SERVICE), serviceConnection, 0);
		/**发送广播**/
		sendBroadcast(new Intent(DtConstant.MEDIA_START).putExtra("tag", "FM"));
	}
	
	@Override
	protected void onStop() {
		
		super.onStop();
		
		if (service != null) {
			try {
				
				service.unregisterCallback(callbackStub);
			} catch (RemoteException e) {
				
				e.printStackTrace();
			}
		}
		
		service = null;
		unbindService(serviceConnection);
		
		mRadioHandler.removeCallbacksAndMessages(null);
		mixtureMsgHandler.removeCallbacksAndMessages(null);
		
		if (isCollectionList && collectpopupwindow.isShowing()) {
			
			collectpopupwindow.dismiss();
		}
		
		isCollectionList = false;
		
		if (isSeek) {
			
			isSeek = false;
			seekBarBg.setVisibility(View.GONE);
			iv_shadow.setVisibility(View.GONE);
			iv_shadow2.setVisibility(View.GONE);
			iv_pre.setImageResource(R.drawable.btn_last);
			iv_next.setImageResource(R.drawable.btn_next);
			setTextViewDrawable(R.drawable.btn_seek_radio, txt_seekbar);
			txt_seekbar.setBackgroundResource(R.drawable.btn_seek_radio);
			txt_seekbar.setTextColor(getResources().getColor(R.color.text));
		}
		
		if (initChannDialog != null && initChannDialog.isShowing()) {
			
			initChannDialog.dismiss();
		}
		
		if (deleteItemDialog != null && deleteItemDialog.isShowing()) {
			
			deleteItemDialog.dismiss();
		}
		
		if (beingpopupwindow != null && beingpopupwindow.isShowing()) {
			
			beingpopupwindow.dismiss();
		}
	}

    @Override
    protected void onDestroy() {
    	
    	super.onDestroy();
    	/**注销广播接收者**/
    	unregisterReceiver(mReceiver);
    }
    
    @Override
	public RadioFmItemPresenter<IRadioFmItemView> createPresenter() {
		
		return new RadioFmItemPresenter<>();
	}
    
    @Override
	public void loadFmItemDatas(int loadType, List<RadioFmItem> fmItemsList) {
    	
    	int currentBand = -1;
    	try {
    		
			currentBand = service.getCurrentBand();
		} catch (RemoteException e) {
			
			e.printStackTrace();
		}
    	
		switch (loadType) {
		  case DtConstant.NORMAL_FM:{
			
			  normalFmItemsList = fmItemsList;
			  normalFmGalleryAdapter = new DisplayFmListAdapter(this,currentBand,normalFmItemsList);
			  fancyCoverFlow.setAdapter(normalFmGalleryAdapter);
			  normalFmGalleryAdapter.setFmItemClickListener(this);
			  setNormalFmItemThings();
			break;
		  }
		  case DtConstant.COLLECTION_FM:{
			  
			  collectFmItemsList = fmItemsList;
			  collectFmItemAdapter = new CollectionFmAdapter(this,currentBand,collectFmItemsList);
			  collectionListView.setAdapter(collectFmItemAdapter);
			  setCollectFmItemThings();
			break;
		  }
		  default:
			break;
		}
	}
    
    @Override
	public void onFmItemClick(int position, ImageView imageView) {
		
    	currentItemPosition = position;
		imageView.setOnClickListener(sessionBtnClickListener);
	}
    
    /**初始化View**/
	private void initView() {
	    
		txt_fm = (FontTextView) findViewById(R.id.text_fm);
		txt_mhz = (FontTextView) findViewById(R.id.text_mhz);
		txNoChannel = (TextView) findViewById(R.id.no_chennel);
		txt_seekbar = (TextView) findViewById(R.id.text_seek);
		txt_search = (TextView) findViewById(R.id.text_search);
		txt_curchannel = (FontTextView) findViewById(R.id.text_curfreq);
		currFreq = txt_curchannel.getText().toString().trim();
		
		iv_pre = (ImageView) findViewById(R.id.btn_pre);
		iv_next = (ImageView) findViewById(R.id.btn_next);
		iv_lists = (ImageView) findViewById(R.id.btn_list);
		iv_shadow = (ImageView) findViewById(R.id.bg_shadow);
		iv_shadow2 = (ImageView) findViewById(R.id.bg_shadow2);
		iv_shadow3 = (ImageView) findViewById(R.id.bg_shadow3);
		ivHighlight = (ImageView) findViewById(R.id.bg_gallerybg);
		
		seekBarBg = (RelativeLayout) findViewById(R.id.seekbarpw_layout);
		btn_mainbar_usb = (RelativeLayout) findViewById(R.id.btnbar_usb);
		btn_mainbar_aux = (RelativeLayout) findViewById(R.id.btnbar_aux);
		btn_mainbar_ipod = (RelativeLayout) findViewById(R.id.btnbar_ipod);
		btn_mainbar_radio = (RelativeLayout) findViewById(R.id.btnbar_radio);
		btn_singlesave  = (RelativeLayout)findViewById(R.id.curchannel_layout);
		btn_mainbar_btmusic = (RelativeLayout) findViewById(R.id.btnbar_music);
		
		btn_fm = (FontButton) findViewById(R.id.btn_fmam);
		btn_eq_film = (Button) findViewById(R.id.btn_eq_film);
		btn_eq_music = (Button) findViewById(R.id.btn_eq_music);
		btn_eq_normal = (Button) findViewById(R.id.btn_eq_normal);
		fmamBtnOnOff = (Button) findViewById(R.id.fmam_btn_onoff);
        
		seekBar = (SeekBar) findViewById(R.id.seekbarpw_seekBar);
		seekBar.setMax(DtConstant.MAX_FREQUENCE_FM - DtConstant.MIN_FREQUENCE_FM);
		seekBar.setProgress(DtConstant.DEF_FREQUENCE_FM - DtConstant.MIN_FREQUENCE_FM);
		
		parentView = (LinearLayout) findViewById(R.id.mainlayout);
		fancyCoverFlow = (FancyCoverFlow) findViewById(R.id.fancyCoverFlow);
       
		currThread = new CurrFreqThread();
		density = getResources().getDisplayMetrics().density;
	}
    
	/**初始化删除收藏列表中的频道对话框**/
	private void initDialog() {

		deleteItemDialog = new Dialog(this, R.style.delete_dialog);
		View dialogView = ToastUtil.inflateView(this,parentView,R.layout.delete_item_dialog);
		
		int width = 525;
		int height = 225;
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width,height);
		deleteItemDialog.setContentView(dialogView,params);
		deleteItemDialog.setCanceledOnTouchOutside(false);
		
		ensureDel = (Button) dialogView.findViewById(R.id.delete_sure);
		cancleDel = (Button) dialogView.findViewById(R.id.delete_cancle);
		ensureDel.setOnClickListener(sessionBtnClickListener);
		cancleDel.setOnClickListener(sessionBtnClickListener);
	}
	
	/**收藏收音机频道列表相关的popupWindow**/
	@SuppressWarnings("deprecation")
	private void initPopupWindows() {
		
		if (collectpopupwindow == null) {
			
			View convertview = ToastUtil.inflateView(this,parentView,R.layout.listpopupwindow);
			/**调用适配PopupWindow方法**/
			adapterPopLayout(convertview);
			collectionListView = (ListView) convertview.findViewById(R.id.listView_coll);
			txNoSuchChannel = (TextView) convertview.findViewById(R.id.listdailog_nochennel);
			iv_lists_back = (ImageView) convertview.findViewById(R.id.listpw_btn_dismiss);
			iv_lists_back.setOnClickListener(mViewClickLisntener);

			btn1_popList = (Button) convertview.findViewById(R.id.listView_btn1);
			btn2_popList = (Button) convertview.findViewById(R.id.listView_btn2);
			btn1_popList.setOnClickListener(sessionBtnClickListener);
			btn2_popList.setOnClickListener(sessionBtnClickListener);

			collectpopupwindow = new PopupWindow(convertview,(int) (410 * density), 
					                             (int) (list_height * density), true);
			collectpopupwindow.setFocusable(true);
			collectpopupwindow.setBackgroundDrawable(new BitmapDrawable());
			collectpopupwindow.setOutsideTouchable(true);
			collectpopupwindow.setOnDismissListener(new RadioPopupWindowListener(COLLECT_POPUPWINDOW));
			collectpopupwindow.setAnimationStyle(R.style.popupwindow_anim_style);
			collectpopupwindow.update();
		}
	}
    
	/**搜索收音机频道相关PopupWindow**/
	@SuppressWarnings("deprecation")
	private void initBeingProgrePopupWindow() {

		if (beingpopupwindow != null) {
			
			iv_animation.startAnimation(music_cd_rotate);
			return;
		}

		View convertview = ToastUtil.inflateView(this, parentView,R.layout.being_progress);
		iv_animation = (ImageView) convertview.findViewById(R.id.iv_animation1);
		popupView = convertview.findViewById(R.id.popupwindow);
		popupView.setOnClickListener(sessionBtnClickListener);
        
		int width = ViewGroup.LayoutParams.MATCH_PARENT;
		int height = ViewGroup.LayoutParams.MATCH_PARENT;
		beingpopupwindow = new PopupWindow(convertview,width,height, true);
		beingpopupwindow.setFocusable(true);

		music_cd_rotate = AnimationUtils.loadAnimation(RadioActivity.this,R.drawable.rotate);
		lin = new LinearInterpolator();
		music_cd_rotate.setInterpolator(lin);

		beingpopupwindow.setBackgroundDrawable(new BitmapDrawable());
		beingpopupwindow.setOnDismissListener(new RadioPopupWindowListener(BEING_POPUPWINDOW));
	}
	
	/**设置事件**/
	private void setListener() {
		
		btn_fm.setOnClickListener(mViewClickLisntener);
		iv_pre.setOnClickListener(mViewClickLisntener);
		iv_next.setOnClickListener(mViewClickLisntener);
		iv_lists.setOnClickListener(mViewClickLisntener);
		
		txt_search.setOnClickListener(mViewClickLisntener);
		txt_seekbar.setOnClickListener(mViewClickLisntener);
		
		iv_pre.setOnTouchListener(myViewTouchListener);
		iv_next.setOnTouchListener(myViewTouchListener);
		seekBarBg.setOnTouchListener(myViewTouchListener);
		txt_search.setOnTouchListener(myViewTouchListener);
		txt_seekbar.setOnTouchListener(myViewTouchListener);
		collectionListView.setOnTouchListener(myViewTouchListener);
		
		btn_mainbar_usb.setOnClickListener(mMainBarClickListener);
		btn_mainbar_aux.setOnClickListener(mMainBarClickListener);
		btn_mainbar_ipod.setOnClickListener(mMainBarClickListener);
		btn_mainbar_radio.setOnClickListener(mMainBarClickListener);
		btn_mainbar_btmusic.setOnClickListener(mMainBarClickListener);
				
		btn_eq_film.setOnClickListener(mEQListener);
		btn_eq_music.setOnClickListener(mEQListener);
		btn_eq_normal.setOnClickListener(mEQListener);
		
		iv_pre.setOnLongClickListener(myViewLongClickListener);
		iv_next.setOnLongClickListener(myViewLongClickListener);
		btn_singlesave.setOnLongClickListener(myViewLongClickListener);
		
		iv_shadow.setOnClickListener(sessionBtnClickListener);
		iv_shadow2.setOnClickListener(sessionBtnClickListener);

		fancyCoverFlow.setOnItemSelectedListener(galleryItemSelectedListener);
		collectionListView.setOnItemClickListener(listViewItemClickListener);
		collectionListView.setOnItemLongClickListener(listViewItemLongClickListener);

		seekBar.setOnSeekBarChangeListener(mSeekBarListener);
		fmamBtnOnOff.setOnClickListener(fmAmBtnOnOfClickLisntener);

	}
    
	/**屏幕适配**/
	private void adapterLayout() {
			
		final Display display = getWindowManager().getDefaultDisplay();
		final Point point = new Point();

		display.getSize(point);
		if (point.x == DtConstant.SCREEN_W && point.y == DtConstant.SCREEN_H) {
				
			list_height = 440;		
			return;
		}
			
		View view;
		RelativeLayout.LayoutParams relativeParams;
		LinearLayout.LayoutParams linearParams;
		list_height = 390;
			
		view = findViewById(R.id.tv_radio);
		relativeParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
		((TextView)findViewById(R.id.tv_radio)).setTextSize(16 * point.y / DtConstant.SCREEN_H);
		relativeParams.topMargin = relativeParams.topMargin * point.y / DtConstant.SCREEN_H;
		view.setLayoutParams(relativeParams);
			
		view = findViewById(R.id.tv_btmusic);
		relativeParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
		((TextView)findViewById(R.id.tv_btmusic)).setTextSize(16 * point.y / DtConstant.SCREEN_H);
		relativeParams.topMargin = relativeParams.topMargin * point.y / DtConstant.SCREEN_H;
		view.setLayoutParams(relativeParams);
			
		view = findViewById(R.id.tv_ipod);
		relativeParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
		((TextView)findViewById(R.id.tv_ipod)).setTextSize(16 * point.y / DtConstant.SCREEN_H);
		relativeParams.topMargin = relativeParams.topMargin * point.y / DtConstant.SCREEN_H;
		view.setLayoutParams(relativeParams);
			
		view = findViewById(R.id.tv_usbmusic);
		relativeParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
		((TextView)findViewById(R.id.tv_usbmusic)).setTextSize(16 * point.y / DtConstant.SCREEN_H);
		relativeParams.topMargin = relativeParams.topMargin * point.y / DtConstant.SCREEN_H;
		view.setLayoutParams(relativeParams);
			
		view = findViewById(R.id.layout_eq);
		relativeParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
		relativeParams.height = relativeParams.height * point.y / DtConstant.SCREEN_H;
		relativeParams.topMargin = relativeParams.topMargin * point.y / DtConstant.SCREEN_H;
		view.setLayoutParams(relativeParams);

		view = findViewById(R.id.ll_fmamonoff);
		relativeParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
		relativeParams.topMargin = relativeParams.topMargin * point.y / DtConstant.SCREEN_H;
		relativeParams.rightMargin = relativeParams.rightMargin * point.y / DtConstant.SCREEN_H;
		view.setLayoutParams(relativeParams);
			
		view = findViewById(R.id.fmam_btn_onoff);
		linearParams = (LinearLayout.LayoutParams) view.getLayoutParams();
		linearParams.leftMargin = linearParams.leftMargin * point.y / DtConstant.SCREEN_H;
		view.setLayoutParams(linearParams);
				
		view = findViewById(R.id.btn_list);
		relativeParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
		relativeParams.bottomMargin = relativeParams.bottomMargin * point.y / DtConstant.SCREEN_H;
		view.setLayoutParams(relativeParams);
	 }
	 
	 /**屏幕适配**/
	 private void adapterPopLayout(View convertview) {
			
		Display display = getWindowManager().getDefaultDisplay();
		Point point = new Point();

		display.getSize(point);
		if (point.x == DtConstant.SCREEN_W && point.y == DtConstant.SCREEN_H) {
				
			return;
		}
			
		View view;
		LinearLayout.LayoutParams linerParams;
		RelativeLayout.LayoutParams relativeParams;
			
		view = convertview.findViewById(R.id.listpw_btn_dismiss);
		relativeParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
		relativeParams.bottomMargin = relativeParams.bottomMargin * point.y / DtConstant.SCREEN_H;
		view.setLayoutParams(relativeParams);
			
	    /**左边主界面标签栏**/
		view = findViewById(R.id.main_btnbar);
		linerParams = (LinearLayout.LayoutParams)view.getLayoutParams();
		linerParams.width = linerParams.width * point.x / DtConstant.SCREEN_W;
		view.setLayoutParams(linerParams);
		    
		/**右边主界面**/
		view = findViewById(R.id.main_include);
		linerParams = (LinearLayout.LayoutParams)view.getLayoutParams();
		linerParams.width = linerParams.width * point.x / DtConstant.SCREEN_W;
		view.setLayoutParams(linerParams);
		    
		view = findViewById(R.id.fancyCoverFlow_layout);
		relativeParams = (RelativeLayout.LayoutParams)view.getLayoutParams();
		relativeParams.width = relativeParams.width * point.x / DtConstant.SCREEN_W;
		relativeParams.height = relativeParams.height * point.y / DtConstant.SCREEN_H;
		relativeParams.topMargin = relativeParams.topMargin * point.y / DtConstant.SCREEN_H;
		view.setLayoutParams(relativeParams);
		    
		view = findViewById(R.id.fancyCoverFlow);
		linerParams = (LinearLayout.LayoutParams)view.getLayoutParams();
		linerParams.width = linerParams.width * point.x / DtConstant.SCREEN_W;
		view.setLayoutParams(linerParams);
		    
		view = findViewById(R.id.bg_gallerybg);
		relativeParams = (RelativeLayout.LayoutParams)view.getLayoutParams();
		relativeParams.width = relativeParams.width * point.x / DtConstant.SCREEN_W;
		relativeParams.height = relativeParams.height * point.y / DtConstant.SCREEN_H;
		relativeParams.topMargin = relativeParams.topMargin * point.y / DtConstant.SCREEN_H;
		view.setLayoutParams(relativeParams);
		    
		view = findViewById(R.id.bottombar_layout);
		relativeParams = (RelativeLayout.LayoutParams)view.getLayoutParams();
		relativeParams.width = relativeParams.width * point.x / DtConstant.SCREEN_W;
		view.setLayoutParams(relativeParams);
		    
		view = findViewById(R.id.curchannel_layout);
		relativeParams = (RelativeLayout.LayoutParams)view.getLayoutParams();
		relativeParams.width = relativeParams.width * point.x / DtConstant.SCREEN_W;
		view.setLayoutParams(relativeParams);
		    
		view = findViewById(R.id.text_curfreq);
		relativeParams = (RelativeLayout.LayoutParams)view.getLayoutParams();
		relativeParams.width = relativeParams.width * point.x / DtConstant.SCREEN_W;
		view.setLayoutParams(relativeParams);
		    
	    /**收音机**/
	    view = findViewById(R.id.iv_radio);
	    RelativeLayout.LayoutParams tabsLayoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
			
		TextView tvRadio = (TextView) findViewById(R.id.tv_radio);
		tvRadio.setTextSize(16 * point.y / DtConstant.SCREEN_H);
			
		tabsLayoutParams.width = tabsLayoutParams.width * point.y / DtConstant.OHTER_SH;
		tabsLayoutParams.height = tabsLayoutParams.height * point.y / DtConstant.OHTER_SH;
		tabsLayoutParams.topMargin = tabsLayoutParams.topMargin * point.y / DtConstant.OHTER_SH;
		view.setLayoutParams(tabsLayoutParams);
			
		/**蓝牙音乐**/
		view = findViewById(R.id.iv_btmusic);
		tabsLayoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
			
		TextView tvBtmusic = (TextView) findViewById(R.id.tv_btmusic);
		tvBtmusic.setTextSize(16 * point.y / DtConstant.SCREEN_H);
			
		tabsLayoutParams.width = tabsLayoutParams.width * point.y / DtConstant.OHTER_SH;
		tabsLayoutParams.height = tabsLayoutParams.height * point.y / DtConstant.OHTER_SH;
		tabsLayoutParams.topMargin = tabsLayoutParams.topMargin * point.y / DtConstant.OHTER_SH;
		view.setLayoutParams(tabsLayoutParams);
			
		/**ipod音乐**/
		view = findViewById(R.id.iv_ipod);
		tabsLayoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
			
		TextView tvIpod = (TextView) findViewById(R.id.tv_ipod);
		tvIpod.setTextSize(16 * point.y / DtConstant.SCREEN_H);
			
		tabsLayoutParams.width = tabsLayoutParams.width * point.y / DtConstant.OHTER_SH;
		tabsLayoutParams.height = tabsLayoutParams.height * point.y / DtConstant.OHTER_SH;
		tabsLayoutParams.topMargin = tabsLayoutParams.topMargin * point.y / DtConstant.OHTER_SH;
		view.setLayoutParams(tabsLayoutParams);
			
		/**aux音乐**/
		view = findViewById(R.id.iv_aux);
		tabsLayoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
			
		TextView tvAux = (TextView) findViewById(R.id.tv_aux);
		tvAux.setTextSize(16 * point.y / DtConstant.SCREEN_H);
			
		tabsLayoutParams.width = tabsLayoutParams.width * point.y / DtConstant.OHTER_SH;
		tabsLayoutParams.height = tabsLayoutParams.height * point.y / DtConstant.OHTER_SH;
		tabsLayoutParams.topMargin = tabsLayoutParams.topMargin * point.y / DtConstant.OHTER_SH;
		view.setLayoutParams(tabsLayoutParams);
			
		/**usb音乐**/
		view = findViewById(R.id.iv_usbmusic);
		tabsLayoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
			
		TextView tvUsbMusic = (TextView) findViewById(R.id.tv_usbmusic);
		tvUsbMusic.setTextSize(16 * point.y / DtConstant.SCREEN_H);
			
		tabsLayoutParams.width = tabsLayoutParams.width * point.y / DtConstant.OHTER_SH;
		tabsLayoutParams.height = tabsLayoutParams.height * point.y / DtConstant.OHTER_SH;
		tabsLayoutParams.topMargin = tabsLayoutParams.topMargin * point.y / DtConstant.OHTER_SH;
		view.setLayoutParams(tabsLayoutParams);	
	}
	
	/**收音机状态改变时回调方法**/
    private void handleStatusChanged() {
		
		if (service == null) {
			
			return;
		}
		
		int status = -1;
		int onoff = -1;
		try {
			status = service.getStatus()[0];
			/**收音机的开与关**/
			onoff = service.getStatus()[1];
		} catch (RemoteException e) {
			
			e.printStackTrace();
		}
		
		if (onoff == 0) {
			
			fmamBtnOnOff.setBackgroundResource(R.drawable.fmam_btn_off);
		} else {
			
			fmamBtnOnOff.setBackgroundResource(R.drawable.fmam_btn_on);
		}
        
		if (status == -1) {
			if (initChannDialog == null) {
				
				initChannDialog = new Dialog(this, R.style.init_chann_dialog);
				View toastView = ToastUtil.inflateView(this, parentView,R.layout.init_radiochann_dialog);
				ivinit_animation = (ImageView) toastView.findViewById(R.id.animation2);
				initChannDialog.setContentView(toastView);
				initChannDialog.setCanceledOnTouchOutside(false);
				initChannDialog.setOnKeyListener(new OnKeyListener() {
					@Override
					public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
						
						if (keyCode == KeyEvent.KEYCODE_BACK) {
							
							return true;
						}
						return false;
					}
				});

				initChannDialog.setOnDismissListener(new OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialog) {
						
						ivinit_animation.clearAnimation();
					}
				});
			}
         } else if (status == 0) {
    
        	if (initChannDialog != null && initChannDialog.isShowing()) {
        		
        		initChannDialog.dismiss();
        	}
        	
        	if (beingpopupwindow != null && beingpopupwindow.isShowing()) {
        		
        		beingpopupwindow.dismiss();
        	}
        } 
	}
	
	/**当前收音机频道发生改变**/
	private void handleCurrentChannelChanged() {

		if (service != null) {
			
		    try {
				
				currentBand = service.getCurrentBand();
				DtConstant.CURRENTCHANNEL[0] = service.getCurrentChannel(0);
				DtConstant.CURRENTCHANNEL[1] = service.getCurrentChannel(1);
			} catch (RemoteException e) {
				
				e.printStackTrace();
			}
            
			presenter.setIService(service)
			         .setLoadType(DtConstant.NORMAL_FM).fetchFmItems();
			
			if (currentBand == 0) {
				
				btn_fm.setText("FM");
			} else if (currentBand == 1) {
				
				btn_fm.setText("AM");
			}
			
			presenter.setIService(service)
					  .setLoadType(DtConstant.COLLECTION_FM).fetchFmItems();
			
			if (currentBand != -1 && DtConstant.CURRENTCHANNEL[currentBand] != -1) {
				
				updateCurrentDisplay(currentBand, DtConstant.CURRENTCHANNEL[currentBand]);
				updateSeekBar(currentBand, DtConstant.CURRENTCHANNEL[currentBand]);
			}
		}
	}
    
	/**搜存列表收音机频道发生改变时回调方法**/
	private void handleChannelListChanged() {
        
		presenter.setIService(service)
		         .setLoadType(DtConstant.NORMAL_FM).fetchFmItems();
	}
	
	/**收藏列表频道数量发生改变时回调**/
	private void handleFavorChanged() {
 
		if (normalFmGalleryAdapter != null) {
			
			notifyFavorChanged();
		}
		
		if (collectFmItemAdapter != null) {
			
			presenter.setIService(service)
					  .setLoadType(DtConstant.COLLECTION_FM).fetchFmItems();
		}
	}
	
	/**设置电台列表相关的事项**/
	private void setNormalFmItemThings() {
		
		if (normalFmGalleryAdapter.getCount() == 0) {
			
			 ivHighlight.setVisibility(View.GONE);
			 txNoChannel.setVisibility(View.VISIBLE);
			 fancyCoverFlow.setVisibility(View.INVISIBLE);
			  
			 /**未搜索到电台的时候  暂停播放电台  把当前电台显示值设置为0.0**/
			 try {
				
			    if (service != null) {
					
					service.playPause();
				}
			 } catch (RemoteException e) {
				
				e.printStackTrace();
			 }
			  
			 txt_curchannel.setText("0.0");
		 } else {
		 	
			 setSelectorItem();
			 ivHighlight.setVisibility(View.VISIBLE);
			 txNoChannel.setVisibility(View.INVISIBLE);
			 fancyCoverFlow.setVisibility(View.VISIBLE);
		 }
	}
	
	/**设置收藏的Fm相关的操作**/
	private void setCollectFmItemThings() {
		
		if (collectFmItemAdapter.getCount() == 0) {
			
			txNoSuchChannel.setVisibility(View.VISIBLE);
			collectionListView.setVisibility(View.INVISIBLE);
		} else {
			
			txNoSuchChannel.setVisibility(View.INVISIBLE);
			collectionListView.setVisibility(View.VISIBLE);
		}
	}
	
	/**点击播放收藏列表中的电台**/
	public void moveTo(ViewGroup parent,int position) {
	   if (service != null) {
		
		  int collectFmItemsCount = collectFmItemsList.size();
		  boolean frequenceInRange = false;
		  if (collectFmItemsCount != 0) {
			 try {
				
				int collectFrequence = collectFmItemsList.get(position).frequence;
				for (RadioFmItem radioFmItem : normalFmItemsList) {
					
					int normalFrequence = radioFmItem.frequence;
					if (normalFrequence == collectFrequence) {
						
						frequenceInRange = true;
						collectFrequence = normalFrequence;
					}
				}
				
				if (frequenceInRange) {
					
					service.tuneTo(collectFrequence, false);
				} else {
					
					String tip = getString(R.string.not_in_range);
					ToastUtil.showChannelAlert(this,parent,tip);
				}
			  } catch (RemoteException e) {
				
				e.printStackTrace();
			 }
		  }
	   }
    }
	
	/**更新进度条**/
    private void updateSeekBar(int currentBand, int currentFrequence) {
		
		mRadioHandler.removeMessages(DtConstant.SEEKBAR_CHANGE_INFO);
        long delayTime = mSeekBarTuneTime + 500 - SystemClock.uptimeMillis();
		if (delayTime > 0) {
			
			Message change_info = Message.obtain(mRadioHandler,DtConstant.SEEKBAR_CHANGE_INFO,
					                             currentBand,currentFrequence);
			mRadioHandler.sendMessageDelayed(change_info, delayTime);
			return;
		}
        
		if (currentBand == 0) {
			
			seekBar.setMax((DtConstant.MAX_FREQUENCE_FM - DtConstant.MIN_FREQUENCE_FM) / 10);
			seekBar.setProgress((currentFrequence - DtConstant.MIN_FREQUENCE_FM) / 10);
			seekBarBg.setBackgroundResource(R.drawable.bg_seekbarbg_radio_fm);
		} else if (currentBand == 1) {
			
			seekBar.setMax((DtConstant.MAX_FREQUENCE_AM - DtConstant.MIN_FREQUENCE_AM) / 9);
			seekBar.setProgress((currentFrequence - DtConstant.MIN_FREQUENCE_AM) / 9);
			seekBarBg.setBackgroundResource(R.drawable.bg_seekbarbg_radio_am);
		}
	}
    
    /**更新当前显示电台**/
	private void updateCurrentDisplay(int currentBand, int currentFrequence) {
		
		if (currentBand == 0) {
			
			txt_fm.setText("FM");
			txt_mhz.setText("MHz");
			txt_curchannel.setText(StringUtil.changeToString(currentFrequence));
		} else if (currentBand == 1) {
			
			txt_fm.setText("AM");
			txt_mhz.setText("KHz");
			txt_curchannel.setText("" + currentFrequence);
		}
	}
    
	/**通知收藏的电台发生改变**/
	private void notifyFavorChanged() {
	   if (service != null) {
		  try {
			
			 List<Channel> favorChannels = service.getFavorChannelList();
             int normalFmItemsCount = normalFmItemsList.size();
			 for (int i = 0; i < normalFmItemsCount; i++) {
				 
				 RadioFmItem radioFmItem = normalFmItemsList.get(i);
				 radioFmItem.favor = false;
				 int favorChannelsCount = favorChannels.size();
				 for (int j = 0; j < favorChannelsCount; j++) {
					 
					Channel favorChannel = favorChannels.get(j);
					if (radioFmItem.frequence == favorChannel.frequence) {
						
						  radioFmItem.favor = true;
						break;
					}
				 }
			 }
		 } catch (RemoteException e) {
		 	
			e.printStackTrace();
		}
		  
		normalFmGalleryAdapter.notifyDataSetChanged();
	 }
   }

   /**通过电台频率获取所在position**/
   public int getPositionByChannel(int frequence) {
	
	  int normalFmItemCount = normalFmItemsList.size();
	  for (int i = 0; i < normalFmItemCount; i++) {
		if (normalFmItemsList.get(i).frequence == frequence) {
			
			return Integer.MAX_VALUE / normalFmItemCount / 2 * normalFmItemCount + i;
		}
	  }

	  return -1;
   }
	
    /**设置当前选中的(Gallery)item**/
	private boolean setSelectorItem() {
		if (currentBand == -1) {
			
			return false;
		}
			
		int position = getPositionByChannel(DtConstant.CURRENTCHANNEL[currentBand]);

		if (position > -1) {
			
			fancyCoverFlow.setSelection(position);
			return false;
		}

		return true;
	}
	
	/**获取intent方法**/
	private Intent getIntent(String pkg) {
		
		return getPackageManager().getLaunchIntentForPackage(pkg);
	}
	
	/**设置EQ**/
    private void setEQ(String value) {
	  /**调用设置btn背景和txt颜色方法*/
	  setBtnBgTxtColor();
	  if (value != null) {	
		if (value.equals(DtConstant.STANDARD)) {
				
			btn_eq_normal.setBackgroundResource(R.drawable.bg_eqbtn2);
			btn_eq_normal.setTextColor(getResources().getColor(R.color.text_light));
		} else if (value.equals(DtConstant.CINEMA)) {
				
			btn_eq_film.setBackgroundResource(R.drawable.bg_eqbtn);
			btn_eq_film.setTextColor(getResources().getColor(R.color.text_light));
		} else if (value.equals(DtConstant.MUSICHALL)) {
				
			btn_eq_music.setBackgroundResource(R.drawable.bg_eqbtn);
			btn_eq_music.setTextColor(getResources().getColor(R.color.text_light));
		}
	  } else {
			
		 btn_eq_normal.setBackgroundResource(R.drawable.bg_eqbtn2);
		 btn_eq_normal.setTextColor(getResources().getColor(R.color.text_light));
	  }
	}
	
    /**运用设置界面中的相关设置信息**/
    private void applySettings() {
		
		String value = StringUtil.getCarSettingValue(this,DtConstant.EQUALIZER_MODE);
		/**下面写监听到模式改变进行的相关操作**/
		setEQ(value);
	}

	/**设置btn背景和txt文本颜色(标准 \音乐\ 电影院)**/
	private void setBtnBgTxtColor() {
		
		btn_eq_normal.setBackground(null);
		btn_eq_film.setBackground(null);
		btn_eq_music.setBackground(null);
		ColorStateList csl = getResources().getColorStateList(R.color.btn_mainbar_colorselector);
		btn_eq_film.setTextColor(csl);
		btn_eq_music.setTextColor(csl);
		btn_eq_normal.setTextColor(csl);
	}
	
	/**切换主界面item**/
	private void switchTabPage(String pkg) {
		
		Intent intent = getIntent(pkg);
		
		if (intent != null) {
			
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			intent.setAction(Intent.ACTION_MAIN);
			try {
				
				startActivity(intent);
			} catch (ActivityNotFoundException e) {
				
	        	e.printStackTrace();
			}
		}
		
		this.finish();
	}
	
	/**给文本设置Drawable**/
	public void setTextViewDrawable(int drawableId,TextView textView) {
		
		Drawable drawable = getResources().getDrawable(drawableId);  
		drawable.setBounds(0,0,drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight());
        textView.setCompoundDrawables(null, drawable, null, null);
	}
	
	/**Service连接回调**/
	private final ServiceConnection serviceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder binder) {
			
			service = IService.Stub.asInterface(binder);
			presenter.setIService(service)
			         .setLoadType(DtConstant.NORMAL_FM).fetchFmItems();
			try {
				
				service.registerCallback(callbackStub);
				service.play();
			} catch (RemoteException e) {
				
				e.printStackTrace();
			}
			
			handleCurrentChannelChanged();
			handleChannelListChanged();
			handleFavorChanged();
			handleStatusChanged();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			
			service = null;
			handleChannelListChanged();
			handleFavorChanged();
		}
	};
    
	/**广播接收者**/
	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
        	
            String tag = "";
            
            if (intent.hasExtra("tag")) {
            	
                tag = intent.getStringExtra("tag");
            }
            
            if (!tag.equals("FM")) {
            	
        		RadioActivity.this.finish();
            }
        }
    };
    
	/**view延时点击事件监听**/
	private final View.OnClickListener mViewClickLisntener = new View.OnClickListener() {

		@Override
		public void onClick(View view) {
		
		  /**暴力点击造成服务端数据同步出现问题。用延迟相应的方式阻止这种通信bug**/
		  mixtureMsgHandler.removeMessages(DtConstant.MSG_DELAYCLICK);
		  Message delayClick = mixtureMsgHandler.obtainMessage(DtConstant.MSG_DELAYCLICK,view.getId(), 1);
		  mixtureMsgHandler.sendMessageDelayed(delayClick, 200);
		}
	};
	
	/**收音机暂停播放事件监听器**/
	private final View.OnClickListener fmAmBtnOnOfClickLisntener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (isFmAmOn) {
				try {
					/**暂停收音机**/
					if (service != null) {
						
						service.playPause();
					}
				} catch (Exception e) {
					
					e.printStackTrace();
				}
				
				fmamBtnOnOff.setBackgroundResource(R.drawable.fmam_btn_off);
				isFmAmOn = false;
			} else {
               try {
					/**播放收音机**/
            	   if (service != null) {
            		   
            		   service.play();
            	   }
				} catch (RemoteException e) {
					
					e.printStackTrace();
			   }
               
               fmamBtnOnOff.setBackgroundResource(R.drawable.fmam_btn_on);
               isFmAmOn = true;
			}
		}
	};
	
	/**左边主菜单栏事件监听**/
	private final View.OnClickListener mMainBarClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
	        
			String action = "";
			
			switch (v.getId()) {
               
			   case R.id.btnbar_radio:{}
				
			   break;
			   case R.id.btnbar_music: {
                   
				   action = DtConstant.BTMUSIC;
				   switchTabPage(action);
			   }
			   break;
			   case R.id.btnbar_ipod: {
                  
				  action = DtConstant.IPOD;
				  switchTabPage(action);
			   }
			   break;
			   case R.id.btnbar_aux: {
                  
				  action = DtConstant.AUX;
				  switchTabPage(action);
			  }
			  break;
              case R.id.btnbar_usb: {
                 
            	 action = DtConstant.USB;
            	 switchTabPage(action);
			 }
             break;
		  }
		}
	};
    
	/**EQ事件监听**/
	private final View.OnClickListener mEQListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {

			if (isSeek) {
				
				mixtureMsgHandler.removeMessages(DtConstant.MSG_ENDSEEK);
				Message msg_endseek = mixtureMsgHandler.obtainMessage(DtConstant.MSG_ENDSEEK, 0, 1);
				mixtureMsgHandler.sendMessageDelayed(msg_endseek, DtConstant.COUNT_TIME);
			}
			
			String scene = DtConstant.STANDARD;
			setBtnBgTxtColor();
			
			switch (v.getId()) {
			 case R.id.btn_eq_film:{
                
				scene = DtConstant.CINEMA;
				btn_eq_film.setBackgroundResource(R.drawable.bg_eqbtn);
				btn_eq_film.setTextColor(getResources().getColor(R.color.text_light));
				break;
			 }
			 case R.id.btn_eq_music:{
				
				scene = DtConstant.MUSICHALL;
				btn_eq_music.setBackgroundResource(R.drawable.bg_eqbtn);
				btn_eq_music.setTextColor(getResources().getColor(R.color.text_light));
				break;
			 }
			 case R.id.btn_eq_normal:{
				
				scene = DtConstant.STANDARD;
				btn_eq_normal.setBackgroundResource(R.drawable.bg_eqbtn2);
				btn_eq_normal.setTextColor(getResources().getColor(R.color.text_light));
				break;
			 }
			 default:
				 break;
			}
			
			StringUtil.setCarSettingValue(RadioActivity.this,DtConstant.EQUALIZER_MODE, scene);
		}
	};
    
	/**阴影背景点击监听**/
	private final View.OnClickListener sessionBtnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			 case R.id.bg_shadow:
			 case R.id.bg_shadow2:{
				 mixtureMsgHandler.removeMessages(DtConstant.MSG_ENDSEEK);
				 isSeek = false;
				 setTextViewDrawable(R.drawable.btn_seek_radio,txt_seekbar);
				 txt_seekbar.setTextColor(getResources().getColor(R.color.text));
				 seekBarBg.setVisibility(View.GONE);
				 iv_shadow.setVisibility(View.GONE);
				 iv_shadow2.setVisibility(View.GONE);
				 iv_next.setImageResource(R.drawable.btn_next);
				 iv_pre.setImageResource(R.drawable.btn_last);
			   break;
			 }	
			 case R.id.delete_sure:{
				 if (deleteChannelId >= 0) {
						if (collectFmItemAdapter != null) {
							
							RadioFmItem item = (RadioFmItem) collectFmItemAdapter.getItem(deleteChannelId);
							if (item != null) {
								if (service != null) {
									try {
										
										service.removeFavorChannel(item.frequence);
									} catch (RemoteException e) {
										
										e.printStackTrace();
									}
								}
							}
						}
	                    deleteChannelId = -1;
	                } else {
	                	
						if (service != null && currentBand != -1 
								            && DtConstant.CURRENTCHANNEL[currentBand] != -1) {
							try {
								
								service.removeFavorChannel(DtConstant.CURRENTCHANNEL[currentBand]);
							} catch (RemoteException e) {
								
								e.printStackTrace();
							}
						}
	                }
                  
				  String tip = getString(R.string.canclecollect);
	              ToastUtil.showChannelAlert(RadioActivity.this,parentView,tip);
				  deleteItemDialog.dismiss();
			   break;
			 }
			 case R.id.delete_cancle:{
				 
				 deleteItemDialog.dismiss();
			   break;
			 }
			 case R.id.listView_btn1:
			 case R.id.listView_btn2:{
				 
				 collectpopupwindow.dismiss();
			   break;
			 }
			 case R.id.popupwindow:{
				 
				 beingpopupwindow.dismiss();
			   break;
			 }
			 case R.id.gallery_btn_star:{
			   
			   RadioFmItem radioFmItem = normalFmItemsList.get(currentItemPosition);
			   if (radioFmItem.favor) {
				  if (service != null) {
					try {
							
					   service.removeFavorChannel(radioFmItem.frequence);
					} catch (RemoteException e) {
							
						e.printStackTrace();
					}
					String tip = getString(R.string.canclecollect);
					ToastUtil.showChannelAlert(RadioActivity.this,parentView,tip);
					}
				} else {
				  if (service != null) {
					try {
							
					    service.addFavorChannel(radioFmItem.frequence);
					} catch (RemoteException e) {
							
						e.printStackTrace();
					}
						
					String tip = getString(R.string.collect);
					ToastUtil.showChannelAlert(RadioActivity.this,parentView,tip);
				  }
				} 
			   break;
			 }
			 default:
				break;
			}
		}
	};
	
	/**View长按事件监听**/
	private final View.OnLongClickListener myViewLongClickListener = new View.OnLongClickListener() {

		@Override
		public boolean onLongClick(View view) {
			switch (view.getId()) {
			  case R.id.btn_next:{
				 if (isSeek) {
					
					Message tune_up = mixtureMsgHandler.obtainMessage(DtConstant.MSG_TUNE_UP, 0, 1);
					mixtureMsgHandler.sendMessage(tune_up);
				 }
				 delayDismissSeekbar = false;
				 break;
			   }
			   case R.id.btn_pre:{
				 if (isSeek) {
					
					Message tune_dn = mixtureMsgHandler.obtainMessage(DtConstant.MSG_TUNE_DN, 0, 1);
					mixtureMsgHandler.sendMessage(tune_dn);
				 }
				
				   delayDismissSeekbar = false;
				 break;
			   }
			   case R.id.curchannel_layout:{
				/**存台处理**/
				if (!isSeek) {
					/**电台87.5MHz特殊处理**/
					String curr_fmfreq = txt_curchannel.getText().toString().trim();
					if (curr_fmfreq.equals(StringUtil.changeToString(DtConstant.MIN_FREQUENCE_FM))) {
						
						if (fmcount == 0) {
							
							ToastUtil.showChannelAlert(RadioActivity.this,parentView,getString(R.string.singlesave0));
						} else {
							
							ToastUtil.showChannelAlert(RadioActivity.this,parentView,getString(R.string.singlesave2));
						}
						fmcount++;
						return true;
					}
					
					/**电台1602KHz特殊处理**/
					String curr_amfreq = txt_curchannel.getText().toString().trim();
					if (curr_amfreq.equals((DtConstant.MAX_FREQUENCE_AM + ""))) {
						
						if (amcount == 0) {
							
							ToastUtil.showChannelAlert(RadioActivity.this,parentView,getString(R.string.singlesave0));
						} else {
							
							ToastUtil.showChannelAlert(RadioActivity.this,parentView,getString(R.string.singlesave2));
						}
						amcount++;
						return true;
					}
					
					if (service != null && currentBand != -1 && DtConstant.CURRENTCHANNEL[currentBand] != -1) {
						
						int result = -3;
						try {
							
							result = service.addChannel(currentBand,DtConstant.CURRENTCHANNEL[currentBand]);
						} catch (RemoteException e) {
							
							e.printStackTrace();
						}
						
						if (result == 0) {
							
							ToastUtil.showChannelAlert(RadioActivity.this,parentView,getString(R.string.singlesave0));
						}else if (result == -1) {
							
							ToastUtil.showChannelAlert(RadioActivity.this,parentView,getString(R.string.singlesave1));
						} else if (result == -2) {
							
							ToastUtil.showChannelAlert(RadioActivity.this,parentView,getString(R.string.singlesave2));
						}
					}
				}
				break;
			   }
			   default:
			     break;
			}
			return true;
		}
	};
	
	/**View触摸事件监听处理**/
	private final View.OnTouchListener myViewTouchListener = new View.OnTouchListener() {

		@Override
		public boolean onTouch(View view, MotionEvent event) {
			
			int eventAction = event.getAction();
			switch (view.getId()) {
			 case R.id.btn_next:{
                if (isSeek) {
					
            	    mixtureMsgHandler.removeMessages(DtConstant.MSG_ENDSEEK);
					Message endseek = mixtureMsgHandler.obtainMessage(DtConstant.MSG_ENDSEEK, 0, 1);
					mixtureMsgHandler.sendMessageDelayed(endseek,DtConstant.COUNT_TIME);
					if (eventAction == MotionEvent.ACTION_UP) {
						
						mixtureMsgHandler.removeMessages(DtConstant.MSG_TUNE_UP);
						mixtureMsgHandler.removeMessages(DtConstant.MSG_TUNE_DN);
					}
				 } 
				break;
			 }
             case R.id.btn_pre:{
				if (isSeek) {
					
					mixtureMsgHandler.removeMessages(DtConstant.MSG_ENDSEEK);
					Message endseek = mixtureMsgHandler.obtainMessage(DtConstant.MSG_ENDSEEK, 0, 1);
					mixtureMsgHandler.sendMessageDelayed(endseek,DtConstant.COUNT_TIME);
					if (eventAction == MotionEvent.ACTION_UP) {
						
						mixtureMsgHandler.removeMessages(DtConstant.MSG_TUNE_UP);
						mixtureMsgHandler.removeMessages(DtConstant.MSG_TUNE_DN);
					}
				} 
				break;
             }
			 case R.id.listView_coll:{
				
				mixtureMsgHandler.removeMessages(DtConstant.MSG_DISMISSPOPWINDOW);
				Message dismisspopwindow = mixtureMsgHandler.obtainMessage(DtConstant.MSG_DISMISSPOPWINDOW, 0, 1);
				mixtureMsgHandler.sendMessageDelayed(dismisspopwindow,DtConstant.COUNT_TIME);
				break;
			 }
			 case R.id.seekbarpw_layout:{
				
				mixtureMsgHandler.removeMessages(DtConstant.MSG_ENDSEEK);
				Message endseek = mixtureMsgHandler.obtainMessage(DtConstant.MSG_ENDSEEK, 0, 1);
				mixtureMsgHandler.sendMessageDelayed(endseek,DtConstant.COUNT_TIME);
				
				/**重置计数器**/
				fmcount = 0;
				amcount = 0;
				break;
			 }
			 case R.id.text_search:{
				if (eventAction == MotionEvent.ACTION_DOWN) {
					
					setTextViewDrawable(R.drawable.btn_search_radio_light, txt_search);
					txt_search.setTextColor(getResources().getColor(R.color.text_light));
				} else if (eventAction == MotionEvent.ACTION_UP) {
					
					setTextViewDrawable(R.drawable.btn_search_radio, txt_search);
					txt_search.setTextColor(getResources().getColor(R.color.text));
					view.performClick();
				}
				break;
			 } 
			 case R.id.text_seek:{
				if (eventAction == MotionEvent.ACTION_DOWN) {
					
					setTextViewDrawable(R.drawable.btn_seek_radio_light, txt_seekbar);
					txt_seekbar.setTextColor(getResources().getColor(R.color.text_light));
				} else if (eventAction == MotionEvent.ACTION_UP) {
					
					setTextViewDrawable(R.drawable.btn_seek_radio, txt_seekbar);
					txt_seekbar.setTextColor(getResources().getColor(R.color.text));
					view.performClick();
				}
				break;
			 }
			 default:
			   break;
			}
			return false;
		}
	};
    
	/**ListView item点击事件监听**/
	private final AdapterView.OnItemClickListener listViewItemClickListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			if (collectFmItemAdapter != null) {
				
				moveTo(parentView,position);
			}

			collectpopupwindow.dismiss();
		}
	};
    
	/**ListView item长按事件监听**/
	private final AdapterView.OnItemLongClickListener listViewItemLongClickListener = new AdapterView.OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			
			if (isCollectionList) {
				
				deleteChannelId = position;
				deleteItemDialog.show();
			}
			
			return false;
		}
	};
	
	/**Gallery item选中事件监听**/
	private final AdapterView.OnItemSelectedListener galleryItemSelectedListener = new AdapterView.OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

			if (!isSeek) {
				
				RadioFmItem fmItem = (RadioFmItem) normalFmGalleryAdapter.getItem(position);
				mixtureMsgHandler.removeMessages(DtConstant.MSG_TUNE_TO);
				Message tune_to = mixtureMsgHandler.obtainMessage(DtConstant.MSG_TUNE_TO,fmItem.frequence, 0);
				mixtureMsgHandler.sendMessageDelayed(tune_to,200);
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {}
	};
	
	/**SeekBar刻度改变事件监听**/
	private final SeekBar.OnSeekBarChangeListener mSeekBarListener = new SeekBar.OnSeekBarChangeListener() {
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
            
			if (fromUser) {
				
				/**重置计数器**/
				fmcount = 0;
				amcount = 0;
				
				mixtureMsgHandler.removeMessages(DtConstant.MSG_ENDSEEK);
				Message endseek = mixtureMsgHandler.obtainMessage(DtConstant.MSG_ENDSEEK, 0, 1);
				mixtureMsgHandler.sendMessageDelayed(endseek, DtConstant.COUNT_TIME);

				int channelValue;
				if (currentBand == 0) {
					
					channelValue = progress * 10 + DtConstant.MIN_FREQUENCE_FM;
				} else {
					
					channelValue = progress * 9 + DtConstant.MIN_FREQUENCE_AM;
				}

        		mSeekBarTuneTime = SystemClock.uptimeMillis();

				mixtureMsgHandler.removeMessages(DtConstant.MSG_TUNE_TO);
				Message tune_to = mixtureMsgHandler.obtainMessage(DtConstant.MSG_TUNE_TO, channelValue, 1);
				mixtureMsgHandler.sendMessageDelayed(tune_to,100);
			}
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {}
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {}
	};
    
	/**内部类--内容观察者**/
	private class MyContentObserver extends ContentObserver {

		public MyContentObserver(Handler handler) {
			
			super(handler);
		}

		@Override
		public void onChange(boolean selfChange) {
			
			super.onChange(selfChange);
		    mRadioHandler.removeMessages(DtConstant.MSG_SETTINGS_CHANGED);
		    mRadioHandler.sendEmptyMessageDelayed(DtConstant.MSG_SETTINGS_CHANGED, 100);
		}
	}
	
	/**内部类--PopupWindow消失**/
	private class RadioPopupWindowListener extends PopupWindowDismissListener {
        
		public RadioPopupWindowListener(int windowType) {
			
			super(windowType);
		}

		@Override
		public void whenDismiss(int windowType) {
			
			if (windowType == COLLECT_POPUPWINDOW) {
				
				isCollectionList = false;
				iv_shadow3.setVisibility(View.GONE);
				parentView.setBackgroundResource(0);
				mixtureMsgHandler.removeMessages(DtConstant.MSG_DISMISSPOPWINDOW);
				iv_lists.setVisibility(View.VISIBLE);
			} else if (windowType == BEING_POPUPWINDOW) {
				
				iv_animation.clearAnimation();
				if (service != null && currentBand != -1 
						            && DtConstant.CURRENTCHANNEL[currentBand] != -1) {
					try {
						
						/**播放收音机**/
						service.tuneTo(DtConstant.CURRENTCHANNEL[currentBand], false);
					} catch (RemoteException e) {
						
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	/**处理当前电台变化的线程类 搜存电台时每隔1s获取当前电台值 与上1s的电台值进行对比*/
	private class CurrFreqThread implements Runnable {
		@Override
		public void run() {
			
			String tempFrequence = txt_curchannel.getText().toString().trim();
			if (txt_curchannel != null) {
				
				if (tempFrequence.equals(currFreq)) {
					
					isAllowed = true;
					isChanged = false;
				} else {
					
					isChanged = true;
					isAllowed = false;
					currFreq = tempFrequence;
				}
			}
			
			mRadioHandler.postDelayed(this, 1000);
			
			/**处理刻度条  10s后消失**/
			if (!isChanged) {
				
				delayDismissSeekbar = true;
				mRadioHandler.removeCallbacks(this);
				mRadioHandler.sendEmptyMessage(DtConstant.CURR_FREQ_CHANGED);
			}
		}
	}
	
	/**静态内部类--处理混合信息的Handler**/
	private static class MixtureMsgHandler extends RadioBaseHandler<RadioActivity> {
		
		public MixtureMsgHandler(RadioActivity activity) {
			
			super(activity);
		}
		
		@Override
		public void toHandleMessage(RadioActivity activity,Message msg) {
			
		  switch (msg.what) {
			case DtConstant.MSG_DELAYCLICK:{
				switch (msg.arg1) {
				  case R.id.btn_fmam:{
					if (activity.service != null) {
						try {
							
							activity.service.band();
						} catch (RemoteException e) {
							
							e.printStackTrace();
						}
					}
					
					if (activity.isSeek) {
						
						activity.mixtureMsgHandler.removeMessages(DtConstant.MSG_ENDSEEK);
						Message endseek = activity.mixtureMsgHandler.obtainMessage(DtConstant.MSG_ENDSEEK, 0, 1);
						activity.mixtureMsgHandler.sendMessageDelayed(endseek, DtConstant.COUNT_TIME);
					}
					break;
				 }
				 case R.id.btn_list:{
					if (!activity.isCollectionList) {
						
						/**获取数据**/
						activity.presenter.setIService(activity.service)
						                  .setLoadType(DtConstant.COLLECTION_FM).fetchFmItems();
						activity.isCollectionList = true;
						activity.collectpopupwindow.showAtLocation(activity.parentView,Gravity.RIGHT, 0, 0);
						activity.iv_shadow3.setVisibility(View.VISIBLE);
						activity.iv_lists.setVisibility(View.INVISIBLE);
						activity.iv_lists_back.setVisibility(View.VISIBLE);
						Message dismisspopwindow = activity.mixtureMsgHandler.obtainMessage(DtConstant.MSG_DISMISSPOPWINDOW, 0, 1);
						activity.mixtureMsgHandler.sendMessageDelayed(dismisspopwindow, DtConstant.COUNT_TIME);
					}
					break;
				 }
				 case R.id.btn_pre:{
					if (activity.isSeek) {
						
						Message tune_dn = activity.mixtureMsgHandler.obtainMessage(DtConstant.MSG_TUNE_DN, 0, 0);
						activity.mixtureMsgHandler.sendMessage(tune_dn);
					} else {
						if (activity.service != null) {
							try {
								
								activity.service.seekDown();
							} catch (RemoteException e) {
								
								e.printStackTrace();
							}
						}
					}
					break;
				 }
				 case R.id.btn_next:{
					if (activity.isSeek) {
						
						Message tune_up = activity.mixtureMsgHandler.obtainMessage(DtConstant.MSG_TUNE_UP, 0, 0);
						activity.mixtureMsgHandler.sendMessage(tune_up);
					} else {
						if (activity.service != null) {
							try {
								
								activity.service.seekUp();
							} catch (RemoteException e) {
								
								e.printStackTrace();
							}
						}
					}
					break;
				 }
				 case R.id.listpw_btn_dismiss:{
					
					activity.collectpopupwindow.dismiss();
					break;
				 }
				 case R.id.text_search:{
					/**每一次搜存完成之后  再进行下一次搜存**/
					if (!activity.isAllowed) {}
					
					/**去除之前的handler message**/
					activity.mixtureMsgHandler.removeMessages(DtConstant.MSG_ENDSEEK);
					activity.delayDismissSeekbar = false;
					
					if (activity.service != null) {
						try {
							
							activity.service.scan();
						} catch (RemoteException e) {
							
							e.printStackTrace();
						}
					}
					activity.currFreq = "";
					activity.mRadioHandler.post(activity.currThread);
					/**置计数器**/
					activity.fmcount = 0;
					activity.amcount = 0;
					break;
				 }
				 case R.id.text_seek:{
					if (activity.isSeek) {
						
						activity.mixtureMsgHandler.removeMessages(DtConstant.MSG_ENDSEEK);
						activity.isSeek = false;
						activity.setTextViewDrawable(R.drawable.btn_seek_radio, activity.txt_seekbar);
						activity.txt_seekbar.setTextColor(activity.getResources().getColor(R.color.text));
						
						/**刻度条**/
						activity.seekBarBg.setVisibility(View.GONE);
						activity.iv_shadow.setVisibility(View.GONE);
						activity.iv_shadow2.setVisibility(View.GONE);
						activity.iv_next.setImageResource(R.drawable.btn_next);
						activity.iv_pre.setImageResource(R.drawable.btn_last);
					} else {
						
						activity.isSeek = true;
						activity.setTextViewDrawable(R.drawable.btn_seek_radio_light, activity.txt_seekbar);
						activity.txt_seekbar.setTextColor(activity.getResources().getColor(R.color.text_light));
						
						/**刻度条**/
						activity.seekBarBg.setVisibility(View.VISIBLE);
						
						activity.iv_shadow.setVisibility(View.VISIBLE);
						activity.iv_shadow2.setVisibility(View.VISIBLE);
						activity.iv_next.setImageResource(R.drawable.btn_next2);
						activity.iv_pre.setImageResource(R.drawable.btn_last2);
					}
					
					if (activity.delayDismissSeekbar) {
						
						activity.mixtureMsgHandler.removeMessages(DtConstant.MSG_ENDSEEK);
						Message endseek = activity.mixtureMsgHandler.obtainMessage(DtConstant.MSG_ENDSEEK, 0, 1);
						activity.mixtureMsgHandler.sendMessageDelayed(endseek, DtConstant.COUNT_TIME);
					}
					break;
				  }
				}
			  break;
			}
			case DtConstant.MSG_TUNE_TO: {
				if (activity.service != null) {
					try {
						
						activity.service.tuneTo(msg.arg1, msg.arg2 == 1);
					} catch (RemoteException e) {
						
						e.printStackTrace();
					}
				}
				break;
			}
		    case DtConstant.MSG_TUNE_DN:{
				if (activity.isSeek) {
					
					activity.mixtureMsgHandler.removeMessages(DtConstant.MSG_ENDSEEK);
					Message endseek = activity.mixtureMsgHandler.obtainMessage(DtConstant.MSG_ENDSEEK, 0, 1);
					activity.mixtureMsgHandler.sendMessageDelayed(endseek,6000);
				}
				
				if (msg.arg2 == 1) { 
					
					Message tune_dn = activity.mixtureMsgHandler.obtainMessage(DtConstant.MSG_TUNE_DN, 0, 1);
					activity.mixtureMsgHandler.sendMessageDelayed(tune_dn, 200);
				}

				if (activity.service != null) {
					try {
						
						activity.service.tuneDown();
					} catch (RemoteException e) {
						
						e.printStackTrace();
					}
				}
                
				activity.delayDismissSeekbar = true;
				break;
			 }
			 case DtConstant.MSG_TUNE_UP:{
				if (activity.isSeek) {
					
					activity.mixtureMsgHandler.removeMessages(DtConstant.MSG_ENDSEEK);
					Message endseek = activity.mixtureMsgHandler.obtainMessage(DtConstant.MSG_ENDSEEK, 0, 1);
					activity.mixtureMsgHandler.sendMessageDelayed(endseek,6000);
				}
				
				if (msg.arg2 == 1) {
					
					Message tune_up = activity.mixtureMsgHandler.obtainMessage(DtConstant.MSG_TUNE_UP, 0, 1);
					activity.mixtureMsgHandler.sendMessageDelayed(tune_up, 200);
				}

				if (activity.service != null) {
					try {
						
						activity.service.tuneUp();
					} catch (RemoteException e) {
						
						e.printStackTrace();
					}
				}
             
				activity.delayDismissSeekbar = true;
				break;
			 }
			 case DtConstant.MSG_ENDSEEK:{
             
				activity.mixtureMsgHandler.removeMessages(DtConstant.MSG_ENDSEEK);
				activity.isSeek = false;
				activity.setTextViewDrawable(R.drawable.btn_seek_radio, activity.txt_seekbar);
				activity.txt_seekbar.setTextColor(activity.getResources().getColor(R.color.text));
				activity.seekBarBg.setVisibility(View.GONE);
				activity.iv_shadow.setVisibility(View.GONE);
				activity.iv_shadow2.setVisibility(View.GONE);
				activity.iv_next.setImageResource(R.drawable.btn_next);
				activity.iv_pre.setImageResource(R.drawable.btn_last);
				break;
			 }
			 case DtConstant.MSG_DISMISSPOPWINDOW:{
				if (activity.isCollectionList && activity.collectpopupwindow.isShowing()) {
					
					activity.collectpopupwindow.dismiss();
				}
				
				/**收藏列表弹窗消失的同时  消失删除电台对话框**/
				if (activity.deleteItemDialog != null && activity.deleteItemDialog.isShowing()) {
					
					activity.deleteItemDialog.dismiss();
				}
				
				activity.isCollectionList = false;
				break;
			  }
		   }
		}
	};
	
	/**静态内部类Handler**/
	private static class RadioHandler extends RadioBaseHandler<RadioActivity> {
		
		public RadioHandler(RadioActivity radioActivity) {
			
			super(radioActivity);
		}
		
		@Override
		public void toHandleMessage(RadioActivity radioActivity,Message msg) {
			
			switch (msg.what) {
			 case DtConstant.CURR_FREQ_CHANGED:{
				  
				 int time = 10 * 1000;
				 int endseek = DtConstant.MSG_ENDSEEK;
				 radioActivity.mixtureMsgHandler.removeMessages(endseek);
				 radioActivity.mixtureMsgHandler.sendEmptyMessageDelayed(endseek, time);
			   break;
			 }
			 case DtConstant.SEEKBAR_CHANGE_INFO:{
				 
				 radioActivity.updateSeekBar(msg.arg1, msg.arg2);
			  break;
			 }
			 case DtConstant.MSG_SETTINGS_CHANGED:{
					
				 radioActivity.applySettings();
			   break;
			 }
			 case DtConstant.MSG_STATUS_CHANGED: {
					
				 radioActivity.handleStatusChanged();
			   break;
			 }
			 case DtConstant.MSG_CHANNELLIST_CHANGED: {
					
			     int band = msg.arg1;
			     if (radioActivity.currentBand == band)  {
						
				    radioActivity.handleChannelListChanged();
			     }
			   break;
			 }
			 case DtConstant.MSG_FAVORCHANNELLIST_CHANGED: {
					
			     radioActivity.handleFavorChanged();
			   break;
			 }
			 case DtConstant.MSG_CHANNEL_CHANGED:{
					
			     radioActivity.handleCurrentChannelChanged();
			   break;
			 }
			 case DtConstant.MSG_DISPLAY_CHANGED:{
					
			     radioActivity.updateCurrentDisplay(msg.arg1, msg.arg2);
			     radioActivity.updateSeekBar(msg.arg1, msg.arg2);
			   break;
			 }
			 default: 
			  break;
		  }
	   }
	}
}
