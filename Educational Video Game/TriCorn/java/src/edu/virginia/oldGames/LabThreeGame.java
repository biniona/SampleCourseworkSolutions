package edu.virginia.oldGames;

import edu.virginia.engine.display.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

/**
 * Example game that utilizes our engine. We can create a simple prototype game with just a couple lines of code
 * although, for now, it won't be a very fun game :)
 * */
public class LabThreeGame extends Game{

	/* Create a sprite object for our game. We'll use mario */
	Sprite sun = new Sprite("Sun", "sun.png");
	DisplayObjectContainer fakeSun = new DisplayObjectContainer("FakeSun");
    Sprite earth = new Sprite("Earth", "earth.png");
	Sprite mars = new Sprite("Mars", "mars.png");
	Sprite moon = new Sprite("Moon", "moon.png");

    private final int[] possibleKeyEvents = {
            KeyEvent.VK_B,
            KeyEvent.VK_N,
            KeyEvent.VK_UP,
            KeyEvent.VK_DOWN,
            KeyEvent.VK_RIGHT,
            KeyEvent.VK_LEFT,
            KeyEvent.VK_I,
            KeyEvent.VK_K,
            KeyEvent.VK_J,
            KeyEvent.VK_L,
            KeyEvent.VK_Q,
            KeyEvent.VK_W,
            KeyEvent.VK_V,
            KeyEvent.VK_Z,
            KeyEvent.VK_X,
            KeyEvent.VK_A,
            KeyEvent.VK_S
    };
	/**
	 * Constructor. See constructor in Game.java for details on the parameters given
	 * */
	public LabThreeGame() {
		super("Lab Three Test Game", 1000, 1000);
		Point sunPos = new Point(0, 100);
		Point fakeSunPos = new Point(0,0);
		Point earthPos = new Point(300, 0);
		Point marsPos = new Point (600, 0);
		Point moonPos = new Point (100, 0);
		//Point sunRotationPoint = new Point(72,72);
		//Point earthRotationPoint = new Point(35,35);
		//Point earthRotAdj = addPoint(earthPos, earthRotationPoint);
		//Point sunRotAdj = addPoint(sunPos, sunRotationPoint);
		//sun.setPosition(sunRotAdj);
		//earth.setPivotPoint(earthRotationPoint);
		sun.setPosition(sunPos);
		fakeSun.setPosition(fakeSunPos);
		earth.setPosition(earthPos);
		mars.setPosition(marsPos);
		moon.setPosition(moonPos);
		sun.addChild(fakeSun);
		fakeSun.addChild(mars);
		fakeSun.addChild(earth);
		earth.addChild(moon);
		System.out.println(earth.localToGlobal(earthPos));
		System.out.println(earth.globalToLocal(new Point(200, 100)));
	}

	/**
	 * Engine will automatically call this update method once per frame and pass to us
	 * the set of keys (as strings) that are currently being pressed down
	 * */

	private Animation walk = new Animation("Walk", 0,1);
    private Animation jump = new Animation("Jump", 2,2);
	private final int ROTATION_CONSTANT = 1;
    private boolean arrListSet = false;

    @Override
	public void update(ArrayList<Integer> pressedKeys){
		super.update(pressedKeys);
		
        if(sun != null) sun.update(pressedKeys);

		//rotationCode
		if (pressedKeys.contains(KeyEvent.VK_S)) {
			setRotations(-1, 2);
		}

		else if (pressedKeys.contains(KeyEvent.VK_A)) {
			setRotations(2, 4);
		}
		else{setRotations(1, 2);}

		//must be called after sun.setRotation
		//think of the input at a percentage to increase rotation by



		if (pressedKeys.contains(KeyEvent.VK_UP)) {
        	sun.setPosition(new Point(sun.getPosition().x,
					sun.getPosition().y - 5));

        }
        if (pressedKeys.contains(KeyEvent.VK_DOWN)) {
        	sun.setPosition(new Point(sun.getPosition().x,
					sun.getPosition().y + 5));
        }
        if (pressedKeys.contains(KeyEvent.VK_RIGHT)) {
        	sun.setPosition(new Point(sun.getPosition().x + 5,
					sun.getPosition().y));
        }
        if (pressedKeys.contains(KeyEvent.VK_LEFT)) {
        	sun.setPosition(new Point(sun.getPosition().x - 5,
					sun.getPosition().y));
        }
        if (pressedKeys.contains(KeyEvent.VK_I)) {
        	sun.setPivotPoint(new Point(sun.getPivotPoint().x,
					sun.getPivotPoint().y - 5));
        }
        if (pressedKeys.contains(KeyEvent.VK_K)) {
        	sun.setPivotPoint(new Point(sun.getPivotPoint().x,
					sun.getPivotPoint().y + 5));
        }
        if (pressedKeys.contains(KeyEvent.VK_J)) {
        	sun.setPivotPoint(new Point(sun.getPivotPoint().x - 5,
					sun.getPivotPoint().y));
        }
        if (pressedKeys.contains(KeyEvent.VK_L)) {
        	sun.setPivotPoint(new Point(sun.getPivotPoint().x + 5,
					sun.getPivotPoint().y));
        }

        if (pressedKeys.contains(KeyEvent.VK_V)) {
        	sun.setCurAlpha(toggleAlpha(sun.getCurAlpha(), sun.getOldAlpha()));
        }
        if (pressedKeys.contains(KeyEvent.VK_Z)) {
        	sun.setCurAlpha(scaleAlpha(sun.getCurAlpha(), .01f));
        }
        if (pressedKeys.contains(KeyEvent.VK_X)) {
        	sun.setCurAlpha(scaleAlpha(sun.getCurAlpha(), -.01f));
        }
        if (pressedKeys.contains(KeyEvent.VK_Q)) {
        	sun.setScaleX(scaleHelper(sun.getScaleX(), .005));
        	sun.setScaleY(scaleHelper(sun.getScaleY(), .005));
        }
        if (pressedKeys.contains(KeyEvent.VK_W)) {
        	sun.setScaleX(scaleHelper(sun.getScaleX(), -.005));
        	sun.setScaleY(scaleHelper(sun.getScaleY(), -.005));
        }

	}
	
	/**
	 * Engine automatically invokes draw() every frame as well. If we want to make sure mario gets drawn to
	 * the screen, we need to make sure to override this method and call mario's draw method.
	 * */
	@Override
	public void draw(Graphics g){
		super.draw(g);
		
		/* Same, just check for null in case a frame gets thrown in before Mario is initialized */
		if(sun != null) sun.draw(g);
	}
	private void setRotations(int multiplier, int adjMultiplier) {
		sun.setRotation(rotationHelper(sun.getRotation(),multiplier* ROTATION_CONSTANT));
		earth.setRotation(rotationHelper(earth.getRotation(), multiplier*ROTATION_CONSTANT));
		mars.setRotationAdj(ROTATION_CONSTANT * adjMultiplier);
	}


	/**
	 * Quick main class that simply creates an instance of our game and starts the timer
	 * that calls update() and draw() every frame
	 * */
	public static void main(String[] args) {
		LabThreeGame game = new LabThreeGame();
		game.start();

	}

	private int rotationHelper(int rotation, int increment) {

		return (rotation+increment);
	}

	private float toggleAlpha(float curAlpha, float oldAlpha){
		if (curAlpha == 0.0f){
			return oldAlpha;
		}
		else{
			return 0.0f;
		}
	}

	private float scaleAlpha(float curAlpha, float scaleFactor){
		if (0f > (curAlpha + scaleFactor)){
			return 0.0f;
		}
		else if ((curAlpha + scaleFactor) > 1f ){
			return 1.0f;
		}
		else {
			return (curAlpha + scaleFactor);
		}

	}

	private double scaleHelper(double scale, double adjust){
		if ((scale + adjust) <= 0){
			return scale;
		}
		return (scale+adjust);
	}

}
