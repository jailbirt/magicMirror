package squeleton3D;

import api.KinectApi;
import api.Models.*;

import SimpleOpenNI.*;
import processing.core.*;

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

	public void setup() 
	{
		size(640, 480, OPENGL);

		try {

			kinect = new SimpleOpenNI(this);
			kinect.enableDepth();
			kinect.enableUser(SimpleOpenNI.SKEL_PROFILE_ALL);
			kinect.setMirror(true);
			// turn on vga - depth-color alignment
			kinect.enableRGB();
			//kinect.alternativeViewPointDepthToImage(); 
			
			kApi = new KinectApi(this,kinect);

			model = new Raket(this);
					
			// Create a Box2D object
			//this.box2d = new PBox2D(this);
			// Create a "default" world
			//this.box2d.createWorld();

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
		translate(width/2, height/2, 0);

		rotateX(radians(180));
		//scale(3);

		IntVector userList = new IntVector();
		kinect.getUsers(userList);
		
		if (userList.size() > 0) {
			
			int userId = userList.get(0);
			if ( kinect.isTrackingSkeleton(userId)) 
			{
			
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
				{ 
					println("Esta arriba del hombro ajusto la raqueta para saque");
					orientation.rotateY(150.0f);
				}

				model.display3DModel(position, orientation);

			}
		}
	}

	// user-tracking callback's !
	public void onNewUser(int userId) 
	{
		println("start pose detection");
		kinect.startPoseDetection("Psi", userId);
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
  
}
