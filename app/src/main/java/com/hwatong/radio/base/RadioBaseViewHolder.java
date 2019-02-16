package com.hwatong.radio.base;
import com.hwatong.radio.util.ViewUtils;
import android.app.Activity;
import android.support.v4.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class RadioBaseViewHolder {
   
	/**复用的view**/
	private View mConvertView;
	/**存储View的容器**/
	private ArrayMap<Integer, View> views;
    
	private RadioBaseViewHolder(Activity activity,ViewGroup parent,int layoutid,int position) {
		
		views = new ArrayMap<>(16);
		mConvertView = LayoutInflater.from(activity).inflate(layoutid, parent,false);
		mConvertView.setTag(this);
	}
	
	private RadioBaseViewHolder(Activity activity,ViewGroup parent,int layoutid,int[] viewsIds,int position) {
		
		views = new ArrayMap<>(16);
		mConvertView = LayoutInflater.from(activity).inflate(layoutid, parent,false);
		ViewUtils.adaptLayout(activity, mConvertView, viewsIds);
		mConvertView.setTag(this);
	}
	
	/**获取ViewHolder对象**/
	public static RadioBaseViewHolder getViewHolder(Activity activity,View convertView,ViewGroup parent,
			                                         int layoutid,int position) {
		
		if (convertView == null) {
			
			return new RadioBaseViewHolder(activity, parent, layoutid, position);
		} else {
			
			RadioBaseViewHolder baseViewHolder = (RadioBaseViewHolder) convertView.getTag();
			return baseViewHolder;
		}
	}
	
	public static RadioBaseViewHolder getViewHolder(Activity activity,View convertView,ViewGroup parent,
			                                         int layoutid,int[] viewsIds,int position) {
		
		if (convertView == null) {
			
			return new RadioBaseViewHolder(activity, parent, layoutid, viewsIds, position);
		} else {
			
			RadioBaseViewHolder baseViewHolder = (RadioBaseViewHolder) convertView.getTag();
			return baseViewHolder;
		}
	}
	
	/**获取View方法**/
	@SuppressWarnings("unchecked")
	public <T extends View> T getView(int viewid) {
		
		View view = views.get(viewid);
		if (view == null) {
			
			view = mConvertView.findViewById(viewid);
			views.put(viewid, view);
		}
		
		return (T) view;
	}
	
	/**获取可复用的View**/
	public View getConvertView() {
	   
	   return mConvertView;
	}
}
