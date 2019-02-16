package com.hwatong.radio.adapter;
import java.util.ArrayList;
import java.util.List;
import com.hwatong.radio.base.RadioBaseViewHolder;
import com.hwatong.radio.constant.DtConstant;
import com.hwatong.radio.entity.RadioFmItem;
import com.hwatong.radio.ui.R;
import com.hwatong.radio.util.StringUtil;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DisplayFmListAdapter extends FancyCoverFlowAdapter {
  
	private Activity activity;
	private int currentBand = -1;
	private IFmItemClickListener fmItemClickListener;
	private List<RadioFmItem> normalFmItemsList = new ArrayList<RadioFmItem>(16);
	
	public DisplayFmListAdapter(Activity activity,int currentBand,List<RadioFmItem> normalFmItemsList) {
		
		this.activity = activity;
		this.currentBand = currentBand;
		this.normalFmItemsList = normalFmItemsList;
	}
	
	@Override
	public int getCount() {
	    
		int normalFmItemsCount = normalFmItemsList.size();
		if (normalFmItemsCount == 0) {
			
			return 0;
		}
		return Integer.MAX_VALUE;
	}

	@Override
	public Object getItem(int position) {
        
		int normalFmItemsCount = normalFmItemsList.size();
		if (normalFmItemsCount == 0) {
			
			return null;
		}
		return normalFmItemsList.get(position % normalFmItemsCount);
	}

	@Override
	public long getItemId(int position) {
		
		int normalFmItemsCount = normalFmItemsList.size();
		if (normalFmItemsCount == 0) {
			
			return position;
		}

		return position % normalFmItemsCount;
	}

	@Override
	public View getCoverFlowItem(int position, View convertView,ViewGroup parent) {
		
		RadioBaseViewHolder viewHolder = RadioBaseViewHolder.getViewHolder(activity, convertView, parent, 
				                                                           R.layout.galleryitem, position);
		TextView tvFm = viewHolder.getView(R.id.gallery_text_fm);
		TextView tvMhz = viewHolder.getView(R.id.gallery_text_mhz);
		TextView tvFrequence = viewHolder.getView(R.id.gallery_freq);
		ImageView ivStar = viewHolder.getView(R.id.gallery_btn_star);
		RelativeLayout rootLayout = viewHolder.getView(R.id.gallery_layout);
		
		int normalFmItemsCount = normalFmItemsList.size();
		int index = position % normalFmItemsCount;
		
		if (normalFmItemsCount > 0) {

			RadioFmItem radioFmItem = normalFmItemsList.get(index);
	
			if (currentBand == 0) {
				
				tvMhz.setText("MHz");
				tvFm.setText(activity.getResources().getText(R.string.fm));
				tvFrequence.setText(StringUtil.changeToString(radioFmItem.frequence));
			} else {
				
				tvMhz.setText("KHz");
				tvFm.setText(activity.getResources().getText(R.string.am));
				tvFrequence.setText("" + radioFmItem.frequence);
			}

			if (radioFmItem.favor) {
				
				ivStar.setImageResource(R.drawable.btn_star_radio_light);
			} else {
				
				ivStar.setImageResource(R.drawable.btn_star_radio);
			}
            
			
			if (currentBand != -1 && DtConstant.CURRENTCHANNEL[currentBand] == radioFmItem.frequence) {
				
				fmItemClickListener.onFmItemClick(index,ivStar);
				rootLayout.setBackgroundResource(R.drawable.bg_galleryitem_radio_light);
			} else {
				
				tvMhz.setTextColor(0xFFFFFFFF);
				tvFrequence.setTextColor(0xFFFFFFFF);
				rootLayout.setBackgroundResource(R.drawable.bg_galleryitem_radio);
			}
		}

		return viewHolder.getConvertView();
	}
    
	public interface IFmItemClickListener {
		
		void onFmItemClick(int position,ImageView imageView);
	}
	
	public void setFmItemClickListener(IFmItemClickListener fmItemClickListener) {
		
		this.fmItemClickListener = fmItemClickListener;
	}
}
