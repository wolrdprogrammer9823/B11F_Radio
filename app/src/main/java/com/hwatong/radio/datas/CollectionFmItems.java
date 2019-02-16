package com.hwatong.radio.datas;
import java.util.ArrayList;
import java.util.List;
import com.hwatong.radio.Channel;
import com.hwatong.radio.IService;
import com.hwatong.radio.entity.RadioFmItem;
import android.os.RemoteException;

public class CollectionFmItems implements ILoadFmItem {

	@Override
	public List<RadioFmItem> loadFmItems(IService iService) {
		
		RadioFmItem radioFmItem = null;
		List<Channel> favorChannelsList = new ArrayList<>(16);
		List<RadioFmItem> fmItemsList = new ArrayList<>(16);
		
		try {
			 
			favorChannelsList = iService.getFavorChannelList();
		} catch (RemoteException e) {
				
			e.printStackTrace();
		}
			 
		final int favorChannelsCount = favorChannelsList.size();
		for (int i = 0; i < favorChannelsCount; i++) {
				
			Channel channel = favorChannelsList.get(i);
			radioFmItem = new RadioFmItem();
			radioFmItem.favor = false;
			radioFmItem.frequence = channel.frequence;
			fmItemsList.add(radioFmItem);
		}
		
		return fmItemsList;
	}
}
