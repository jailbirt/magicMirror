package api;

import processing.core.*;
import SimpleOpenNI.*;

public class KinectApi extends PApplet
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2422128540264796785L;
	protected SimpleOpenNI kinect ;
	protected PApplet parent ;
	PVector bodyCenter = new PVector();
	PVector bodyDir = new PVector();
	
	public KinectApi(PApplet p, SimpleOpenNI k)
	{
		kinect = k ;
		parent = p ;
	}
	
	public void draw2DSkeleton(int userId)
	{
		kinect.drawLimb(userId, SimpleOpenNI.SKEL_HEAD, SimpleOpenNI.SKEL_NECK);
		kinect.drawLimb(userId, SimpleOpenNI.SKEL_NECK, SimpleOpenNI.SKEL_LEFT_SHOULDER);
		kinect.drawLimb(userId, SimpleOpenNI.SKEL_LEFT_SHOULDER, SimpleOpenNI.SKEL_LEFT_ELBOW);
		kinect.drawLimb(userId, SimpleOpenNI.SKEL_LEFT_ELBOW, SimpleOpenNI.SKEL_LEFT_HAND);
		kinect.drawLimb(userId, SimpleOpenNI.SKEL_NECK, SimpleOpenNI.SKEL_RIGHT_SHOULDER);
		kinect.drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_SHOULDER, SimpleOpenNI.SKEL_RIGHT_ELBOW);
		kinect.drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_ELBOW, SimpleOpenNI.SKEL_RIGHT_HAND);
		kinect.drawLimb(userId, SimpleOpenNI.SKEL_LEFT_SHOULDER, SimpleOpenNI.SKEL_TORSO);
		kinect.drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_SHOULDER, SimpleOpenNI.SKEL_TORSO);
		kinect.drawLimb(userId, SimpleOpenNI.SKEL_TORSO, SimpleOpenNI.SKEL_LEFT_HIP);
		kinect.drawLimb(userId, SimpleOpenNI.SKEL_LEFT_HIP, SimpleOpenNI.SKEL_LEFT_KNEE);
		kinect.drawLimb(userId, SimpleOpenNI.SKEL_LEFT_KNEE, SimpleOpenNI.SKEL_LEFT_FOOT);
		kinect.drawLimb(userId, SimpleOpenNI.SKEL_TORSO, SimpleOpenNI.SKEL_RIGHT_HIP);
		kinect.drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_HIP, SimpleOpenNI.SKEL_RIGHT_KNEE);
		kinect.drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_KNEE, SimpleOpenNI.SKEL_RIGHT_FOOT);
		kinect.drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_HIP, SimpleOpenNI.SKEL_LEFT_HIP);
	}
	
	public void draw3DSkeleton(int userId)
	{ 
		parent.strokeWeight(3);
		drawLimb(userId, SimpleOpenNI.SKEL_HEAD, SimpleOpenNI.SKEL_NECK);
		drawLimb(userId, SimpleOpenNI.SKEL_NECK, SimpleOpenNI.SKEL_LEFT_SHOULDER);
		drawLimb(userId, SimpleOpenNI.SKEL_LEFT_SHOULDER, SimpleOpenNI.SKEL_LEFT_ELBOW);
		drawLimb(userId, SimpleOpenNI.SKEL_LEFT_ELBOW, SimpleOpenNI.SKEL_LEFT_HAND);
		drawLimb(userId, SimpleOpenNI.SKEL_NECK, SimpleOpenNI.SKEL_RIGHT_SHOULDER);
		drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_SHOULDER, SimpleOpenNI.SKEL_RIGHT_ELBOW);
		drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_ELBOW, SimpleOpenNI.SKEL_RIGHT_HAND);
		drawLimb(userId, SimpleOpenNI.SKEL_LEFT_SHOULDER, SimpleOpenNI.SKEL_TORSO);
		drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_SHOULDER, SimpleOpenNI.SKEL_TORSO);
		drawLimb(userId, SimpleOpenNI.SKEL_TORSO, SimpleOpenNI.SKEL_LEFT_HIP);
		drawLimb(userId, SimpleOpenNI.SKEL_LEFT_HIP, SimpleOpenNI.SKEL_LEFT_KNEE);
		drawLimb(userId, SimpleOpenNI.SKEL_LEFT_KNEE, SimpleOpenNI.SKEL_LEFT_FOOT);
		drawLimb(userId, SimpleOpenNI.SKEL_TORSO, SimpleOpenNI.SKEL_RIGHT_HIP);
		drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_HIP, SimpleOpenNI.SKEL_RIGHT_KNEE);
		drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_KNEE, SimpleOpenNI.SKEL_RIGHT_FOOT);
		drawLimb(userId, SimpleOpenNI.SKEL_RIGHT_HIP, SimpleOpenNI.SKEL_LEFT_HIP);
    	 // draw body direction
	    getBodyDirection(userId,bodyCenter,bodyDir);
		  
		bodyDir.mult(200);  // 200mm length
		bodyDir.add(bodyCenter);
		  
		parent.stroke(255,200,200);
		parent.line(bodyCenter.x,bodyCenter.y,bodyCenter.z,
		              bodyDir.x ,bodyDir.y,bodyDir.z);

		parent.strokeWeight(1);
	}

	void drawLimb(int userId,int jointType1,int jointType2)
	{
		PVector jointPos1 = new PVector();
		PVector jointPos2 = new PVector();
		float confidence;
		confidence  = kinect.getJointPositionSkeleton(userId,jointType1,jointPos1);
		confidence += kinect.getJointPositionSkeleton(userId,jointType2,jointPos2);
		
		parent.fill(255, 0, 0);
		parent.stroke(100);
		parent.strokeWeight(5);
		if(confidence > 1)
		{
			parent.stroke(255,0,0,confidence * 200 + 55);
			parent.line(jointPos1.x, jointPos1.y, jointPos1.z, jointPos2.x, jointPos2.y, jointPos2.z);
		}
		
		drawJointOrientation(userId,jointType1,jointPos1,50);
	}
	
	void drawJointOrientation(int userId,int jointType,PVector pos,float length)
	{
	  // draw the joint orientation  
	  PMatrix3D  orientation = new PMatrix3D();
	  float confidence = kinect.getJointOrientationSkeleton(userId,jointType,orientation);
	  if(confidence < 0.001f) 
	    // nothing to draw, orientation data is useless
	    return;
	    
	  parent.pushMatrix();
	    parent.translate(pos.x,pos.y,pos.z);
	    // set the local coordsys
	    parent.applyMatrix(orientation);   
	    // coordsys lines are 100mm long
	    // x - r
	    parent.stroke(255,0,0,confidence * 200 + 55);
	    parent.line(0,0,0,length,0,0);
	    // y - g
	    parent.stroke(0,255,0,confidence * 200 + 55);
	    parent.line(0,0,0,0,length,0);
	    // z - b    
	    parent.stroke(0,0,255,confidence * 200 + 55);
	    parent.line(0,0,0,0,0,length);
	 parent.popMatrix();
	}

	public void drawJoint(int userId, int jointID)
	{
		PVector joint = new PVector();

		float confidence = kinect.getJointPositionSkeleton(userId, jointID, joint);
		if(confidence < 0.5)
		{
			return; 
		}
		PVector convertedJoint = new PVector();
		kinect.convertRealWorldToProjective(joint, convertedJoint);
		
		parent.ellipse(convertedJoint.x, convertedJoint.y, 5, 5);
	}
	
	public void displayDepthUser(int uId)
	{
		//PVector[] depthPoints = kinect.depthMapRealWorld();	

		int[] userMap;

		userMap = kinect.getUsersPixels(SimpleOpenNI.USERS_ALL);

		parent.loadPixels();
		for (int i=0; i < userMap.length; i++)
		{
			// if the pixel is part of the user
			if (userMap[i] == uId) {
				// set the sketch pixel to the color pixel
				parent.pixels[i] = parent.color(0, 255, 0);
			}
		}
		parent.updatePixels();		
	}
	
	public void getBodyDirection(int userId,PVector centerPoint,PVector dir)
	{
	  PVector jointL = new PVector();
	  PVector jointH = new PVector();
	  PVector jointR = new PVector();
	  float  confidence;
	  
	  // draw the joint position
	  confidence = kinect.getJointPositionSkeleton(userId,SimpleOpenNI.SKEL_LEFT_SHOULDER,jointL);
	  confidence = kinect.getJointPositionSkeleton(userId,SimpleOpenNI.SKEL_HEAD,jointH);
	  confidence = kinect.getJointPositionSkeleton(userId,SimpleOpenNI.SKEL_RIGHT_SHOULDER,jointR);
	  
	  // take the neck as the center point
	  confidence = kinect.getJointPositionSkeleton(userId,SimpleOpenNI.SKEL_NECK,centerPoint);
	  
	  /*  // manually calc the centerPoint
	  PVector shoulderDist = PVector.sub(jointL,jointR);
	  centerPoint.set(PVector.mult(shoulderDist,.5));
	  centerPoint.add(jointR);
	  */
	  
	  PVector up = new PVector();
	  PVector left = new PVector();
	  
	  up.set(PVector.sub(jointH,centerPoint));
	  left.set(PVector.sub(jointR,centerPoint));
	  
	  dir.set(up.cross(left));
	  dir.normalize();
	}
	
	//Metodos Para manipular diferencias entre la resolución del sketch <-> kinect.
/*
	int[] resizeDepth(int[] depthImg, int n){
	  int xSizeOrig = kinect.depthWidth();
	  int ySizeOrig = kinect.depthHeight();
	  int xSize = xSizeOrig/n;
	  int ySize = ySizeOrig/n;
	  int[] resDepthImg = new int[xSize*ySize];

	  for(int y = 0; y < ySize; y++){
	    for(int x = 0; x < xSize; x++){
	      resDepthImg[x + y*xSize] = depthImg[x*n + y*n*xSizeOrig];
	    }
	  }
	  return resDepthImg;
	}

	PVector[] resizeMap3D(PVector[] map3D, int n){
	  int xSizeOrig = kinect.depthWidth();
	  int ySizeOrig = kinect.depthHeight();
	  int xSize = xSizeOrig/n;
	  int ySize = ySizeOrig/n;
	  PVector[] resMap3D = new PVector[xSize*ySize];

	  for(int y = 0; y < ySize; y++){
	    for(int x = 0; x < xSize; x++){
	      resMap3D[x + y*xSize] = map3D[x*n + y*n*xSizeOrig].get();
	    }
	  }
	  return resMap3D;
	}

	PImage resizeRGB(PImage rgbImg, int n){
	  int xSizeOrig = kinect.depthWidth();
	  int ySizeOrig = kinect.depthHeight();
	  int xSize = xSizeOrig/n;
	  int ySize = ySizeOrig/n;
	  PImage resRGB = createImage(xSize,ySize,RGB);
	 
	  for(int y = 0; y < ySize; y++){
	    for(int x = 0; x < xSize; x++){
	      resRGB.pixels[x + y*xSize] = rgbImg.pixels[x*n + y*n*xSizeOrig];
	    }
	  }
	  return resRGB; 
	}
*/
}
