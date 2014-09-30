package edu.kaist.irlab.sglee.util;

public class SortingArray {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int[] sample1 = {3,1,4,6,100,-3,0};
		int[] sample2 = {-3,0,-20,22};
		double[] sample3 = {1.0, 0.3, -0.4, -20, 500, 12};
		
		
		System.out.println("max value is = "+getMaxValue(sample3));
		System.out.println("min value is = "+getMinValue(sample3));
		System.out.println("max index is = "+getMaxIndex(sample3));
		System.out.println("min index is = "+getMinIndex(sample3));

		
	}
	public static int getMaxIndex(int[] intArray){
		int maxInx = 0;
		int minInx = 0;

		int max = intArray[0]; // 배열의 첫 번째 값으로 최대값을 초기화 한다.
		int min = intArray[0]; // 배열의 첫 번째 값으로 최소값을 초기화 한다.
		 
		for(int i=1; i < intArray.length; i++){
		   if(intArray[i] > max){
		     max = intArray[i];
		     maxInx = i;
		   }
		   if(intArray[i] < min){
		     min = intArray[i];
		     minInx = i;
		   }
		}	
		
		return maxInx;
	}
	public static int getMaxIndex(double[] intArray){
		int maxInx = 0;
		int minInx = 0;

		double max = intArray[0]; // 배열의 첫 번째 값으로 최대값을 초기화 한다.
		double min = intArray[0]; // 배열의 첫 번째 값으로 최소값을 초기화 한다.
		 
		for(int i=1; i < intArray.length; i++){
		   if(intArray[i] > max){
		     max = intArray[i];
		     maxInx = i;
		   }
		   if(intArray[i] < min){
		     min = intArray[i];
		     minInx = i;
		   }
		}	
		
		return maxInx;
	}

	public static int getMinIndex(int[] intArray){
		int maxInx = 0;
		int minInx = 0;

		int max = intArray[0]; // 배열의 첫 번째 값으로 최대값을 초기화 한다.
		int min = intArray[0]; // 배열의 첫 번째 값으로 최소값을 초기화 한다.
		 
		for(int i=1; i < intArray.length; i++){
		   if(intArray[i] > max){
		     max = intArray[i];
		     maxInx = i;
		   }
		   if(intArray[i] < min){
		     min = intArray[i];
		     minInx = i;
		   }
		}	
		
		return minInx;
	}
	public static int getMinIndex(double[] intArray){
		int maxInx = 0;
		int minInx = 0;

		double max = intArray[0]; // 배열의 첫 번째 값으로 최대값을 초기화 한다.
		double min = intArray[0]; // 배열의 첫 번째 값으로 최소값을 초기화 한다.
		 
		for(int i=1; i < intArray.length; i++){
		   if(intArray[i] > max){
		     max = intArray[i];
		     maxInx = i;
		   }
		   if(intArray[i] < min){
		     min = intArray[i];
		     minInx = i;
		   }
		}	
		
		return minInx;
	}
	public static int getMaxValue(int[] intArray){
		
		int max = intArray[0]; // 배열의 첫 번째 값으로 최대값을 초기화 한다.
		int min = intArray[0]; // 배열의 첫 번째 값으로 최소값을 초기화 한다.
		 
		for(int i=1; i < intArray.length; i++){
		   if(intArray[i] > max){
		     max = intArray[i];
		   }
		   if(intArray[i] < min){
		     min = intArray[i];
		   }
		}	
		
		return max;
	}
	public static int getMinValue(int[] intArray){
		
		int max = intArray[0]; // 배열의 첫 번째 값으로 최대값을 초기화 한다.
		int min = intArray[0]; // 배열의 첫 번째 값으로 최소값을 초기화 한다.
		 
		for(int i=1; i < intArray.length; i++){
		   if(intArray[i] > max){
		     max = intArray[i];
		   }
		   if(intArray[i] < min){
		     min = intArray[i];
		   }
		}	
		
		return min;
	}
	
	public static double getMaxValue(double[] intArray){
		
		double max = intArray[0]; // 배열의 첫 번째 값으로 최대값을 초기화 한다.
		double min = intArray[0]; // 배열의 첫 번째 값으로 최소값을 초기화 한다.
		 
		for(int i=1; i < intArray.length; i++){
		   if(intArray[i] > max){
		     max = intArray[i];
		   }
		   if(intArray[i] < min){
		     min = intArray[i];
		   }
		}	
		
		return max;
	}
	
	public static double getMinValue(double[] intArray){
		
		double max = intArray[0]; // 배열의 첫 번째 값으로 최대값을 초기화 한다.
		double min = intArray[0]; // 배열의 첫 번째 값으로 최소값을 초기화 한다.
		 
		for(int i=1; i < intArray.length; i++){
		   if(intArray[i] > max){
		     max = intArray[i];
		   }
		   if(intArray[i] < min){
		     min = intArray[i];
		   }
		}	
		
		return min;
	}
	
}
