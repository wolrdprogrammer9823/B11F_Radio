package com.hwatong.radio.datas;
import java.util.ArrayList;
import java.util.List;
import com.hwatong.radio.Channel;
import com.hwatong.radio.IService;
import com.hwatong.radio.entity.RadioFmItem;
import android.os.RemoteException;

public class NormalFmItems implements ILoadFmItem {

	@Override
	public List<RadioFmItem> loadFmItems(IService iService) {

		int currentBand = -1;
		RadioFmItem radioFmItem = null;
		List<Channel> favorChannelsList = new ArrayList<Channel>(16);
		List<Channel> normalChannelsList = new ArrayList<Channel>(16);
		List<RadioFmItem> fmItemsList = new ArrayList<RadioFmItem>(16);
		
        try {
			
			currentBand = iService.getCurrentBand();
		} catch (RemoteException e) {
			
			e.printStackTrace();
		}
        
        try {
			   
			favorChannelsList = iService.getFavorChannelList();
		} catch (RemoteException e) {
			
			e.printStackTrace();
		}
        
        try {
        	
        	normalChannelsList = iService.getChannelList(currentBand);
        } catch (RemoteException e) {
        	
        	e.printStackTrace();
        }
		   
		int favorChannelsCount = favorChannelsList.size();
		int normalChannelsCount = normalChannelsList.size();
		
 	    for (int i = 0; i < normalChannelsCount; i++) {
 			
 		    Channel normalChannel = normalChannelsList.get(i);
 			radioFmItem = new RadioFmItem();
 			radioFmItem.favor = false;
 			radioFmItem.frequence = normalChannel.frequence;
 			
			for (int j = 0; j < favorChannelsCount; j++) {
					
			   Channel favorChannel = favorChannelsList.get(j);
			   if (radioFmItem.frequence == favorChannel.frequence) {
						
					radioFmItem.favor = true;
					break;
				}
			}
				
			fmItemsList.add(radioFmItem);
		  }
 	       
		return fmItemsList;
	}
}
