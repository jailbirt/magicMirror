package appi.Models;

import processing.core.*;
import saito.objloader.OBJModel;
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
	
	public void display3DModel(PVector position,PMatrix3D orientation)
	{
	    super.display3DModel(position, orientation);
	}
	
}
