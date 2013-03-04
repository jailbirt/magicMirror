package kinectAr3D;

import api.KinectApi;
import api.Models.*;

import SimpleOpenNI.*;
import processing.core.*;
//import processing.*;


//import pbox2d.*;

public class tennisRacket extends PApplet {

    /**
    * 
    */
	//protected PBox2D box2d ;
	protected Raket model;
	protected SimpleOpenNI kinect ;
	protected KinectApi kApi ;
	boolean show3dSkel=false;
	boolean removeBackground=false;
	boolean userPixels=false;
	float zoomF =0.7f;
	float rotX = radians(180);  // by default rotate the hole scene 180deg around the x-axis, the data from openni comes upside down
	float rotY = radians(0);
	
	 
	private static final long serialVersionUID = -4481472481792026127L;

	public static void main(String args[]) 
	{
		
		PApplet.main(new String[] { "--bgcolor=#FFFFFF", "kinectAr3D.tennisRacket" });    
	}

	
    
	public void setup() 
	{
		size(640, 480, OPENGL);
		 //
		try {

			kinect = new SimpleOpenNI(this);
			kinect.enableDepth();
			kinect.enableUser(SimpleOpenNI.SKEL_PROFILE_ALL);
			kinect.setMirror(true);
			// turn on vga - depth-color alignment
			kinect.enableRGB();
			kinect.alternativeViewPointDepthToImage(); 
			
			kApi = new KinectApi(this,kinect);
			model = new Raket(this);
		
	
		} catch (Throwable e) {
			// Print out the exception that occurred
		    System.out.println("An Exception occurs: " + e.getMessage());
		    this.stop();
		    exit();
		}

		noStroke();
		noFill();
		smooth();
		perspective(radians(45),width/height,10,150000);
	              
	}

	public void draw() 
	{
		kinect.update();
		background(255);
		hint(DISABLE_DEPTH_TEST); // Así se hace para realidad aumentada.
		image(kinect.rgbImage(),0,0);
		hint(ENABLE_DEPTH_TEST);
		// set the scene pos
		translate(width/2, height/2, 0);
		rotateX(rotX);
		rotateY(rotY);
		scale(zoomF);
    	translate(0,0,-1000);  // set the rotation center of the scene 1000 infront of the camera

		IntVector userList = new IntVector();
		kinect.getUsers(userList);
		int users=(int) userList.size();
		if (users > 0) {
		 for (int i = 0; i < users; i++) {
			int userId = userList.get(i);
			if ( kinect.isTrackingSkeleton(userId)) {
				if(show3dSkel) kApi.draw3DSkeleton(userId);
				if(userPixels) {
					if(removeBackground) background(255);
					hint(DISABLE_DEPTH_TEST); 
					kApi.displayUserPixels(userId);	
					hint(ENABLE_DEPTH_TEST); 
				}
				matrixOrientation(userId);
			}
		 }
		}
	}

	//MatrixVersion
	public void matrixOrientation (int userId) {
		pushMatrix();
		//  kApi.draw3DSkeleton(userId);
		PVector position = new PVector();
		kinect.getJointPositionSkeleton(userId, SimpleOpenNI.SKEL_RIGHT_HAND, position);
		PMatrix3D orientation = new PMatrix3D();
		kinect.getJointOrientationSkeleton(userId, SimpleOpenNI.SKEL_RIGHT_ELBOW, orientation);
		 
		// En el saque la raqueta se poner 'màs' derecha.
		PVector rightElbow = new PVector();
		PVector rightShoulder = new PVector();
		kinect.getJointPositionSkeleton(userId,SimpleOpenNI.SKEL_RIGHT_ELBOW, rightElbow);
		kinect.getJointPositionSkeleton(userId,SimpleOpenNI.SKEL_RIGHT_SHOULDER,rightShoulder);
		
		if(rightElbow.y > rightShoulder.y && rightElbow.x > rightShoulder.x)
			orientation.rotateY(150.0f);
		
		model.prepare3DModel(position, orientation);
        //Prueba de buffer.http://processing.org/reference/createGraphics_.html
		//PGraphics pg;
		//pg = createGraphics(100, 100);
		// pg.beginDraw();
		//  pg.background(102);
		//  pg.stroke(255);
		//  pg.line(pg.width*0.5, pg.height*0.5, mouseX, mouseY);
		//  pg.endDraw();
		//  image(pg, 50, 50); 
		model.draw();
		hint(DISABLE_DEPTH_TEST); // Así se hace para realidad aumentada.
		kApi.drawJoint(userId,SimpleOpenNI.SKEL_RIGHT_HAND);
		hint(ENABLE_DEPTH_TEST);
		kinect.convertRealWorldToProjective(position, position);	
		popMatrix();
	}
		
	// user-tracking callback's !
	public void onNewUser(int userId) 
	{
		try {
		     println("Detectando posicion");
		     kinect.startTrackingSkeleton(userId);
		} catch (Throwable e) {
			// Print out the exception that occurred
		    println("An Exception occurs: " + e.getMessage() + "Autodetection doesn't works");
		    println("porfa start pose detection");
			kinect.startPoseDetection("Psi", userId);
		}
	}

	public void onEndCalibration(int userId, boolean successful) 
	{
		if (successful) 
		{
			println(" User calibrated !!!");
			kinect.startTrackingSkeleton(userId);
		}
		else 
		{
			println(" Failed to calibrate user !!!");
			kinect.startPoseDetection("Psi", userId);
		}
	}

	public void onStartPose(String pose, int userId) 
	{
		println("Started pose for user");
		kinect.stopPoseDetection(userId);
		kinect.requestCalibrationSkeleton(userId, true);
	}

	public void stop()
	{
		kinect.finalize();
		super.stop();
	}
	public void keyPressed()
	{
		  switch(keyCode)
		  {
		    case 1:model.shapeMode(LINES);break; 
			case 2:model.shapeMode(TRIANGLES);break;
			case 3:model.shapeMode(POINT);break; 
			case 4:model.shapeMode(QUADS);break; 
			case 5:model.shapeMode(POLYGON);break; 
			
  		    case LEFT:rotY += 0.1f;break;
		    case RIGHT:
		              // zoom out
		              rotY -= 0.1f;
		              break;
		    case UP:
		             if(keyEvent.isShiftDown())
		               zoomF += 0.01f;
		             else
		               rotX += 0.1f;
		             break;
		    case DOWN:
		              if(keyEvent.isShiftDown() && zoomF > 0.01)
		              {
		                zoomF -= 0.01f;
		              }
		              else
		                rotX -= 0.1f;
		              break;
		  }
		  switch(key)
		  {
		    case 's':
		       if (show3dSkel) show3dSkel=false;
			   else show3dSkel=true;
			   break;
		    case 'b':
			       if (removeBackground) removeBackground=false;
				   else removeBackground=true;
				   break;
		    case 'u':
			       if (userPixels) {
			    	   zoomF=0.7f;
			    	   userPixels=false;
			       }
				   else
					   {
			    	   zoomF=0.2f; 	 
					   userPixels=true;
					   }
					break;
		  }
	}
}
