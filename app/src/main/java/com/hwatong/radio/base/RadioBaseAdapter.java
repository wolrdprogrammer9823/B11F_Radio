package com.hwatong.radio.base;
import java.util.List;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class RadioBaseAdapter<T> extends BaseAdapter {
    
	private List<T> datas;
	private Activity activity;
	protected LayoutInflater layoutInflater;
	
	public RadioBaseAdapter(Activity activity,List<T> datas) {
		
		this.datas = datas;
		this.activity = activity;
		layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {

		return datas.size();
	}

	@Override
	public Object getItem(int position) {
		
		return datas.get(position);
	}

	@Override
	public long getItemId(int itemId) {
		
		return itemId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		RadioBaseViewHolder baseViewHolder = createViewHolder(activity, convertView, parent, getLayoutId(), position);
		reuseView(convertView,baseViewHolder,parent,position);
		return baseViewHolder.getConvertView();
	}
    
	public abstract int getLayoutId();
	public abstract void reuseView(View convertView,RadioBaseViewHolder baseViewHolder,ViewGroup parent,int position);
	public abstract RadioBaseViewHolder createViewHolder(Activity activity,View convertView,ViewGroup parent,int layoutid,int position);
}
