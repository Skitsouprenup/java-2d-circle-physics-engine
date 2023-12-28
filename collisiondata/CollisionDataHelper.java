package collisiondata;

import objects.*;
import sort.Sort;

public final class CollisionDataHelper {
	
	//private constructor
	private CollisionDataHelper(){}
	
	public static boolean compareShapesType(Shape shp1, Shape shp2) {
		return shp1.getShapeType().equals(shp2.getShapeType());
	}
	
	//check if all shapes in shapes varargs have the same shape type
	public static boolean multShapesType(ShapeType type,Shape... shapes) {
		boolean result = true;
		
		for(int i = 0; i < shapes.length; i++) { 
			if(!shapes[i].getShapeType().equals(type)) {
				result = false;
				break;
			}
		}
		
		return result;
		
	}
	
	//set the nearest collidee shape to the collider shape
	//in the initial check collision phase
	public static void setMinDistInit(Shape shape) {
		
		switch(shape.getShapeType()) {
			
				case CIRCLE:
				Circle circ = (Circle) shape;
				
				circ.getColDat().setColDat(
					(Circle) Sort.sortMinDistToMaxDist(circ.collidedWithInit).get(0) );
				break;
				
				default:
				System.err.println("Invalid Shape!!");
				break;
			
			}
		
	}
	
}