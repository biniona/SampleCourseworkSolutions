package edu.virginia.oldGames;

import edu.virginia.engine.display.AnimatedSprite;
import edu.virginia.engine.display.Animation;
import edu.virginia.engine.display.Game;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

/**
 * Example game that utilizes our engine. We can create a simple prototype game with just a couple lines of code
 * although, for now, it won't be a very fun game :)
 * */
public class LabTwoGame extends Game{

	/* Create a sprite object for our game. We'll use mario */
    String[] marioFiles = {"Mario.png", "Mario_walk.png", "Mario_jump.png"};
	AnimatedSprite mario = new AnimatedSprite("Mario", "Mario.png", new Point(0,0), marioFiles);
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
	public LabTwoGame() {
		super("Lab One Test Game", 500, 300);
	}

	/**
	 * Engine will automatically call this update method once per frame and pass to us
	 * the set of keys (as strings) that are currently being pressed down
	 * */

	private Animation walk = new Animation("Walk", 0,1);
    private Animation jump = new Animation("Jump", 2,2);

    private boolean arrListSet = false;

    @Override
	public void update(ArrayList<Integer> pressedKeys){
		super.update(pressedKeys);
		
		/* Make sure mario is not null. Sometimes Swing can auto cause an extra frame to go before everything is initialized */
        if(mario != null) mario.update(pressedKeys);

        boolean keyFlag = false;

        /*
        I think this lets possibleKeyEvents exist, it was freaking sometimes out when i didn't
        have this line.
         */
        if(possibleKeyEvents == null){return;}
        for(int i = 0 ; i < this.possibleKeyEvents.length ; i++) {
            if (pressedKeys.contains(this.possibleKeyEvents[i])) {

                keyFlag = true;
                //for whatever reason this makes mario skip out
                if (pressedKeys.contains(KeyEvent.VK_B)) {
                    mario.setAnimationSpeed(mario.getAnimationSpeed() + 10);
                }
                if (pressedKeys.contains(KeyEvent.VK_N)) {
                    if (mario.getAnimationSpeed() > 10) {
                        mario.setAnimationSpeed(mario.getAnimationSpeed() - 10);
                    }
                }

                if (pressedKeys.contains(KeyEvent.VK_UP)) {
                    mario.setPosition(new Point(mario.getPosition().x,
                            mario.getPosition().y - 5));
                    mario.animate(2, 2);

                }
                if (pressedKeys.contains(KeyEvent.VK_DOWN)) {
                    mario.setPosition(new Point(mario.getPosition().x,
                            mario.getPosition().y + 5));
                }
                if (pressedKeys.contains(KeyEvent.VK_RIGHT)) {
                    //passing in Animation walk delcared above
                    mario.animate(walk);
                    mario.setPosition(new Point(mario.getPosition().x + 5,
                            mario.getPosition().y));
                }
                if (pressedKeys.contains(KeyEvent.VK_LEFT)) {
                    mario.setPosition(new Point(mario.getPosition().x - 5,
                            mario.getPosition().y));
                }
                if (pressedKeys.contains(KeyEvent.VK_I)) {
                    mario.setPivotPoint(new Point(mario.getPivotPoint().x,
                            mario.getPivotPoint().y - 5));
                }
                if (pressedKeys.contains(KeyEvent.VK_K)) {
                    mario.setPivotPoint(new Point(mario.getPivotPoint().x,
                            mario.getPivotPoint().y + 5));
                }
                if (pressedKeys.contains(KeyEvent.VK_J)) {
                    mario.setPivotPoint(new Point(mario.getPivotPoint().x - 5,
                            mario.getPivotPoint().y));
                }
                if (pressedKeys.contains(KeyEvent.VK_L)) {
                    mario.setPivotPoint(new Point(mario.getPivotPoint().x + 5,
                            mario.getPivotPoint().y));
                }
                if (pressedKeys.contains(KeyEvent.VK_Q)) {
                    mario.setRotation(rotationHelper(mario.getRotation(), 1));
                }
                if (pressedKeys.contains(KeyEvent.VK_W)) {
                    mario.setRotation(rotationHelper(mario.getRotation(), -1));
                }
                if (pressedKeys.contains(KeyEvent.VK_V)) {
                    mario.setCurAlpha(toggleAlpha(mario.getCurAlpha(), mario.getOldAlpha()));
                }
                if (pressedKeys.contains(KeyEvent.VK_Z)) {
                    mario.setCurAlpha(scaleAlpha(mario.getCurAlpha(), .01f));
                }
                if (pressedKeys.contains(KeyEvent.VK_X)) {
                    mario.setCurAlpha(scaleAlpha(mario.getCurAlpha(), -.01f));
                }
                if (pressedKeys.contains(KeyEvent.VK_A)) {
                    mario.setScaleX(scaleHelper(mario.getScaleX(), .1));
                    mario.setScaleY(scaleHelper(mario.getScaleY(), .1));
                }
                if (pressedKeys.contains(KeyEvent.VK_S)) {
                    mario.setScaleX(scaleHelper(mario.getScaleX(), -.1));
                    mario.setScaleY(scaleHelper(mario.getScaleY(), -.1));
                }
                break;
            }
        }
        if (mario != null && !keyFlag){
            mario.setPlaying(false);
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
		if(mario != null) mario.draw(g);
	}


	/**
	 * Quick main class that simply creates an instance of our game and starts the timer
	 * that calls update() and draw() every frame
	 * */
	public static void main(String[] args) {
		LabTwoGame game = new LabTwoGame();
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
