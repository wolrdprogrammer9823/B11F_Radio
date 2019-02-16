package com.hwatong.radio.datas;
import java.util.List;
import com.hwatong.radio.IService;
import com.hwatong.radio.entity.RadioFmItem;

public interface ILoadFmItem {
   
	List<RadioFmItem> loadFmItems(IService iService);
}
