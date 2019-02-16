package com.hwatong.radio.mvpmodel;
import java.util.List;
import com.hwatong.radio.IService;
import com.hwatong.radio.entity.RadioFmItem;

public interface IRadioFmModel {

	void loadRadioFmData(int loadType,IService iService,LoadRadioFmDataComplete loadRadioFmDataComplete);
	
	interface LoadRadioFmDataComplete {

		void onLoadComplete(List<RadioFmItem> fmDatas);
	}
}
