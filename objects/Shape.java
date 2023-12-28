package objects;

import math.physicsmath.PhysicsMath;
import collisiondata.CollisionData;
import java.util.*;

public abstract class Shape {
	
	private int id;
	
	private String shapeName;
	private ShapeType shapeType;
	
	//This toggle toggles the ability of a shape to be a collider.
	//There are two components when a collision between two shapes happen
	//the collider and the collidee. The collider is the shape that is
	//going to be checked if it collided with another shape. The collidee
	//shape is the shape that collided to the collider shape during checking.
	private boolean colliderToggle;
	
	//Store distance of two shapes that collided and should be stored to collidee shape.
	//The relationship is many-to-one relationship. multiple collidee shapes that
	//is collided to a collider shape.
	private float colDist;
	
	//Position
	private float posX;
	private float posY;
	
	//size
	private float width;
	private float height;
	
	//Velocity m/s
	private float velX;
	private float velY;
	
	private float angDisp; //angular displacement //in radians
	private float angVel;
	private float angAccel;
	
	//circle's centerpoint
	private float centerX;
	private float centerY;
	
	//mass
	private float mass; //kg
	
	//acceleration
	private float acceleration_x;
	private float acceleration_y;
	
	private float rotInertia;
	
	//energy
	private float kex; //kinetic energy in x axis
	private float key; //kinetic energy in y axis
	private float keRot; //rotational kinetic energy
	
	//initial and final velocity per time-step
	private float v_init_x;
	private float v_fin_x;
	private float v_init_y;
	private float v_fin_y;
	
	//Force of gravity on object
	private float fog;
	//total x and y force accumulated including collision newton's 3rd law, force of gravity,
	//air resistance,etc.
	private float totForceX;
	private float totForceY;
	
	//collections of indexes where this shape collided which is another
	//shape that is closest to this shape during initial collision phase.
	//This phase collects every shapes that collided near this shape. This
	//list is in arraylist form because we will rearrange this based on the 
	//intersection(collision) distance between shapes
	public java.util.List<Shape> collidedWithInit;
	//collections of indexes where this shape collided which is another
	//shape that is closest to this shape during final collision phase.
	//This phase collects every shapes that collided near this shape that
	//are verified to be the nearest shape in each collision phase instance.
	//This list is in linkedlist because we will just access this list
	//sequentially
	public java.util.List<Shape> collidedWithFin;
	
	//coefficient of restitution between shape and wall
	protected float wcor;
	//coefficient of restitution between two shapes with same material
	protected float bcor;
	
	Shape(int id,float posX,float posY,float width,float height,float velX,float velY,float mass) {
		
		this.id = id;
		
		this.posX = posX;
		this.posY = posY;
		
		this.width = width;
		this.height = height;
		
		this.velX = velX;
		this.velY = velY;
		
		this.mass = mass;
		
		colliderToggle = true;
		
		fog = mass * PhysicsMath.getGravity();
		
		//assign the force of gravity as net force in y direction
		//this implementation is subject to improvement in the future
		//for improvement, we can assign multiple forces not only fog
		accumulateForceY(fog);

		setAccelX(totForceX);
		setAccelY(totForceY);
		
		collidedWithInit = new ArrayList<Shape>();
		collidedWithFin = new LinkedList<Shape>();
	}
	
	//id getter
	public int getId(){ return id; }
	//shape name setter and getter
	public String getShapeName(){ return shapeName; }
	//protected void setShapeName(String shapeName){ this.shapeName = shapeName; }
	//Shape type setter and getter
	public ShapeType getShapeType(){ return shapeType; }
	protected void setShapeType(ShapeType shapeType){ this.shapeType = shapeType; }
	//centerColDist getter and setter
	public float getColDist(){ return colDist; }
	public void setColDist(float colDist){ this.colDist = colDist; }
	//Collider Toggler
	public void colliderToggleOn(){ colliderToggle = true; }
	public void colliderToggleOff(){ colliderToggle = false; }
	//ColliderToggle getter
	public boolean getColliderToggle(){ return colliderToggle; }
	
	//mass getter
	public float getMass(){ return mass; }
	
	//update the center point of an object
	public void computeCenterPoint(){ /*reserve for polygonal centerpoint computation*/ }
	
	//center x and y getters
	public float getCenterX(){ return centerX; }
	public float getCenterY(){ return centerY; }
	//center x and y setters
	protected void setCenterX(float centerX){ this.centerX = centerX; }
	protected void setCenterY(float centerY){ this.centerY = centerY; }
	
	
	
	//formula: a = f/m
	//I didn't use the formula a = Δv/Δt because the velocity is not
	//given. I use a=f/m because mass and force is given in our program.
	//compute x and y acceleration
	public void setForceToAccel(float[] nForceX,float[] nForceY)
	{
		//set acceleration(x or y or both) to zero if there's no friction that will stop an object from moving
		
		float[] net_force = PhysicsMath.computeForce(nForceX,nForceY);
		
		acceleration_x = PhysicsMath.forceToAccel(net_force[0],mass);
		acceleration_y = PhysicsMath.forceToAccel(net_force[1],mass);
		
		//System.out.println("accelerationX: " + acceleration_x + " accelerationY: " + acceleration_y);
	}
	
	//compute force x acceleration x
	public void setForceToAccelX(float[] nForceX) {
		
	   float net_force = PhysicsMath.computeForceX(nForceX);
	   acceleration_x = PhysicsMath.forceToAccel(net_force,mass);
	
	}
	
	//compute force y acceleration y
	public void setForceToAccelY(float[] nForceY) {
	
	   float net_force = PhysicsMath.computeForceY(nForceY);
	   acceleration_y = PhysicsMath.forceToAccel(net_force,mass);
	
	}
	
	//compute individual force x to acceleration x
	public void setForceToAccelX(float forceX){ acceleration_x = PhysicsMath.forceToAccel(forceX,mass); }
	//compute individual force y to acceleration y
	public void setForceToAccelY(float forceY){ acceleration_y = PhysicsMath.forceToAccel(forceY,mass); }
	
	//add acceleration x to velocity x
	public void addAccelXtoVelX(float acceleration_x) { velX += PhysicsMath.accelToVel(acceleration_x); }
	//add acceleration y to velocity y
	public void addAccelYtoVelY(float acceleration_y) { velY += PhysicsMath.accelToVel(acceleration_y); }
	//add velocity x to velocity x
	public void addVelX(float velX){ this.velX += velX; }
	//add velocity y to velocity y
	public void addVelY(float velY){ this.velY += velY; }
	
	//compute x distance
	public void addDistanceX(float velocity_x) { posX += PhysicsMath.velToDist(velocity_x); }
	//compute y distance
	public void addDistanceY(float velocity_y) { posY += PhysicsMath.velToDist(velocity_y); }
	
	//accumulate total force
	public void accumulateForceX(float forceX){ totForceX += forceX; }
	public void accumulateForceY(float forceY){ totForceY += forceY; }
	//reset total force
	public void resetTotForceX(){ totForceX = 0f; }
	public void resetTotForceY(){ totForceY = 0f; }
	//get total force
	public float getTotForceX(){ return totForceX; }
	public float getTotForceY(){ return totForceY; }
	
	//x and y acceleration getters
	public float getAccelX(){ return acceleration_x; }
	public float getAccelY(){ return acceleration_y; }
	//x and y acceleration setters
	public void setAccelX(float accelX){ acceleration_x = accelX; }
	public void setAccelY(float accelY){ acceleration_y = accelY; }
	
	//x and y velocity setters
	public void setVelX(float velX){ this.velX = velX; }
	public void setVelY(float velY){ this.velY = velY; }
	//x and y velocity getters
	public float getVelX(){ return velX; }
	public float getVelY(){ return velY; }
	
	//x and y position setters
	public void setPosX(float posX){ this.posX = posX; }
	public void setPosY(float posY){ this.posY = posY; }
	//x and y position getters
	public float getPosX(){ return posX; }
	public float getPosY(){ return posY; }
	
	//translational kinetic energy getters
	public float get_kex(){ return kex; }
	public float get_key(){ return key; }
	//translational kinetic energy setters
	public void set_kex(float kex){ this.kex = kex; }
	public void set_key(float key){ this.key = key; }
	
	//angVel getter
	public float getAngVel(){ return angVel; }
	//angVel setter
	protected void setAngVel(float angVel){ this.angVel = angVel; }
	//angVel increment/decrement
	protected void addAngVel(float angVel){ this.angVel += angVel; }
	
	//rotInertia getter
	public float getRotInertia(){return rotInertia;}
	//rotInertia setter
	protected void setRotInertia(float rotInertia){ this.rotInertia = rotInertia; }
	
	//force of gravity(fog) getter
	public float get_fog(){ return fog; }
	
	//width and height getters
	public float getWidth(){ return width; }
	public float getHeight(){ return height; }
	
	//coefficient of restitution of ball-to-wall(wcor) and
	//ball-to-ball(bcor) getters
	public float get_wcor(){ return wcor; }
	public float get_bcor(){ return bcor; }
	
	//initial and final velocity getters
	public float get_init_x(){ return v_init_x; }
	public float get_init_y(){ return v_init_y; }
	public float get_fin_x(){ return v_fin_x; }
	public float get_fin_y(){ return v_fin_y; }
	
	//Set objects position with gravity involved
	public void setGenPos(){
		addAccelXtoVelX(getAccelX());
		addAccelYtoVelY(getAccelY());
		colliderToggleOn();
		regularMovement();
	}
	
	
	void regularMovement(){
	
	   addDistanceX(velX);
	   addDistanceY(velY);
	   computeCenterPoint();
	   
	}
	
	
	//update the initial(before current) velocity and
	//final(current) velocity
	//Note: v_fin_x and velX are equal. Same goes for v_fin_y and velY.
	//v_fin_x and v_fin_y are just a storage of final velX and final velY during collision.
	//velX and velY are much more general because these variables holds the
	//actual object's velocity. Since velX does not change during computation v_fin_x stores
	//the current value of velX. Same goes to v_fin_y
	public void updateInitFinVel(){
	
	    v_init_x = v_fin_x;
		v_fin_x = velX;
		
		v_init_y = v_fin_y;
		v_fin_y = velY;
	
	}
	
}