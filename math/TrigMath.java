package math;

import objects.*;


public final class TrigMath {
	
	//private constructor
	private TrigMath(){}
	
	//calculate the total distance(hypotenuse) of x and y between two points
	//in the case of circle shapes those two points are the center of each circle
	//this method returns the distance(hypotenuse) between two points
	//parameters: shape(1) x-coordinates,shape(1) y-coordinates,shape(2) x-coordinates,
				//shape(1) y-coordinates
	public static float pythagorasTheorem(float s1PointX,float s1PointY,float s2PointX,float s2PointY) {
	  
	  float xSide = s1PointX - s2PointX;
	  float ySide = s1PointY - s2PointY;
	
	  xSide = xSide * xSide;
	  ySide = ySide * ySide;
	  
	  return (float) Math.sqrt(xSide + ySide);
	
	}
	
	//calculate the total distance(hypotenuse) of x and y between two points
	//in the case of circle shapes those two points are the center of each circle
	//this method returns the distance(hypotenuse) between two points and the differences
	//of their x's and y's
	//parameters: result destination, shape(1) x-coordinates,shape(1) y-coordinates,shape(2) x-coordinates,
				//shape(1) y-coordinates
	public static void pythagorasTheoremII(float[] dest,float s1PointX,float s1PointY,float s2PointX,float s2PointY) {
	  
	  //Since we're returning xSide and ySide here without squaring. We need to remove their
	  //negative signs by using Math.abs(). We need to remove the negative sign because we want a
	  //positive angle when we use these informations to get the angle that we need using Math.atan2()
	  float xSide = Math.abs(s1PointX - s2PointX);
	  float ySide = Math.abs(s1PointY - s2PointY);
	  
	  dest[0] = xSide;
	  dest[1] = ySide;
	  dest[2] = (float) Math.sqrt((xSide * xSide) + (ySide * ySide));
	
	}
	
	
	//circle1 subject(collider) of collision, circle2 object(collidee) of collision
	public static void circColSeparation(float adjacent, float opposite, float distance, float totalRadius, Circle circle1, Circle circle2){
		//if(c1Index == 1 || c1Index == 2) System.out.println("Before: total KE: " + (circle.kex + circles[i].kex));
			  
		//get the reference angle between hypotenuse and adjacent(x) using arc tangent
	    //that takes opposite/adjacent values
  	    float angle = (float)Math.atan2(opposite,adjacent);
		
		float cosx = (float)Math.cos(angle);
		float siny = (float)Math.sin(angle);
		
  	    //get the overlap length of intersection between two circles by subtracting
	    //total radius and total distance
	    float moveDistance = totalRadius - distance;
		
		//we need to assign a sign to the product of our move distance based on the position
		//of circle's center. If circle1 centerX is
		//greater than circle2 centerX then circle1 is on the right side of circle2 and
		//right side direction in this system is positive. So, move distance in x direction
		//of circle1 is positive. Otherwise, move distance of circle1 is negative.
		//Same goes with y direction. If circle1.centerY is greater than circle2.centerY
		//circle1 is on the bottom side of circle2. bottom side direction in this system is
		//positive. Thus, circle1 move distance in y is positive. Otherwise circle1 move
		//distance is negative
		//if centerX or centerY of both circles are equal then move the circles in positive
		//direction. It is nearly impossible to have both centerX and centerY equal at the
		//same time in both circles. If they do, it means that their center are exactly close.
		int xSign = 1;
		int ySign = 1;
		if(circle1.getCenterX() >= circle2.getCenterX()) xSign = 1;
		else xSign = -1;
		
		if(circle1.getCenterY() >= circle2.getCenterY()) ySign = 1;
		else ySign = -1;
		
	    //update first circle by adding the opposite and adjacent side of the overlap length
	    //of intersection between two circles to the current x and y of the first circle.
	    //Since, we want two circles to move away from each other we need to distribute the 
		//opposite and adjacent side of the overlap. We can divide it by 2 or multiply by
		//0.5 to cut the overlap values to half. I multiply the values by 0.5 because
		//multiplication is faster than division on computers
		circle1.setPosX(circle1.getPosX() + cosx * moveDistance * xSign * 0.5f);
		circle1.setPosY( circle1.getPosY() + siny * moveDistance * ySign * 0.5f );
			  
		//the direction of the overlap values should be opposite from the direction of
		//the overlap values that is applied to the first circle. So, we need to negate
		//xSign and ySign
		circle2.setPosX(circle2.getPosX() + cosx * moveDistance * -xSign * 0.5f);
		circle2.setPosY(circle2.getPosY() + siny * moveDistance * -ySign * 0.5f);
			  
		//remember to compute the circle's center-point every-time we change its
		//position
		circle1.computeCenterPoint();
		circle2.computeCenterPoint();
		
		circle1.getColDat().setCossin(cosx, siny);
	
	}
	
}