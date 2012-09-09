package squeleton3D;

import api.KinectApi;
import api.Models.*;

import SimpleOpenNI.*;
import processing.core.*;
import processing.opengl.*;


//import pbox2d.*;

public class Demo extends PApplet {

     /**
     * 
     */
	private static final long serialVersionUID = -4481472481792026127L;

	public static void main(String args[]) 
	{
		PApplet.main(new String[] { "--bgcolor=#FFFFFF", "squeleton3D.Demo" });    
	}

	//protected PBox2D box2d ;
	protected Raket model;
	protected SimpleOpenNI kinect ;
	protected KinectApi kApi ;
    boolean useMatrixForOrientation=true;
    int Z=1000;
    
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
	}

	public void draw() 
	{
		kinect.update();
		background(255);
		hint(DISABLE_DEPTH_TEST); // Así se hace para realidad aumentada.
		image(kinect.rgbImage(),0,0);
		hint(ENABLE_DEPTH_TEST);
		translate(width/2, height/2, Z);

		rotateX(radians(180));
		//scale(3);

		IntVector userList = new IntVector();
		kinect.getUsers(userList);
		
		if (userList.size() > 0) {
			
			int userId = userList.get(0);
			if ( kinect.isTrackingSkeleton(userId)) matrixOrientation(userId);
		}
	}

	//MatrixVersion
	public void matrixOrientation (int userId) {
		pushMatrix();
		kApi.draw3DSkeleton(userId);
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
		model.draw();
//		kinect.drawJoint(userId,SimpleOpenNI.SKEL_RIGHT_HAND);
		println("position X"+position.x);
		println("position Y"+position.y);
		kinect.convertRealWorldToProjective(position, position);	
		println("CONVposition X"+position.x);
		println("CONVposition Y"+position.y);
		popMatrix();
	}
		
	// user-tracking callback's !
	public void onNewUser(int userId) 
	{
		//println("start pose detection");
		//kinect.startPoseDetection("Psi", userId);
		println("Detectando posicion quedate quieto un cacho!");
		kinect.startTrackingSkeleton(userId);
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
		if(key == '1'){ model.shapeMode(LINES); }
		else if (key == '2'){ model.shapeMode(TRIANGLES); }
		else if (key == '3'){ model.shapeMode(POINT); }
		else if (key == '4'){ model.shapeMode(QUADS); }
		else if (key == '5'){ model.shapeMode(POLYGON); }
		else if (key == '6'){
			if (useMatrixForOrientation) useMatrixForOrientation=false;
			else useMatrixForOrientation=true;
		}else if (key == '+' && Z < 1000) Z+=100;
		else if (key == '-' && Z > 0)  Z-=100;
		println("Z"+Z);
		
	}
}
