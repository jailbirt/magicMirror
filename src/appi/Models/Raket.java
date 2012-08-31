package appi.Models;

import java.awt.DisplayMode;

import processing.core.*;
import saito.objloader.OBJModel;
import SimpleOpenNI.SimpleOpenNI;
import appi.InteractModel;

public class Raket extends InteractModel {

	public Raket(PApplet p) {
		super(p);
		// TODO Auto-generated constructor stub
//		 String executionPath = System.getProperty("user.dir");		
		model = new OBJModel(p, "data/TennisRaket/TennisRaket.obj", "relative", PConstants.TRIANGLES);
		model.scale(20);
		model.enableDebug();
		model.enableMaterial();
		model.enableTexture();

		PVector cmass = new PVector(0,-100,0); // interseccion en el medio del puño
		model.translate(cmass);
	}

	public InteractModel setupDisplay()
	{
		parent.noStroke(); //NoStroke para que muestre el material
		parent.lights(); //Prendo Luces.
		
		//parent.stroke(255,0,0);
		//parent.strokeWeight(1);
		parent.rotateX(PApplet.radians(90));
		parent.rotateY(PApplet.radians(180));
		
		return this;
	}
	
	public void display3DModel(int userId, SimpleOpenNI kinect, PVector position,PMatrix3D orientation){
		// En el saque la raqueta se poner 'màs' derecha.
		PVector rightElbow = new PVector();
		PVector rightShoulder = new PVector();
		kinect.getJointPositionSkeleton(userId,SimpleOpenNI.SKEL_RIGHT_ELBOW, rightElbow);
		kinect.getJointPositionSkeleton(userId,SimpleOpenNI.SKEL_RIGHT_SHOULDER,rightShoulder);
		if(rightElbow.y > rightShoulder.y && rightElbow.x > rightShoulder.x) { 
		 parent.println("Esta arriba del hombro ajusto la raqueta para saque");
		 orientation.rotateY(150.0f);
		}
	    super.display3DModel(position, orientation);
	}
	
}
