package background;
import pogg.TheoraMovie;
import processing.core.PApplet;
import ddf.minim.AudioPlayer;

public class video extends PApplet{
	/* OpenProcessing Tweak of *@*http://www.openprocessing.org/sketch/9004*@* */
	/* !do not delete the line above, required for linking your tweak if you re-upload */
//	import ddf.minim.*;

//	Minim minim;
	AudioPlayer player;

	TheoraMovie myMovie;
	public void setup(){
	  size(1920,1080);

	  String js="javascript:";
	  js+="a=document.getElementsByTagName('*');";
	  js+="for(var i=0;i<a.length;i++){ var o=a[i].style;";
	  js+="  o.backgroundColor='black';";
	  js+="  o.backgroundImage='none';";
	  js+="  o.color='black';";
	  js+="  o.border='none';";
	  js+="};";
	  js+="void(0); ";
	  link(js);

	  myMovie = new TheoraMovie(this, "Grateful-Dead-Fire-on-the-Mountain-for-2012-with-Pot-Leaves.ogv");
	  frameRate(myMovie.fps);
	  myMovie.loop();

	 // minim = new Minim(this);
	 // player = minim.loadFile("short.mp3");
	 // player.play();
	 // player.loop();
	}

	float prevSec=-1;
	float sec=-1;

	public void draw(){
		  prevSec=sec;
		  sec = myMovie.time();
		 // if(sec < prevSec) player.rewind();

		  myMovie.read();
		  image(myMovie,0,0);
		}

	public	void stop(){
		 // player.close();
		 // minim.stop();
		 // super.stop();
		}



}
