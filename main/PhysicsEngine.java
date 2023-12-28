package main;

import objects.*;
import sort.Sort;
import math.TrigMath;
import math.physicsmath.PhysicsMath;
import collisiondata.*;
import collisionalgorithm.*;

import javax.swing.*;

import java.lang.reflect.InvocationTargetException;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.RenderingHints;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.awt.image.BufferStrategy;
import java.awt.geom.*;

import javax.imageio.ImageIO;

import java.io.File;
import java.io.IOException;

import java.awt.event.*;

import java.util.*;


public final class PhysicsEngine extends JFrame implements KeyListener, Singleton
{	
	//singleton reference
    private static PhysicsEngine pEngine;
	
	public final static int FRAMES_PER_SECOND = 60;
	
	//change in time
	private static float dt;
	
	//collection of circle objects in our program
	private static Circle[] circles;
	
	//condition when we want to add additional circles in our program
	private boolean showCircles;
	//toggle between index mark and id mark
	private boolean toggleToIndex;
	
	private CollisionByNearestShape cbns;
	
	//ball image
	private BufferedImage ballImg;
	
	//This will be our back buffer graphics. All objects that have been drawn
	//into this graphics2d will be drawn to the BufferedImage().
	private Graphics2D g2dbf;
	
	//
	private Graphics g;
	
	//this will be our back buffer image.
	private VolatileImage bgImage;
	
	//Canvas
	private Canvas frameCanvas;
	
	//JFrame size
	private static int frameWidth,frameHeight;
	
	
	//Identity transform
	private AffineTransform identityTrans;
	
	//Object transform
	private AffineTransform objTrans;
	
	//
	private BufferStrategy bufferS;
	
	//platform's graphics attributes...
    GraphicsEnvironment ge;
    GraphicsDevice gd;
    GraphicsConfiguration gc;
	
	PhysicsEngine()
	{
		
		setTitle("Physics Engine Test Developed By Brainy Ghosts");
		setIgnoreRepaint(true);
		setResizable(false);
		setUndecorated(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//Set transform to identity transform
		identityTrans = new AffineTransform();
		//object transform
		objTrans = new AffineTransform();
		
		CollisionByNearestShape.createInstance();
		cbns = CollisionByNearestShape.getInstance();
		
		// Get platform's graphics attributes...
		ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		gd = ge.getDefaultScreenDevice();
		gc = gd.getDefaultConfiguration();
		
		//Simulated Full Screen Mode
		//Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        //frameWidth = (int)screenSize.getWidth();
        //frameHeight = (int)screenSize.getHeight();
		
		//Windowed Mode
		frameWidth = 720;
		frameHeight = 480;
		setUndecorated(false);
		
		setVisible(true);
		
		//Canvas
		frameCanvas = new Canvas(gc);
		frameCanvas.setIgnoreRepaint( true );
        frameCanvas.setSize( frameWidth, frameHeight );
		
		add(frameCanvas);
		pack();
		frameCanvas.requestFocus();
		//setVisible(true);
		
		
		frameCanvas.createBufferStrategy( 2 );
        bufferS = frameCanvas.getBufferStrategy();
		
		//back buffer image
		bgImage = gc.createCompatibleVolatileImage(frameWidth,frameHeight);	
		
		try { ballImg = ImageIO.read(new File("sources/images/ball.png")); }
		catch(IOException e){ System.err.println(e); }
		
		frameCanvas.addKeyListener(this);
		
	}
	
	@Override
	public void keyPressed(KeyEvent e){
		
		int keyCode = e.getKeyCode();
		
		switch(keyCode) {
		
		case KeyEvent.VK_Z:
		showCircles = true;
		break;
		
		case KeyEvent.VK_B:
		toggleToIndex = !toggleToIndex;
		break;
		
		}
		
	}
	@Override
	public void keyReleased(KeyEvent e){}
	@Override
	public void keyTyped(KeyEvent e){}
	
	public static float get_dt(){return dt;}
	public static void set_dt(float steps){dt = steps;}
	
	public static int getFrameWidth(){ return frameWidth; }
	public static int getFrameHeight(){ return frameHeight; }
	
	//draw graphics on the screen
	void drawGraphics(Shape[] shapes)
	{
		
		
		//this loop mechanics will ensure that even the content of our double-buffer is lost
		//due to some circumstances like doing alt+tab or locking your pc, our program can still
		//recover and draw graphics again.
		//reference: https://docs.oracle.com/javase/7/docs/api/java/awt/image/BufferStrategy.html
		do{
			
			do{
				
				g2dbf = (Graphics2D) bufferS.getDrawGraphics();
				//Rendering Hints to improve graphics quality
				g2dbf.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				g2dbf.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
				g2dbf.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2dbf.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,RenderingHints.VALUE_STROKE_PURE);
				g2dbf.setTransform(identityTrans);
				//set background
				g2dbf.setPaint(Color.BLACK);
				g2dbf.fillRect(0,0,frameWidth,frameHeight);
			
				g2dbf.setColor(Color.WHITE);
				g2dbf.drawString("Multiple Circles collision", frameWidth/3, 20);
				g2dbf.drawString("In-House 2D Physics Engine", frameWidth/3, 40);
				g2dbf.drawString("Developed by: Brainy Ghosts", frameWidth/3, 60);
				g2dbf.drawString("Press \"Z\" to add Circles. Press \"B\" to toggle circle mark between loop index or their id", frameWidth/5, 80);
			
				if(shapes != null) {
					drawShapes(shapes);
					markShapes(shapes);
				}
				
				//dispose graphics
				g2dbf.dispose();
				
			}
			while(bufferS.contentsRestored());
			
			//Display the buffer
			bufferS.show();
			
		}
		while(bufferS.contentsLost());
		
		
	}
	
	//draw shapes on the buffer
	void drawShapes(Shape[] shapes)
	{
		for(int i = 0; i < shapes.length;i++) {
		g2dbf.setTransform(identityTrans);
		objTrans.setToIdentity();
		objTrans.translate(shapes[i].getPosX(), shapes[i].getPosY());
		objTrans.rotate(shapes[i].getAngVel(), ballImg.getWidth(this) * 0.5f, ballImg.getHeight(this) * 0.5f);
		//objTrans.scale(0.75,0.75);
		g2dbf.drawImage(ballImg,objTrans,this);
        //g2dbf.setPaint(Color.WHITE);
        //g2dbf.fill(new Ellipse2D.Double(shapes[i].posX, shapes[i].posY, shapes[i].width, shapes[i].height));
		}
	}
	
	//mark shapes with numbers based on their indexes as their identifiers
	void markShapes(Shape[] shapes) {
	  
	  for(int i = 0; i < shapes.length;i++) {
	   
	    g2dbf.setTransform(identityTrans);
        g2dbf.setPaint(Color.CYAN);
	    if(!toggleToIndex) g2dbf.drawString(String.valueOf( shapes[i].getId() ),
                                    		shapes[i].getCenterX() - 3, shapes[i].getCenterY() + 3);
		else g2dbf.drawString(String.valueOf(i), shapes[i].getCenterX() - 3, shapes[i].getCenterY() + 3);
	  }
	  
	
	}
	
	//update shapes information and location.
	//parameters: (array of shapes)
	void updateShapes(Shape[] shapes)
	{    
		boolean collidersExist = true;
	    //updates object position due to gravity and collision.
	    //Update its velocity, distance and kinetic energy.
	    for(int i = 0; i < shapes.length;i++) shapes[i].setGenPos();
		
		while(collidersExist){ collidersExist = cbns.checkCollisions(shapes); }
			
		//System.out.println("reset loop");
		
	}
	
	/*Singleton Methods*/
	public static void createInstance(){
		if(pEngine != null) return;
		else pEngine = new PhysicsEngine();
	}
	
	public static void releaseInstance(){
		if(pEngine != null) pEngine = null;
	}
	
	public static PhysicsEngine getInstance() {
		if(pEngine != null) return pEngine;
		else return null;
	}
	/**/
	
	
	public static void main(String[]args) throws InterruptedException,InvocationTargetException
	{
		
		SwingUtilities.invokeAndWait(new Runnable(){
			@Override
			public void run()
			{
		       createInstance();
			}
		});
		
		
		//Physics Steps
		new Thread(new Runnable(){
			
			//Variables for fps(Frames Per Second)
            int frames = 0,yieldThread = 0;
            long totalTime = 0,t2 = 0;
            long initialTime = System.nanoTime();
            long currentTime = 0;
		    final int FRAMES_PER_YIELD = (int)(FRAMES_PER_SECOND * 0.2f); //Yield when processed frames reaches a portion of Frames per second
            final int NANOS_PER_SECOND = 1_000_000_000;
            final int FRAMES_PER_NANO = NANOS_PER_SECOND / FRAMES_PER_SECOND;
			
			//This millis variables are use to compute dt in millis format. Using nanos format when computing physics elements render a high
			//value.
			final float FRAMES_PER_MILLIS = 1000f/FRAMES_PER_SECOND; 
			
			
			
			@Override
			public void run()
			{
				//convert millis to second before setting the time step in dt
				set_dt(FRAMES_PER_MILLIS/1000f);
				int cLength = 0,prevLength = 0;
				while(true)
				{
					currentTime = System.nanoTime();
			        totalTime += currentTime - initialTime;
                    t2 += currentTime - initialTime;
                    initialTime = currentTime;
			  
                    if( totalTime > NANOS_PER_SECOND ) {
					  //System.out.println("FPS: " + frames);
                      totalTime -= NANOS_PER_SECOND;
                      frames = 0;
                    } 
            
                    if(t2 < FRAMES_PER_NANO) continue;
			  
			        //System.out.println("Physics Steps: " + frames);
					//System.out.println("Physics Steps");
			  
                    frames += 1;
                    t2 -= FRAMES_PER_NANO;
		            yieldThread++;
					
					//This condition checks if showCircles is true
					//and the total objects in the array is not equal to 40.
					//showCircles boolean will go true if we press the Z
					//button. Once we press the Z button another 5 circles
					//will appear on the screen. The maximum circle that 
					//we can create in this program is 40.
					if(pEngine.showCircles && cLength != 40) {
					  //This array stores the old array values
					  Circle[] tempCircles = null;
					  //offset for x position of the circles
					  float offset = 70f;
					  //use to assign a direction to a circle
					  int signChanger = 1;
					  
					  //If the array already has a content. Move those 
					  //content in the tempCircles array. Store the old
					  //length to prevLength
					  if(circles != null){ 
					    tempCircles = circles;
					    prevLength = tempCircles.length;
					  }
					  //Whether the array had a content or not we need to
					  //increment cLength to 5 and instantiate circles with
					  //the length of clength
					  cLength += 1;
					  circles = new Circle[cLength];
					  
					  //If prevLenth is not equal to 0 it means that the array had a content. Otherwise,
					  //the array doesn't have any content. If the array had a content we need to add
					  //the old content first before adding new content. Otherwise, skip this condition
					  //and add new content
					  if(prevLength != 0) for(int i = 0; i < prevLength;i++) circles[i] = tempCircles[i];
					  
					  //This loop adds new five circles in our array. Every iteration, each circle will have
					  //different x position, width and height and x and y velocities and velocities direction.
					  //each circle's acceleration stays the same to all circles.
					  for(int i = prevLength; i < cLength;i++) {
						  
						//circles[i] = new Circle(100f + offset,-10f,50f,50f,((i+1)*.5f)*signChanger,(i+1)*.5f,i + 1);
						//circles[i] = new Circle(100f + offset,-10f,50f,50f,((i+1)*.5f)*signChanger,(i+1)*.5f,1);
						circles[i] = new Circle(i,100f + offset,-10f,50f,50f,2f * signChanger,1f,1);
						signChanger = -signChanger;
						offset += 70f;
					  }
					  
					  //switch showCircles to false.
					  pEngine.showCircles = false;
		
					}
					if(circles != null) pEngine.updateShapes(circles);
					pEngine.drawGraphics(circles);

			  
			        if(yieldThread >= (FRAMES_PER_YIELD))
			        {
				        yieldThread = 0;
				        Thread.yield();
			        }
				}
			}
			
		}).start();
		
	}
	
}