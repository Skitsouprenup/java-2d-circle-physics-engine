package objects;

import math.physicsmath.PhysicsMath;
import collisiondata.CircCollisionData;

public class Circle extends Shape
{
	
	//circle's radius
	private float radius;
	
	//stores the two collides circles x and y differences between their centers
	//should be stored in the collidee circle
	private float xDiff,yDiff;
	////total radius between two circles. Should be stored in the collidee circle
	private float totalRadius;
	//Collision data consists of intersection data and others with the
	//nearest shape to the collider. Should be stored in collider shape
	//we need this because colDist in Shape class can modified multiple
	//times during initial collision phase
	private CircCollisionData circColDat;
	
	public Circle(int id,float posX,float posY,float width,float height,float velX,float velY,float mass)
	{
		super(id,posX,posY,width,height,velX,velY,mass);
		
		//either posX/2 or posY/2 is fine because we're dealing
		//with circle in which the radii in all parts of circle
		//are equal
		radius = width/2;
		
		setShapeType(ShapeType.CIRCLE);
		wcor = 0.8f;
		bcor = 0.9f;
		
		computeCenterPoint();
		circRotInertia();
		
		circColDat = new CircCollisionData();
	}
	
	//radius getter
	public float getRadius(){ return radius; }
	
	//xDiff and yDiff getters
	public float get_xDiff(){ return xDiff; }
	public float get_yDiff(){ return yDiff; }
	//xDiff and yDiff setters
	public void set_xDiff(float xDiff){ this.xDiff = xDiff; }
	public void set_yDiff(float yDiff){ this.yDiff = yDiff; }
	//totalRadius getter and setter
	public float getTotalRadius(){ return totalRadius; }
	public void setTotalRadius(float totalRadius){ this.totalRadius = totalRadius; }
	//colDat getter
	public CircCollisionData getColDat() { return circColDat; }
	
	@Override
	public void computeCenterPoint() {
		setCenterX(getPosX() + getWidth()*0.5f);
		setCenterY(getPosY() + getHeight()*0.5f);
	}
	
	@Override
	void regularMovement(){
	
	   addDistanceX(getVelX());
	   addDistanceY(getVelY());
	   computeCenterPoint();
	   rotateCircle();
	}
	
	
	//Second moment of inertia or rotational inertia of a solid sphere and
	//the axis of rotation is in the centroid(center): 2/5*mr^2
	private void circRotInertia(){
		//Dont forget to convert the radius to meters first
		//remember our scale: 1meter = 75pixels
		//So, radius-in-pixels * 1meter/75pixels
		float radiusToMeter = radius/75;
		setRotInertia(2*getMass()*(radiusToMeter*radiusToMeter)/5);
	}
	
	void rotateCircle(){
		
		//System.out.println("Radians: " + angVel);
		float angVelDeg = (float)Math.toDegrees(getAngVel());
		if(angVelDeg > 360) setAngVel((float)Math.toRadians(angVelDeg - 360f));
		//I use plus here to make sure that I'm subtracting 'cause
		//angVelDeg in this condition is always negative
		else if(angVelDeg < 0) setAngVel((float)Math.toRadians(360f + angVelDeg));
		//System.out.println("Degrees: " + angVelDeg);
		addAngVel(PhysicsMath.noSlipRoll(getVelX(),radius));
	
	}
	
}