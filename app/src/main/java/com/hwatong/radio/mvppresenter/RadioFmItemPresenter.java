package com.hwatong.radio.mvppresenter;
import java.util.List;
import com.hwatong.radio.IService;
import com.hwatong.radio.entity.RadioFmItem;
import com.hwatong.radio.mvpmodel.IRadioFmModel;
import com.hwatong.radio.mvpmodel.IRadioFmModel.LoadRadioFmDataComplete;
import com.hwatong.radio.mvpmodel.RadioFmModelImpl;
import com.hwatong.radio.mvpview.IRadioFmItemView;

public class RadioFmItemPresenter<T extends IRadioFmItemView> extends BasePresenter<T> {
    
	private int loadType;
	private IService iService;
	private IRadioFmModel radioFmModel;
	
	public RadioFmItemPresenter() {
		
		radioFmModel = new RadioFmModelImpl();
	}
	
	/**获取数据**/
	public void fetchFmItems() {
		radioFmModel.loadRadioFmData(loadType, iService, new LoadRadioFmDataComplete() {
			@Override
			public void onLoadComplete(List<RadioFmItem> fmDatas) {
				if (mRefs.get() != null) {
					
					mRefs.get().loadFmItemDatas(loadType, fmDatas);
				}
			}
		});
	}
	
	@SuppressWarnings({ "hiding", "unchecked" })
	public <T extends IRadioFmItemView> RadioFmItemPresenter<T> setLoadType(int loadType) {
		
		this.loadType = loadType;
		return (RadioFmItemPresenter<T>) this;
	}
	
	@SuppressWarnings({ "hiding", "unchecked" })
	public <T extends IRadioFmItemView> RadioFmItemPresenter<T> setIService(IService iService) {

		this.iService = iService;
		return (RadioFmItemPresenter<T>) this;
	}
}
