package collisionalgorithm;

import objects.*;
import math.physicsmath.*;
import math.TrigMath;
import main.*;

public abstract class CollisionAlgorithmHelper {
	
	//[0] checks if an object hits a horizontal(y) or vertical(x) wall or both.
	//[1] checks if an object hits the vertical(x) wall
	//[2] checks if an object hits the horizontal(y) wall
	private boolean[] screenCol;
	
	//stores x and y differences between two circles and 
	//the hypotenuse from the result of pythagoras theorem
	//[0] x
	//[1] y
	//[2] distance(hypotenuse)
	private float[] circleTrig;
	
	//Default constructors are automatically called by
	//the subclass
	CollisionAlgorithmHelper(){
		screenCol = new boolean[3];
		circleTrig = new float[3];
	}
	
	//collision interaction between circle and wall
	void screenCollision(Circle circle,boolean applyMomentum,boolean applyFriction){
		
		  float circEndX = circle.getPosX() + circle.getWidth();
		  float circEndY = circle.getPosY() + circle.getHeight();
		  int frameWidth = PhysicsEngine.getFrameWidth();
		  int frameHeight = PhysicsEngine.getFrameHeight();
		  boolean yAxCollision = false,xAxCollision = false;
		  
		  //if(circle.collidedWith == 2) System.out.println("(2)Before wall Collision: " + circle.posX + " " + circle.posY + " " + colWallPoint[1] + " " + colWallPoint[2]);
		  
		  //this condition checks if circle does collide with right,left of bottom wall
		  //this checks if circle collides with the bottom wall
		  if(circEndY > frameHeight){
			  circle.setPosY(frameHeight - circle.getHeight());
			  yAxCollision = true;
		  }
		  //this checks if circle collides with the right wall
		  if(circEndX > frameWidth){
			  circle.setPosX(frameWidth - circle.getWidth());
			  xAxCollision = true;
		  }
		  //this checks if circle collides with the left wall
		  else if(circle.getPosX() < 0){
			  circle.setPosX(0f);
			  xAxCollision = true;
		  }
		  //remember to compute the circle's center-point every-time we change its
		  //position
		  circle.computeCenterPoint();
		  //if(circle.collidedWith == 2) System.out.println("(2)After wall Collision: " + circle.posX + " " + circle.posY + " " + colWallPoint[1] + " " + colWallPoint[2]);
		  
		  //velocity due to gravity should be canceled when an object collided with
		  //the floor because of the normal force due to gravity that the floor
		  //exerted to an object. Thus, canceling the force of gravity.
		  //if(yAxCollision) circle.v_fin_y = circle.v_fin_y - getGravity() * get_dt();
		  
		  
		  
		  //Here we used the force of gravity as our normal force in the friction of x axis. Why?
		  //Because when the circle makes contact with the ground, the circle will slide a bit
		  //to the ground. The friction that we want to apply to the circle is a static friction
		  //because the ball will just slide a bit in order to budge the static friction of the
		  //ground. The y-axis won't be affected in this scenario because the sliding of the
		  //ball to the ground is completely horizontal. If the ball bounces on an inclined plane
		  //then both axes will be affected by the friction and also the angle of the ball
		  //
		  //Also, we can apply the friction to y-axis. Our normal force here is the force of impact. i.e
		  //when the ball hits the wall. Why? because when the ball hits the wall it will vertically
		  //slide a bit to the wall.Again, we need to get the static friction of the ball to the 
	      //wall here because the ball won't slide continuously it will slide a bit to budge the static
		  //friction of the ball to the wall. the friction of the wall to the ball based on the force
		  //of impact at 0.016(sec) time-step is very small. So, we don't need to worry about the ball
		  //suddenly lose all of its y-velocity and sticks on the wall. Also, gravity won't let that
		  //happen.
		  //
		  //Friction between circle and the sides of the screen
		  //coefficient of friction ranges from 0 to 1. 1 is the
		  //highest cof and 0 is the lowest cof. The higher the cof, the harder
		  //the object to be pushed/pulled. The lower the cof, the easier
		  //the object to be pushed/pulled.
		  float friction = 0f;
		  
		  //if circle collides with bottom wall, apply momentum in the y direction of the circle
		  if(yAxCollision && applyMomentum)
			  circle.setVelY( PhysicsMath.applyImpulse( PhysicsMath.getWallMass(),
		                      circle.getMass(),circle.getVelY(),circle.get_wcor() ) );
			
			if(yAxCollision && applyFriction){
			
		    friction = PhysicsMath.frictionForce(circle.getMass(),circle.getVelX(),
		                                 circle.getTotForceY(), 0.20f);
			
			
		    if(Math.abs(circle.getVelX()) > Math.abs(friction)) circle.setVelX(circle.getVelX() - friction);
		    else circle.setVelX(0f);
			}

		  
		  
		   //if circle collides with right or left wall, apply momentum in the x direction of the circle
		  if(xAxCollision)
			  circle.setVelX( PhysicsMath.applyImpulse( PhysicsMath.getWallMass(),circle.getMass(),
		                                                     circle.getVelX(),circle.get_wcor() ) );
			
			if(xAxCollision && applyFriction){
			
		    friction = PhysicsMath.frictionForce(circle.getMass(),circle.getVelY(),
		                                 Math.abs(PhysicsMath.forceOfImpact( circle.getMass(), 
										                                     circle.get_fin_x(),
																             circle.getVelX() )), 0.20f);
			 
			if(Math.abs(circle.getVelY()) > Math.abs(friction)) circle.setVelY( circle.getVelY()-friction );
		    else circle.setVelY(0);
			}
			
		 
		 
		 circle.updateInitFinVel();
		 //[0]: result if there's a collision
		 //[1]: result if an object collides in x(vertical) screen edge
		 //[2]: result if an object collides in y(horizontal) screen edge
		 screenCol[0] = xAxCollision | yAxCollision;
		 screenCol[1] = xAxCollision;
		 screenCol[2] = yAxCollision;
		 
	}
	
	//parameters: (colliderCircle,collideeCircle)
	void computeCircIntersection(Circle c1,Circle c2) {
				 
		  //total radius between two circles
		 float totalRadius = c1.getRadius() + c2.getRadius();
		    
		 //get the distance(hypotenuse) and x and y difference of the circles
		 TrigMath.pythagorasTheoremII(circleTrig,c1.getCenterX(),c1.getCenterY(),
											   c2.getCenterX(),c2.getCenterY());
			
		 //if distance < total radius, it means the two circles intersects(collides)
		 if(circleTrig[2] < totalRadius) {
				
			c2.set_xDiff(circleTrig[0]);
			c2.set_yDiff(circleTrig[1]);
			c2.setColDist(circleTrig[2]);
			c2.setTotalRadius(totalRadius);
			//Store shapes that collided with the first circle
			c1.collidedWithInit.add(c2);
			
			}
		
	}		
	
	void resetScreenCol() {
	  screenCol[0] = false;
	  screenCol[1] = false;
	  screenCol[2] = false;
	}
	
	boolean[] getScreenCol(){ return screenCol; }
	boolean getScreenCol(int index) { return screenCol[index]; }
	
	
}	