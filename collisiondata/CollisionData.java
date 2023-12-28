package collisiondata;

import objects.Shape;


public abstract class CollisionData {
	
	//refrence of a shape(Forward chain)
	protected Shape shapeRef;
	//refrence of a shape(Backward chain)
	protected Shape shapeBackRef;
	
	//Store distance of two shapes that collided and should be stored to collidee shape.
	//The relationship is many-to-one relationship. multiple collidee shapes that
	//is collided to a collider shape.
	private float colDist;
	
	
	public void setColDat(Shape shape) {
		
		this.colDist = shape.getColDist();
		shapeRef = shape;
		
	}
	
	protected void defaultValues(boolean delBackRef) {
		
		colDist = 0f;
		shapeRef = null;
		if(delBackRef) shapeBackRef = null;
		
	}
	
	public float getColDist(){ return colDist; }
	public Shape getShapeRef(){ return shapeRef; }
	public Shape getShapeBackRef(){ return shapeBackRef; }
	public void setShapeBackRef(Shape shapeBackRef){ this.shapeBackRef = shapeBackRef; }
	
	
}