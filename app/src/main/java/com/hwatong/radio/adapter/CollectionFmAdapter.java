package com.hwatong.radio.adapter;
import java.util.ArrayList;
import java.util.List;
import com.hwatong.radio.base.RadioBaseAdapter;
import com.hwatong.radio.base.RadioBaseViewHolder;
import com.hwatong.radio.constant.DtConstant;
import com.hwatong.radio.entity.RadioFmItem;
import com.hwatong.radio.ui.R;
import com.hwatong.radio.util.StringUtil;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CollectionFmAdapter extends RadioBaseAdapter<RadioFmItem> {
	
	private Activity activity;
	private int currentBand = -1;
	private List<RadioFmItem> collectFmItemsList = new ArrayList<>(16);
	
	public CollectionFmAdapter(Activity activity,int currentBand,List<RadioFmItem> collectFmItemsList) {
		
		super(activity, collectFmItemsList);
		
		this.activity = activity;
		this.currentBand = currentBand;
		this.collectFmItemsList = collectFmItemsList;
	}

	@Override
	public void reuseView(View convertView, RadioBaseViewHolder baseViewHolder, ViewGroup parent, int position) {
		
        TextView tvMhz = baseViewHolder.getView(R.id.listitem_mhz);
		TextView tvFrequence = baseViewHolder.getView(R.id.listitem_freq);
		RelativeLayout rootLayout = baseViewHolder.getView(R.id.listitem_layout);
		
		int frequence = collectFmItemsList.get(position).frequence;
		
		if (currentBand != -1 && frequence == DtConstant.CURRENTCHANNEL[currentBand]) {
			
			tvMhz.setTextColor(activity.getResources().getColor(R.color.text_light));
			tvFrequence.setTextColor(activity.getResources().getColor(R.color.text_light));
			//myViewHolder.layout.setBackgroundResource(R.drawable.radio_listadapter_light);
		}else {
			
			rootLayout.setBackgroundResource(0);
			tvMhz.setTextColor(activity.getResources().getColor(R.color.white));
			tvFrequence.setTextColor(activity.getResources().getColor(R.color.white));
		}
		
		if (frequence >= DtConstant.MIN_FREQUENCE_FM && frequence <= DtConstant.MAX_FREQUENCE_FM) {
			
			tvFrequence.setText(StringUtil.changeToString(frequence));
			tvMhz.setText(activity.getResources().getString(R.string.fmsign));
		} else {
			
			tvFrequence.setText(""+(frequence));
			tvMhz.setText(activity.getResources().getString(R.string.amsign));
		}
	}
	
	@Override
	public int getLayoutId() {
		
		return R.layout.listitem;
	}
	
	@Override
	public RadioBaseViewHolder createViewHolder(Activity activity, View convertView, ViewGroup parent, 
			                                    int layoutid,int position) {
        
		int[] viewsIds = new int[]{R.id.listitem_mhz,R.id.listitem_freq};
		RadioBaseViewHolder radioBaseViewHolder = RadioBaseViewHolder.getViewHolder(activity, convertView, parent, 
				                                                                    layoutid, viewsIds, position);
		return radioBaseViewHolder;
	}
}
