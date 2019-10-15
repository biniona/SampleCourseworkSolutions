package edu.virginia.oldGames;

import edu.virginia.engine.display.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * Example game that utilizes our engine. We can create a simple prototype game with just a couple lines of code
 * although, for now, it won't be a very fun game :)
 * */
public class LabFourGame extends Game{

	/* Create a sprite object for our game. We'll use mario */
    String[] marioFiles = {"Mario.png", "Mario_walk.png", "Mario_jump.png"};
    private AnimatedSprite mario = new AnimatedSprite("Mario", "Mario.png", new Point(0,0), marioFiles);
    private Sprite points = new Sprite("points", "points.png");
    private Sprite losePoints = new Sprite("LosePoints", "losePoints.png");
    private Sprite win = new Sprite("Win", "win.png");
    private SoundManager soundManager = new SoundManager();
    private Sprite grass = new Sprite("grass", "grass.png");
    private Sprite grass2 = new Sprite("grass2", "grass.png");
    private DisplayObject SinWave = new DisplayObject("SinWave");

    private int pointsVal = 0;
    private boolean won = false;
    //2 bounce variables prevent an infinite bounce
    private boolean bounce = true;
    private  boolean bounceLOCK = true;

    private final DisplayObject[] nonSpriteDrawable = {
            this.SinWave
    };

    private final Sprite[] sprites = {
            this.mario,
            this.points,
            this.losePoints,
            this.win,
            this.grass,
            this.grass2,
    };

    private final Sprite[] spritesWithPhysics = {
            mario,
            points,
            grass,
            grass2,
            losePoints
    };

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
            KeyEvent.VK_S,
            KeyEvent.VK_T,
            KeyEvent.VK_Y,
    };
	/**
	 * Constructor. See constructor in Game.java for details on the parameters given
	 * */
	public LabFourGame() {
	    //WIDTH HEIGHT
		super("Lab Four Test Game", 800, 500);
        Point pointsPos = new Point(250, 40);
        Point losePointsPos = new Point (0, 40);
        Point winPos  = new Point(600,40);
        Point marioPos  = new Point(0,300);
        Point grassPos  = new Point(-50,450);
        Point grass2Pos = new Point (500, 300);

        //set positions
        this.mario.setPosition(marioPos);
        this.points.setPosition(pointsPos);
        this.losePoints.setPosition(losePointsPos);
        this.win.setPosition(winPos);
        this.grass.setPosition(grassPos);
        this.grass2.setPosition(grass2Pos);
        //physics
        this.mario.setHasPhyiscs(true, false);
        this.points.setHasPhyiscs(true, true);
        this.grass.setHasPhyiscs(true, true);
        this.losePoints.setHasPhyiscs(true, true);
        this.grass2.setHasPhyiscs(true,true);
        //sounds
        this.soundManager.LoadMusic("steve", "wonder.wav");
        this.soundManager.LoadSoundEffect("crow", "crow.wav");
        this.soundManager.PlayMusic("steve");
        //text
        this.points.startText("0");
        //sinWave
        SineWave SW = new SineWave(800, 280,  200, 20);
        SW.setCycles(2);
        this.SinWave.setSinWave(SW);

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
		if(this.won) {
            return;
        }
        else {
            super.update(pressedKeys);

            /* Make sure mario is not null. Sometimes Swing can auto cause an extra frame to go before everything is initialized */
            if (mario != null) mario.update(pressedKeys);

            //if all the sprites exist
            boolean left = true;
            boolean right = true;
            boolean up = true;
            boolean down = true;
            if (sprites != null) {
                if (Arrays.asList(sprites).contains(null) == false) {


                    //physics loop
                    for(int k = 0; k < spritesWithPhysics.length; k++) {
                        sprites[k].setHitBox("RECTANGLE");
                        if (sprites[k] == mario) continue;
                        if (mario.collidesWith(spritesWithPhysics[k])){
                            String collisions = mario.whereIsIt(spritesWithPhysics[k]);
                            if (collisions.contains("BOTTOM")){
                                down = false;
                            }
                            if (collisions.contains("TOP")){
                                up = false;
                            }
                            if (collisions.contains("LEFT")){
                                left = false;
                            }
                            if (collisions.contains("RIGHT")){
                                right = false;
                            }
                        }

                    }

                    //gravity

                    //confusing logic if statement. It says that if you can't go down, but you
                    //have downwards momentum, then skip gravity code, in other words
                    //similarly, the second half says that if you can't go up, but mario wants to go up, then
                    //skip the gravity code and setUpVelocity to 0.
                    if((!(!down && (mario.getUpVelocity() > 0))) && !(!up && mario.getUpVelocity()<0)) {
                        mario.setPosition(new Point(mario.getPosition().x,
                                (mario.getPosition().y + mario.getUpVelocity())));
                        if (mario.getUpVelocity() < 8) {
                            mario.setUpVelocity(mario.getUpVelocity() + 1);
                        }
                        else{
                            bounceLOCK = true;
                        }
                        this.bounce = true;
                    }
                    else{
                        //bounceCode
                        if (this.bounce && bounceLOCK){
                            mario.setPosition(new Point(mario.getPosition().x,
                                    (mario.getPosition().y - 10)));
                            bounceLOCK = false;
                        }
                        mario.setUpVelocity(0);
                        this.bounce = false;
                    }


                    //sound tests
                    if (mario.collidesWith(points)) {
                        pointsVal += 1;
                        points.startText(Integer.toString(pointsVal));
                    }
                    if (mario.collidesWith(losePoints)) {
                        pointsVal -= 1;
                        points.startText(Integer.toString(pointsVal));
                        soundManager.PlaySoundEffect("crow");
                        soundManager.LoadSoundEffect("crow", "crow.wav");
                    }
                    if (mario.collidesWith(win)) {
                        won = true;
                        soundManager.PlaySoundEffect("crow");
                        points.startText("you win!!!!!");

                    }


                }
            }
            boolean keyFlag = false;

        /*
        I think this lets possibleKeyEvents exist, it was freaking sometimes out when i didn't
        have this line.
         */
            if (possibleKeyEvents == null) {
                return;
            }
            for (int i = 0; i < this.possibleKeyEvents.length; i++) {
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

                    if (pressedKeys.contains(KeyEvent.VK_UP) && up) {
                        if (!down) {
                            mario.setUpVelocity(-30);
                            mario.animate(2, 2);
                        }
                    }
                    if (pressedKeys.contains(KeyEvent.VK_DOWN) && down) {
                        mario.setPosition(new Point(mario.getPosition().x,
                                mario.getPosition().y + 5));
                    }
                    if (pressedKeys.contains(KeyEvent.VK_RIGHT) && right) {
                        //passing in Animation walk delcared above
                        mario.animate(walk);
                        mario.setPosition(new Point(mario.getPosition().x + 5,
                                mario.getPosition().y));
                    }
                    if (pressedKeys.contains(KeyEvent.VK_LEFT) && left) {
                        System.out.println("IN T");
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
                        if(!down) {
                            mario.setPosition(new Point(mario.getPosition().x,
                                    (mario.getPosition().y - 10)));
                        }
                    }
                    if (pressedKeys.contains(KeyEvent.VK_W)) {
                        mario.setRotation(rotationHelper(mario.getRotation(), -1));
                        if(!down) {
                            mario.setPosition(new Point(mario.getPosition().x,
                                    (mario.getPosition().y - 10)));
                        }
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
                        //cant get more than 3 times big and need room to grow
                        if((mario.getScaleX() < 3) && up) {
                            mario.setScaleX(scaleHelper(mario.getScaleX(), .1));
                            mario.setScaleY(scaleHelper(mario.getScaleY(), .1));
                            mario.setPosition(new Point(mario.getPosition().x,
                                    (mario.getPosition().y - 12)));
                        }

                    }
                    if (pressedKeys.contains(KeyEvent.VK_S)) {
                        if(mario.getScaleX() > .5) {
                            mario.setScaleX(scaleHelper(mario.getScaleX(), -.1));
                            mario.setScaleY(scaleHelper(mario.getScaleY(), -.1));
                        }
                    }
                    if (pressedKeys.contains(KeyEvent.VK_T)) {
                        //passing in Animation walk delcared above
                        System.out.println("IN T");
                        if(SinWave.getWaveSpeed() < 10) {
                            SinWave.setWaveSpeed(SinWave.getWaveSpeed() + 1);
                        }
                    }
                    if (pressedKeys.contains(KeyEvent.VK_Y)) {
                        //passing in Animation walk delcared above
                        System.out.println("IN Y");
                        if(SinWave.getWaveSpeed() > 0) {
                            SinWave.setWaveSpeed(SinWave.getWaveSpeed() - 1);
                        }
                    }
                    break;
                }
            }
            if (mario != null && !keyFlag) {
                mario.setPlaying(false);
            }
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
        if (sprites != null){

            if((Arrays.asList(sprites).contains(null) == false) && (Arrays.asList(nonSpriteDrawable).contains(null) == false)){
                /*
                for(int i = 0; i < sprites.length; i++){
                    sprites[i].draw(g);
                }
                */

                for(int i = 0; i < nonSpriteDrawable.length; i++){
                    nonSpriteDrawable[i].draw(g);
                }
            }
        }
    }


	/**
	 * Quick main class that simply creates an instance of our game and starts the timer
	 * that calls update() and draw() every frame
	 * */
	public static void main(String[] args) {
		LabFourGame game = new LabFourGame();
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
