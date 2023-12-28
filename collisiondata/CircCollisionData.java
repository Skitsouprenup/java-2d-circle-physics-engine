package collisiondata;

import objects.Circle;

public class CircCollisionData extends CollisionData {
	
	//stores the two collided circles x and y differences between their centers
	private float xDiff,yDiff;
	//total radius between two circles
	private float totalRadius;
	//This holds the sine(theta) cosine(theta) function result in a circle collision
	//separation
	//[0] cosine
	//[1] sine
	private float[] cossin;
	
	public CircCollisionData(){ cossin = new float[2]; }
	
	public void setColDat(Circle circle) {
		
		this.xDiff = circle.get_xDiff();
		this.yDiff = circle.get_yDiff();
		this.totalRadius = circle.getTotalRadius();
		super.setColDat(circle);
		
	}
	
	@Override
	public void defaultValues(boolean delBackRef){
		
		xDiff = 0f;
		yDiff = 0f;
		totalRadius = 0f;
		cossin[0] = 0f;
		cossin[1] = 0f;
		super.defaultValues(delBackRef);
		
	}
	
	public void setCossin(float cosine,float sine){
		
		cossin[0] = cosine;
		cossin[1] = sine;
		
	}
	
	//xDiff and yDiff getters
	public float get_xDiff(){ return xDiff; }
	public float get_yDiff(){ return yDiff; }
	//totalRadius getter
	public float getTotalRadius(){ return totalRadius; }
	//cossin getters
	public float getCosine(){ return cossin[0]; }
	public float getSine(){ return cossin[1]; }
	
}