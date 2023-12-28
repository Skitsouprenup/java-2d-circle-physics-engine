package math.physicsmath;

import objects.*;
import main.*;

public final class PhysicsMath {
	
	//private constructor
	private PhysicsMath(){}
	
	//m/s^2 -> force acceleration. Force of gravity = mg -> mass * force acceleration
	private static float gravity = 9.81f; //force acceleration
	
	//mass of the wall on the left,right and bottom
	private static float wallMass = 5000;
	
	public static float getGravity(){ return gravity; }
	public static float getWallMass(){ return wallMass; }
	
	//compute the x and y translational kinetic energy of an object
	public static void computeKineticEnergy(Shape shape)
	{
		shape.set_kex( 0.5f*shape.getMass()*( shape.getVelX()*shape.getVelX() ) );
		shape.set_key( 0.5f*shape.getMass()*( shape.getVelY()*shape.getVelY() ) );
	}
	
	
	//nForce = Net Force
	//unit: kg.m/s^2 or Newton(N)
	//direction sign: right-down -> positive, left-up -> negative
	//compute x and y net force
	public static float[] computeForce(float[] nForceX,float[] nForceY)
	{
		float net_force_x = 0;
		float net_force_y = 0;
		for(int i = 0; i < nForceX.length;i++) net_force_x += nForceX[i];
		for(int i = 0; i < nForceY.length;i++) net_force_y += nForceY[i];
        return new float[]{net_force_x,net_force_y};		
	}
	
	//compute x net force
	public static float computeForceX(float[] nForceX) {
	
	    float net_force_x = 0f;
		for(int i = 0; i < nForceX.length;i++) net_force_x += nForceX[i];
		return net_force_x;
	
	}
	
	//compute y net force
	public static float computeForceY(float[] nForceY) {
	
	    float net_force_y = 0f;
		for(int i = 0; i < nForceY.length;i++) net_force_y += nForceY[i];
		return net_force_y;
	
	}
	
	//F = ma
	//This method is usable if you only have a single force acting on an
	//object
	public static float computeForce(float mass,float acceleration) {
	  
	  float force = mass*acceleration;
	  return force;
	
	}
	
	//F = m.Δv/t or f = Δp/t
	//return the force magnitude of an individual force. It means the returned force is always positive
	//This method is usable when dealing with momentum problems
	public static float computeForce(float mass,float vfin,float vinit) {
	   
	  //System.out.println("init: " + vinit + " fin: " + vfin + " sum: " + (Math.abs(vfin)-Math.abs(vinit)));
	  float force = mass * (Math.abs(vfin)-Math.abs(vinit))/PhysicsEngine.get_dt();
	  return force;
	
	}
	
	//convert force to acceleration(avg)
	//parameter: overall force that acted on object, mass of an object
	public static float forceToAccel(float[] force,float mass) {
		
		float nForce = 0f;
		
		for(int i = 0; i < force.length;i++) nForce += force[i];
		
		return nForce/mass;
		
	}
	//convert force to acceleration(avg)
	//parameter: overall(summed) force that acted on object, mass of an object
	public static float forceToAccel(float force,float mass) { return force/mass; }
	
	//convert acceleration to velocity(avg)
	public static float accelToVel(float accel) {return accel * PhysicsEngine.get_dt();}
	
	//convert velocity to Displacement(avg)
	//Don't forget to convert meters to pixels
	//my custom scale: 75pixels/meter
	public static float velToDist(float vel){return vel * PhysicsEngine.get_dt() * 75;}
	
	//friction force(static)
	//Note: This method converts friction force to velocity
	public static float frictionForce(float objMass,float objVel, float fn, float cof) {
		//friction force formula: Ff = Fn*cof
		//Ff = Friction force, Fn = normal force, cof = coefficient of friction
		
		float frictionVel = 0f;
		
		float frictionForce = Math.abs(fn) * cof;
		float frictionAccel = frictionForce/objMass;
		frictionVel += frictionAccel * PhysicsEngine.get_dt();
		
		//When the velocity of the circle is positive, we
		//need to make the friction positive because we're using
		//- operator to reduce the velocity after the collision and we want the
		//friction to be on the opposite side of the velocity of the object.
		//So, v - f = v + -f When the the velocity of the
		//circle is negative make the friction negative. So, -v - -f = -v + f
		if(objVel > 0) frictionVel = frictionVel;
		else if(objVel < 0) frictionVel = -frictionVel;
		else if(objVel == 0) frictionVel = 0;
		
		return frictionVel;
	}
	
	//Formula F = m*Δv/t or F = Δp/t
	//This method gets the magnitude of the force not the direction. Since, I'm using
	//this method to get the normal force for impact friction of the ball when colliding
	//with the wall the frictionForce method will get the normal(reaction) force of this
	//force. Force of impact varies during the time of collision. The equation here just
	//solve the average force of impact in due time
	public static float forceOfImpact(float mass,float vinit,float vfin) {
	
	  return mass*(Math.abs(vfin)-Math.abs(vinit))/PhysicsEngine.get_dt();
	
	}
	
	//This is used to compute the normal force that is perpendicular to a tangential(line that only touches a single point
	//on a circle's edge) line of a circle that is also included the sliding force between two spheres when a sphere is
	//on top of another spheres. 
	public static void SphereRollOnSphere(float cosx,float siny,Circle circle1,Circle circle2) {
		
		//we need to make sure that the normal force of two circles are not perpendicular to the force of gravity.
		//Otherwise, we can't break down the force of gravity into components because gravity doesn't act completely
		//to perpendicular direction(left or right. In this system gravity acts downward). We can check if the normal force
		//on both circles are perpendicular or not by checking the value of cos(angle). If the value is 1 then
		//normal force is perpendicular to fog. otherwise, it's parallel or inclined to fog. It's alright
		//if normal force is parallel to gravity, that means normal force and fog are in the same axis.
		if(cosx != 1) {
		
		int xSign = 1;
		int ySign = 1;
		//We need the force of the first circle because in our system we know that the first circle is on top
		//of the second circle and the first circle collides with the second circle because our program
		//has gravity. So, we just need to get the force of the first circle applied to the second circle
		//and the reaction force of the second circle to the first circle.
		float c1TotForceY = accelToVel(forceToAccel(circle1.getTotForceY(),circle1.getMass()));
		
		if(circle1.getCenterX() >= circle2.getCenterX()) xSign = 1;
		else xSign = -1;
		
		if(circle1.getCenterY() >= circle2.getCenterY()) ySign = 1;
		else ySign = -1;
		
		circle1.addVelX(cosx*c1TotForceY*xSign);
		circle1.addVelY(siny*c1TotForceY*ySign);
		
		circle2.addVelX(cosx*c1TotForceY*-xSign);
		circle2.addVelY(siny*c1TotForceY*-ySign);
		
		//When you change circle's velocity be sure to update v_fin_x and v_fin_y by invoking updateInitFinVel()
	    circle1.updateInitFinVel();
	    circle2.updateInitFinVel();
				
	    //Also compute circle's kinetic energy when circle's velocity change
	    computeKineticEnergy(circle1);
	    computeKineticEnergy(circle2);
		
		}
		
	}
	
	//Formula: ω = v(translational)/r
	//units radians/s
	//Note: radians is a dimensionless unit so v/r = m/s * 1/m = s = radians/s
	//reference: https://math.stackexchange.com/questions/803955/why-radian-is-dimensionless
	//This method will relate translational velocity x(or y) to angular velocity
	//v_fin_n_conserve is the final velocity where friction and energy loss is
	//applied during collision
	public static float noSlipRoll(float velocity, float radius){
		
		//Don't forget to convert the radius to meters first
		//remember our scale: 1meter = 75pixels
		//So, radius-in-pixels * 1meter/75pixels
		float radiusToMeter = radius/75;

		//cut velocity by fps. We want a velocity per frame which is equivalent to 16.6666667 milliseconds
		//'cause our velocity is for example 3meters/second, we want to divide that to milliseconds
		//So, what we can do is to convert that milliseconds per frame to seconds then multiply to the velocity
		//just like we the equation for distance or divide the velocity by frame per second. Either 
		//methods are already they will come up to the same answer. I'll use the division method because
		//fps is dimensionless at least in my preference, so, the result unit is still acceptable which is m/s
		//if I use the multiplication method the result unit is gonna be meters.
		float vel = velocity/PhysicsEngine.FRAMES_PER_SECOND;
		//System.out.println(radiusToMeter + " | " + velocity + " | " + vel + " | " + (vel/radiusToMeter));
		
		return vel/radiusToMeter;
	}
	
	//conservation of momentum + coefficient of restitution(cor) derivation.
	//This equation works in elastic and inelastic collisions and also, this
	//equation simulates energy lost after collision
	
	//equation: m1v1i + m2v2i + m1e(v1i - v2i)/m1 + m2 = v2f
	//Since circle is the only object moving in this simulation we don't need
	//to get the velocity of the wall because wall doesn't move in this simulation.
	//So, we can modify our equation:
	//m2*v2i - m1*e*v2i/m1 + m2 = v2f
	//e = coefficient of restitution
	
	//modified momentum equation + cor equation for wall-circle collision
	//impulse between an object and a static wall
	public static float applyImpulse(float m1, float m2, float v2i, float e) {
	  //Object1: Wall
	  //Object2: circle
	  
	  return (m2*v2i - m1*e*v2i) / (m1 + m2); 
	
	}
	
	//impulse between two objects
	public static void applyImpulse(Shape shape1,Shape shape2){
		
		//firstObjImpulse(float m1, float v1i, float m2, float v2i, float e)
		//secondObjImpulse(float m1, float v1i, float m2, float v2i, float e)
		//compute the momentum of each shape's x and y. Also, be sure not to use
		//the result of one shape's velocity due to collision to another shape
		//
		//e.g.
		//correct
		//shape.velX = firstObjImpulse(shape.getMass(), shape.v_fin_x, shapes[i].getMass(), shapes[i].v_fin_x, shape.cor);
		//shapes[i].velX = secondObjImpulse(shape.getMass(), shape.v_fin_x, shapes[i].getMass(), shapes[i].v_fin_x, shapes[i].cor);
		//incorrect
		//shape.velX = firstObjImpulse(shape.getMass(), shape.v_fin_x, shapes[i].getMass(), shapes[i].v_fin_x, shape.cor);
		//shapes[i].velX = secondObjImpulse(shape.getMass(), shape.velX, shapes[i].getMass(), shapes[i].v_fin_x, shapes[i].cor);
		//
		//in the incorrect version, I used shape.velX in the second statement where I need to get shapes[i] velX. But shape.velX was
		//already changed during computation, So, shapes[i].velX result is going to be incorrect because we put a wrong velocity
		//which is shape.velX velocity after the collision occur. We need shape.velX value during collision not after the collision.
		//Same goes, If you happen to get shapes[i].velX first before shape.velX. That's why always use v_fin_x and v_fin_y here
		
		shape1.setVelX( PhysicsMath.firstObjImpulse( shape1.getMass(), shape1.get_fin_x(), 
				                                shape2.getMass(), shape2.get_fin_x(), shape1.get_bcor() ) );
				
		shape2.setVelX( PhysicsMath.secondObjImpulse( shape1.getMass(), shape1.get_fin_x(),
			                                    shape2.getMass(), shape2.get_fin_x(), shape2.get_bcor() ) );
													
	    shape1.setVelY( PhysicsMath.firstObjImpulse(shape1.getMass(), shape1.get_fin_y(), 
				                                shape2.getMass(), shape2.get_fin_y(), shape1.get_bcor() ) );
												
		shape2.setVelY( PhysicsMath.secondObjImpulse(shape1.getMass(), shape1.get_fin_y(), 
				                                shape2.getMass(), shape2.get_fin_y(), shape2.get_bcor() ) );
												
		//if((shape1.getCenterX() > shape2.getCenterX() && (shape1.get_fin_x() > 0 && shape2.get_fin_x() < 0)) || 
		//  (shape1.getCenterX() < shape2.getCenterX() && (shape1.get_fin_x() < 0 && shape2.get_fin_x() > 0))) shape1.setVelX(-shape1.getVelX());
		
		//When you change circle's velocity be sure to update v_fin_x and v_fin_y by invoking updateInitFinVel()
	    shape1.updateInitFinVel();
	    shape2.updateInitFinVel();
				
	    //Also compute circle's kinetic energy when circle's velocity change
	    PhysicsMath.computeKineticEnergy(shape1);
	    PhysicsMath.computeKineticEnergy(shape2);
		
	}
	
	//standard momentum equation + cor equation
	//equation: v1f = m1v1i + m2v2i + m2e(v2i - v1i)/ m1 + m2
	//This method can be used for two objects colliding
	//e = coefficient of restitution
	private static float firstObjImpulse(float m1, float v1i, float m2, float v2i, float e) {
	  
	  return ((m1*v1i + m2*v2i + m2*e*v2i - m2*e*v1i)/(m1 + m2)); 
	
	}
	
	//standard momentum equation + cor equation
	//equation: v2f = m1v1i + m2v2i + m1e(v1i - v2i)/m1 + m2
	//This method can be used for two objects colliding
	//e = coefficient of restitution
	private static float secondObjImpulse(float m1, float v1i, float m2, float v2i, float e) {
	  
	  return ((m1*v1i + m2*v2i + m1*e*v1i - m1*e*v2i)/(m1 + m2)); 
	
	}
	
}