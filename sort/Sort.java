package sort;

import objects.*;
import java.util.*;


public final class Sort {
	
	//private constructor
	private Sort(){}
	
	//This method solely use with collidedWithInit and collidedWithFin to transfer correct momentum
	//for colliding circles. Smallest distance means the first shape collided with that second shape
	//first. This is a naive way of implementing which circle collides first.
	//private List<Float> sortMinToMax(List<Float> arr,List<Circle> circles) // for debugging
	public static List<Shape> sortMinDistToMaxDist(List<Shape> arr){
		
		Shape tempShape = null;
		
		for(int i = 0; i < arr.size();i++){
			for(int j = 0; j < arr.size();j++){
			
				if( arr.get(i).getColDist() < arr.get(j).getColDist() ){
				
					tempShape = arr.get(i);
					arr.set(i,arr.get(j));
					arr.set(j,tempShape);
				
				}
			
			}
		}
		
		return arr;
	}
	
	//This method sorts the nearest intersection of shapes in the entire program
	public static List<Shape> sortNearestShape(List<Shape> arr){
		
		Shape tempShape = null;
		
		for(int i = 0; i < arr.size();i++){
			for(int j = 0; j < arr.size();j++){
				
				Circle c1 = (Circle) arr.get(i);
				Circle c2 = (Circle) arr.get(j);
				
				if( c1.getColDat().getColDist() < c2.getColDat().getColDist() ){
				
					tempShape = arr.get(i);
					arr.set(i,arr.get(j));
					arr.set(j,tempShape);
				
				}
			
			}
		}
		
		return arr;
		
	}
	
	//Sort from smallest centerY to highest centerY. small centerY
	//means a circle is near the top side of the screen
	public static Circle[] sortMinCenterYToMaxCenterY(Circle[] arr){
		
		Circle tempCirc = null;
		
		for(int i = 0; i < arr.length;i++){
			for(int j = 0; j < arr.length;j++){
			
			if(arr[i].getCenterY() < arr[j].getCenterY()){
				
				tempCirc = arr[i];
				arr[i] = arr[j];
				arr[j] = tempCirc;
				
			}
			
		}}
		
		return arr;
		
	}
	
	
	public static float[] sortMinToMax(float[] arr){
		
		float tempVal = 0;
		
		for(int i = 0; i < arr.length;i++){
			for(int j = 0; j < arr.length;j++){
			
			if(arr[i] < arr[j]){
				
				tempVal = arr[i];
				arr[i] = arr[j];
				arr[j] = tempVal;
				
			}
			
		}}
		
		return arr;
		
	}


}