package com.hwatong.radio.mvpview;
import java.util.List;
import com.hwatong.radio.entity.RadioFmItem;

public interface IRadioFmItemView {
  
	void loadFmItemDatas(int loadType,List<RadioFmItem> fmItemsList);
}
