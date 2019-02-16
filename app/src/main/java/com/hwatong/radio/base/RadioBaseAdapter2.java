package com.hwatong.radio.base;
import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

public abstract class RadioBaseAdapter2<T> extends BaseAdapter {
    
	private List<T> datas;
	protected LayoutInflater layoutInflater;
	public RadioBaseAdapter2(Context context,List<T> datas) {
		
		this.datas = datas;
		layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
}
