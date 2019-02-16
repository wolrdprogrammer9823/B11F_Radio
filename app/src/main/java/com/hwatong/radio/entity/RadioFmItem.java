package com.hwatong.radio.entity;
import java.io.Serializable;

public class RadioFmItem implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public int frequence;
	public boolean favor;

	@Override
	public boolean equals(Object o) {
		
        if (o == this) {
        	
            return true;
        }
        
		if (o instanceof RadioFmItem) {
			
			return frequence == ((RadioFmItem)o).frequence;
		}
		
		return false;
	}
	
	public class Comparator implements java.util.Comparator<RadioFmItem> {
		@Override
		public int compare(RadioFmItem arg0, RadioFmItem arg1) {
			
			return arg0.frequence - arg1.frequence;
		}
	}	
}
