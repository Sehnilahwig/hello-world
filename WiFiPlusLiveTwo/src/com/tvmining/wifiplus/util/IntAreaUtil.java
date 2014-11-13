package com.tvmining.wifiplus.util;



public class IntAreaUtil {
	public static boolean rangeInDefined(double current, double min, double max)
    {
        return Math.max(min, current) == Math.min(current, max);
    }
    
    public synchronized static boolean isFresh(boolean[] freshArray,float rate){
    	boolean result = false;
    	
    	if(rate == Constant.PROGRESS_DOWNLOAD_ALL_SIZE){
    		result = true;
    	}else{
    		for(int i=0;i<100;i++){
        		double l = i + 1d;
        		
        		if(rangeInDefined(rate,i,l)){
        			if(!freshArray[i]){
            			freshArray[i] = true;
            			result = true;
            		}
        			else{
        				result = false;
        			}
        		}
        	}
    	}
    	
    	return result;
    }
    
    public synchronized static boolean isFreshLargeProgress(boolean[] freshArray,float rate){
    	boolean result = false;
    	if(rate >= Constant.PROGRESS_DOWNLOAD_ALL_SIZE){
    		rate = 100;
    	}
    	if(rate == 100){
    		result = true;
    	}else{
    		int i= 0;
    		while(i < 100){
    			double l = i + 20d;
        		
        		if(rangeInDefined(rate,i,l)){
        			if(!freshArray[i]){
            			freshArray[i] = true;
            			result = true;
            		}
        			else{
        				result = false;
        			}
        		}
        		i+=20;
    		}
    	}
    	
    	return result;
    }
}
