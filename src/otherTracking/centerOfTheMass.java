package otherTracking;
import SimpleOpenNI.*;
import processing.core.*;
import processing.opengl.*;

public class centerOfTheMass extends PApplet {
	SimpleOpenNI kinect;
	public void setup() {
	size(640, 480);
	kinect = new SimpleOpenNI(this);
	kinect.enableDepth();
	kinect.enableUser(SimpleOpenNI.SKEL_PROFILE_NONE); //1
	}
	public void draw() {
	kinect.update();
	image(kinect.depthImage(), 0, 0);
	IntVector userList = new IntVector();
	kinect.getUsers(userList);
	for (int i=0; i<userList.size(); i++) { //2
    	int userId = userList.get(i);
    	PVector position = new PVector();
    	kinect.getCoM(userId, position);// 3 for each user get center of the mass
    	kinect.convertRealWorldToProjective(position, position);
    	fill(255, 0, 0);
    	ellipse(position.x, position.y, 25,25);
    	textSize(40);
    	fill(0);
    	text(userId, position.x, position.y);
     }
    }

	 static public void main(final String args[]) {
	    	// should have pakage.obj
	    	int display=2;
	    	
	      	String mainSketch="centerOfTheMass";
	    	//String mainSketch="multicam";
	    	System.out.print("Initializing on display..."+display);
	    	PApplet.main(new String[] {"--bgcolor=#ECE9D8","--present","--present-stop-color=#000000",
	    		        	"--display="+display,"otherTracking."+mainSketch });
	    }

}
