package collisionalgorithm;

import objects.*;
import main.Singleton;
import math.TrigMath;
import math.physicsmath.PhysicsMath;
import sort.Sort;
import collisiondata.*;

import java.util.*;

//Singleton class
public final class CollisionByNearestShape extends CollisionAlgorithmHelper implements Singleton {
	
	//singleton reference
	private static CollisionByNearestShape cbns;
	
	//this collided variable is used to check if a circle already collided
	//with another circle. if so, we don't need to apply momentum on that
	//circle because the momentum of that circle in the current frame has
	//been already applied during its previous collision
	boolean collided;
	
	//this holds collider shapes that are not turned off
	private java.util.List<Shape> ableColliders;
	
	//collision chain count
	private int chainCount;
	//max collision chain
	private int maxChainCount;
	
	private CollisionByNearestShape(){
		
		collided = false;
		//set ableColliders. We will use arraylist
		//because we will access this in random access fashion
		ableColliders = new ArrayList<Shape>();
	}
	
	/*Singleton Methods*/
	public static void createInstance(){
		if(cbns != null) return;
		else cbns = new CollisionByNearestShape();
	}
	
	public static void releaseInstance(){
		if(cbns != null) cbns = null;
	}
	
	public static CollisionByNearestShape getInstance() {
		if(cbns != null) return cbns;
		else return null;
	}
	/**/
	
	//Check collision between circles
	//We will use brute force algorithm here for
	//collision checking. Brute force is a really expensive
	//algorithm if you have too many object that's colliding.
	//Since We're doing simple simulation here and our
	//colliding objects are few, brute force is enough here
	//parameter: array of circles, circle to be tested for collision,
	//index of the circle that is being tested for collision
	//
	//Note: the collision algorithm that is applied here is not perfect.
	//There are times that some objects may noticeably overlap a bit
	//from each other. But, IMO, the overlaps are tolerable at some degree
	//So, for now, I'll stay with this method. I might improve this
	//algorithm in the future.
	public boolean checkCollisions(Shape[] shapes) {
	  //condition if there are collider shapes remaining
	  //in the current check collision phase
	  boolean collidersExist = false;
	  
	  //Test shape(i) to all shapes(j) in the shape array
	  for(int i = 0; i < shapes.length;i++) {
		  
		  //if a shape collider is off it means that the shape
		  //has become a collider previously in this shape collision
		  //check phase. Thus, it can't be a collider again, so,
		  //we move on to the next shape. Otherwise, the shape collider
		  //is still active.
		  if(!shapes[i].getColliderToggle()) continue;
		  else collidersExist = true;
		  
		  for(int j = 0; j < shapes.length;j++) {
			  
			  //We don't want the shape(i) to be tested to itself. So,
			  //we need to check if the shape(j) index is not equal to
			  //shape(i). if they're equal, then it means that the shape(j)
			  //and the shape(i) are the same, so, we don't need to check if they
			  //collide because they are the same shape. Otherwise, shape(i)
			  //and shape(j) in the second parameter are different
			  //thus we can check if they collide.
			  if(shapes[i].getId() != shapes[j].getId()) {
			 
				//We need to check if these two shapes previously collided
				//if so, we don't need to compute their velocities again due
				//to collision, so, we can move to the next shape(j)
				for(int k = 0; k < shapes[i].collidedWithFin.size();k++){
				
					if(shapes[i].collidedWithFin.get(k).getId() == shapes[j].getId()){
					collided = true;
					break;
					}
			 
				}
				if(collided){ collided = false; continue; }
			  
			  }else continue;
			 
			 //if the collidee shape(j) was not in the collider shape(j)
			 //collidedWithFin and shape(i) and shape(j) are not the same
			 //then we need to check if shape(j) collide with shape(i)
			 //in this collision phase
			 
			 //Let's check if two shapes have the same shape type
			 if(CollisionDataHelper.multShapesType(ShapeType.CIRCLE,shapes[i],shapes[j])) {
				 
				 Circle colliderCirc = (Circle) shapes[i];
				 Circle collideeCirc = (Circle) shapes[j];
				 
				 computeCircIntersection(colliderCirc,collideeCirc);
				
			 }//if(1)
		 }//for loop(j)
		
		
		//Check if collidedWithInit is not empty. If it's not empty then
		//our collider shape(i) has a collidee shape/s(j)
		if(shapes[i].collidedWithInit.size() != 0) { 
			//Once we know all shapes that collided with our collider shape(i). We
		    //need to get the nearest collidee shapes(j) to our collider.
			CollisionDataHelper.setMinDistInit(shapes[i]);
			//clear collidedWithInit because we already got the nearest collidee
			//shape
			shapes[i].collidedWithInit.clear();
		}
		//add this collider shape(i) to ableColliders collection
		ableColliders.add(shapes[i]);
	 
	  }//for loop(i) end
	  
	  //Once we know all the nearest shape in every collider shape, we can arrange
	  //the entire object in our program from the nearest to farthest collision
	  //intersection between collider and collidee. First, we need to check if
	  //ableColliders is not empty. If it's empty, it means that there's no
	  //remaining collider shape, therefore, no nearest shape and chain collision
	  //will occur.
	  if(ableColliders.size() != 0) {
	  Shape nearestShape = Sort.sortNearestShape(ableColliders).get(0);
	  chainCollision(shapes,nearestShape);
	  ableColliders.clear();
	  }
	  
	  return collidersExist;
	  
	}
	
	
	private void chainCollision(Shape[] shapes, Shape colliderShape) {
	  
	  //Check the collider shape if it has collided with a wall. We want to check
	  //if maxChainCount is 0 first because it implies that we are at the starting
	  //point of a chain. The if condition here ensure us that the engine only
	  //checks the collider shape be it in a chain or not. Include friction because
	  //the collider shape is the one that hit the wall
	  if(maxChainCount == 0) screenCollision((Circle)colliderShape,true,true);
	  
	  float colDist = 0f;
	  switch(colliderShape.getShapeType()){
		  
		  case CIRCLE:
		  Circle c1 = (Circle) colliderShape;
		  colDist = c1.getColDat().getColDist();
		  break;
		  
		  default:
			System.err.println("Invalid Shape in chainCollision method!!");
		  break;
		  
	  }
	  
	  
	  //assign the collidee shape to this variable
	  Shape collideeShape = null;
	  Circle collideeCirc = null;
	  //if colDist of the collider shape is 0f, it means that this
	  //collider shape didn't collide with any collidee shape. Otherwise,
	  //collider shape has a collision with another shape
	  if(colDist != 0f) {
		if(colliderShape.getShapeType().equals(ShapeType.CIRCLE)){
		  
			//cast shape reference to circle reference
			Circle colliderCirc = (Circle) colliderShape;
			collideeShape = colliderCirc.getColDat().getShapeRef();
			
			
			if(collideeShape.getShapeType().equals(ShapeType.CIRCLE)) {
			 
				collideeCirc = (Circle) collideeShape;
		
				//collision separation between two circles
				TrigMath.circColSeparation(colliderCirc.getColDat().get_xDiff(),
										   colliderCirc.getColDat().get_yDiff(),colliderCirc.getColDat().getColDist(),
										   colliderCirc.getColDat().getTotalRadius(),colliderCirc,collideeCirc);
								   
				//Apply momentum formula for the collision of both shapes.
				PhysicsMath.applyImpulse(colliderCirc,collideeCirc);  
				//increase the force of the second circle because of the collision of the
				//first circle
				collideeShape.accumulateForceY(colliderCirc.getTotForceY()*
									           colliderCirc.getColDat().getSine());
			
				//add collider shape to collidee back reference for backward chaining
				collideeCirc.getColDat().setShapeBackRef(colliderCirc);
			}
		}else {/**/}
		
		//Since we're here in this block, It means that two shapes collided. We want to keep track
		//which shape collided with another shape. To keep track which shape collided with another
		//shape we will use a linked list. This linked list will store every index of each shape that
		//collided with the specified shape. Both shapes collided with each other, So, we need to
		//store their indexes to each shape's collidedWithFin linked list
		//colliderShape.collidedWithFin.add(collideeShape);
		collideeShape.collidedWithFin.add(colliderShape);
		maxChainCount = ++chainCount;
		
		collideeCirc.getColDat().defaultValues(false);
		
		for(int i = 0; i < shapes.length;i++){
			
			if(shapes[i] == collideeCirc.getColDat().getShapeBackRef()) continue;
		   
			if(shapes[i].getShapeType().equals("circle")) {
				 
				 Circle c1 = collideeCirc;
				 Circle c2 = (Circle) shapes[i];
				 
				 computeCircIntersection(c1,c2);
					
				if(c1.collidedWithInit.size() != 0) c1.getColDat().setColDat(
													(Circle) Sort.sortMinDistToMaxDist(c1.collidedWithInit).get(0) );
				c1.collidedWithInit.clear();
				
			 }//if(1)
			 
		}
		
	  }
		
		colliderShape.colliderToggleOff();
		colliderShape.collidedWithFin.clear();
		
		
		//Check if the second circle collides with a wall during chain
		//collision.We need to exclude wall friction to this execution because
		//in this block the last circle in the chain hits a wall, thus, the
		//momentum of the previous circle transfers to the current circle that hits the wall.
		//we don't want to apply friction on the momentum of the previous circle that was
		//transferred to the current circle because the previous circle doesn't hit the
		//wall.
		if(colDist != 0f) screenCollision((Circle)collideeShape,true,false);
		//if the second circle does not collide with a wall(with/without gravity involved)
		//then continue the chain process. else, stop the chain process
		if(!getScreenCol(0) && colDist != 0f) chainCollision(shapes,collideeShape); 
	  
	  
	  
	  //check if there's a chain collision. If there is then
	  //we need to check whether the last object in the chain
	  //hits a wall or not and if there's a chain count.
	  //System.out.println(chainCount);
	  Circle colliderCirc = (Circle) colliderShape;
	  if(chainCount != 0){ 
		CircCollisionData colliderShapeDat = colliderCirc.getColDat();
		Circle prevColliderCirc = (Circle) colliderShapeDat.getShapeRef();
		
		//if the last object hits a wall reapply the momentum
		//formula to each object in the chain. We do this in order
		//to make the objects in the chain bounce up because of the
		//impact on the wall.
		//Also, the bounce reaction of an object depends on which kind
		//of wall the last object in the chain collided. If it collided
		//on horizontal wall then the circles bounce to the left or right
		//if it hits a vertical wall then the circles bounce up. If the last circle
		//hits both walls then the circles bounces to the left or right and up.
	    if(getScreenCol(0)){
			
			PhysicsMath.applyImpulse(colliderShape,prevColliderCirc);
			
			PhysicsMath.SphereRollOnSphere(colliderShapeDat.getCosine(),
			colliderShapeDat.getSine(),colliderCirc,prevColliderCirc);
			
			//wallCollision(prevColliderCirc,false,true);
			
		}
		
		//reset the force increase of every second circles in a chain instance
		//that build up the whole chain. The first circle in the whole chain is excluded
		//because it didn't increase its force
		colliderCirc.resetTotForceY();
		colliderCirc.accumulateForceY(colliderCirc.get_fog());
		
		--chainCount;
		
		if(chainCount == 0){ maxChainCount = 0; resetScreenCol();}
		
		 
	  }
	  colliderCirc.getColDat().defaultValues(true);
	 
	  
	}
	
}