package com.hwatong.radio.mvpmodel;
import com.hwatong.radio.IService;
import com.hwatong.radio.constant.DtConstant;
import com.hwatong.radio.datas.CollectionFmItems;
import com.hwatong.radio.datas.ILoadFmItem;
import com.hwatong.radio.datas.LoadFmItemEntrance;
import com.hwatong.radio.datas.NormalFmItems;

public class RadioFmModelImpl implements IRadioFmModel {

	@Override
	public void loadRadioFmData(int loadType,IService iService,LoadRadioFmDataComplete loadRadioFmDataComplete) {
		
		ILoadFmItem loadFmItem = null;
		LoadFmItemEntrance fmItemEntrance = LoadFmItemEntrance.getInstance();
		
		switch (loadType) {
		 case DtConstant.NORMAL_FM:{

			 loadFmItem = new NormalFmItems();
			 break;
		 }
		 case DtConstant.COLLECTION_FM:{

			loadFmItem = new CollectionFmItems();
			break;
		 }
		 default:{
			 
			loadFmItem = new NormalFmItems();
			break;
		 }	
	 }
		
	 fmItemEntrance.setLoadFmItem(loadFmItem);
	 loadRadioFmDataComplete.onLoadComplete(fmItemEntrance.getRadioFmItems(iService));
   }
}
