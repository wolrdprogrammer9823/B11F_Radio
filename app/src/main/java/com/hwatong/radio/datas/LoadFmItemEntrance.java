package com.hwatong.radio.datas;
import java.io.ObjectStreamException;
import java.util.List;
import com.hwatong.radio.IService;
import com.hwatong.radio.entity.RadioFmItem;

public class LoadFmItemEntrance {
    
	private ILoadFmItem loadFmItem;
	/**volatile关键字可以防止高并发情况下发生的jvm指令重排列造成的单例破坏**/
	private static volatile LoadFmItemEntrance mInstance;
	private LoadFmItemEntrance(){}
	
	public static LoadFmItemEntrance getInstance() {
		
		if (mInstance == null) {
			synchronized (LoadFmItemEntrance.class) {
				
				if (mInstance == null) {
					
					mInstance = new LoadFmItemEntrance();
				}
			}
		}
		
		return mInstance;
	}
	
	/**防止反射情况下造成的单例破坏**/
	public Object readResolve() throws ObjectStreamException {
		
		return mInstance;
	}

	public void setLoadFmItem(ILoadFmItem loadFmItem) {
		
		this.loadFmItem = loadFmItem;
	}
	
	public List<RadioFmItem> getRadioFmItems(IService iService) {
		
		return loadFmItem.loadFmItems(iService);
	}
}
