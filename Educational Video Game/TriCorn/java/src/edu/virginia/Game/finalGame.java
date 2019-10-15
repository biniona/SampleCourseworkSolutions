package edu.virginia.Game;

import edu.virginia.engine.display.*;

import java.awt.*;
import java.util.ArrayList;

/**
 * Example game that utilizes our engine. We can create a simple prototype game with just a couple lines of code
 * although, for now, it won't be a very fun game :)
 * */
public class finalGame extends Game{

    /* Create a sprite object for our game. We'll use mario */
    //private AnimatedSprite character = new AnimatedSprite("Boat", "boat.png", new Point(0,0));
    private SoundManager soundManager = new SoundManager();

    //reference to all of the games motion functions
    private motionLibrary ml;

    //important constants
    public int level = -1;
    public boolean won = false;
    public boolean transition = false;
    public boolean alive = true;
    public int health = 3;
    public int skinCounter = 1;
    public Color currSkin;
    public Gradient skinGradient;
    public int boatCounter = 0;
    public String currBoat = "boat.png";
    public int hairCounter = 0;
    public String currHair = "neutral.png";
    public final String[] hairs = {"neutral.png","man.png","woman.png"};
    public final String[] boats = {"boat.png", "boat1.png", "boat2.png"};
    public final int RADIUS = 30;
    public final int[] NUMENEMIES = {4, 10, 20};
    public static final int SCREENHEIGHT = 500;
    public static final int SCREENWIDTH = 800;
    public static final int ADJ = 200;
    public static final int ENEMYSPEED[] = {10, 6, 0};
    public boolean winFaded = false;
    //FOR testing a specific level
    //public static final int ENEMYSPEED[] = {400, 400, 0};


    public DisplayObjectContainer[] currLevel = new DisplayObjectContainer[1];

    /**
     * Constructor. See constructor in Game.java for details on the parameters given
     * */
    public finalGame() {
        super("Tricorn Game", SCREENWIDTH, SCREENHEIGHT);

        ml = new motionLibrary(this);
        Color darkSkin = Color.decode("#3F2D26");
        Color lightSkin = Color.decode("#F8DFB2");
        currSkin = darkSkin;
        this.skinGradient = new Gradient(darkSkin, lightSkin, 1,10);

        //Make CharacterSelection Screen
        buildLevel(level);

        //load music and play music
        this.soundManager.LoadMusic("flute", "flute.wav");
        this.soundManager.PlayMusic("flute");


    }

    /**
     * Engine will automatically call this update method once per frame and pass to us
     * the set of keys (as strings) that are currently being pressed down
     * */

    @Override
    public void update(ArrayList<Integer> pressedKeys){
        if(currLevel[0] == null){
            return;
        }
        else if(!this.alive){
            //currLevel[0].startText("YOU LOSE!!!!!");
            restart();
        }
        else if(this.won){
            winner();
        }
        //this is the code for transitioning between levels
        else if(this.transition) {
            transitionLevel();
        }
        //this is the code for executing each level
        else {
            //level -1 is character selection screen
            if(level == -1){
                ml.characterSelection();
            }
            //level0, intro level. Few enemies, and the are squares.
            //get a handle of the controls.
            else if(level == 0) {
                ml.motion();
                ml.checkHealth(currLevel[0]);
                if(fadedin(currLevel[0])){
                    ml.enemies(currLevel[0], 0);
                }

            }
            //level1 triangular enemies
            //make them isosoles to beat them
            else if(level == 1) {
                ml.motion();
                ml.checkHealth(currLevel[0]);
                if(fadedin(currLevel[0])){
                    ml.enemies(currLevel[0], 1);
                }
            }
            //boss battle level, Triangle similarity!
            else if(level == 2) {
                ml.motion();
                ml.checkHealth(currLevel[0]);
                if(fadedin(currLevel[0])){
                    ml.enemies(currLevel[0], 2);
                }
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
        if (currLevel[0] != null){
                for(int i = 0; i <  currLevel.length; i++){
                    currLevel[i].draw(g);
            }
        }
    }


    /**
     * Quick main class that simply creates an instance of our game and starts the timer
     * that calls update() and draw() every frame
     * */
    public static void main(String[] args) {
        finalGame game = new finalGame();
        game.start();
    }

    private void transitionLevel(){
        //if you beat level 0, make level 1 and play level 1
        ml.motion();
        this.health = 3;
        if (this.level == -1 && fadedout(this.currLevel[0])){
            buildLevel(0);
            return;
        }
        if (this.level == 0 && fadedout(this.currLevel[0])){
            buildLevel(1);
            return;
        }
        if (this.level == 1 && fadedout(this.currLevel[0])){
            buildLevel(2);
            return;
        }
        //fade out the level
        this.currLevel[0].setCurAlpha(scaleAlpha(this.currLevel[0].getCurAlpha(), -.01f));
    }

    private void restart(){
        ml.motion();
        currLevel[0].startText("YOU LOST!");
        if(fadedout(this.currLevel[0])) {
            buildLevel(this.level);
            this.alive = true;
            this.health = 3;
            return;
        }
        this.currLevel[0].setCurAlpha(scaleAlpha(this.currLevel[0].getCurAlpha(), -.01f));

    }

    private void winner(){
        currLevel[0].startText("");
        ml.motion();
        if(fadedout(this.currLevel[0]) || this.winFaded) {
            this.winFaded = true;
            fadedin(this.currLevel[0]);
            currLevel[0].removeAll();
            currLevel[0].setImage("winner.png");
            return;
        }
        this.currLevel[0].setCurAlpha(scaleAlpha(this.currLevel[0].getCurAlpha(), -.01f));

    }

    private void buildLevel(int levelNum){

        if(levelNum == -1){
            DisplayObjectContainer characterSelectionScreen = new DisplayObjectContainer("level1", "blankFile.png");
            finalGameLevels fgl = new finalGameLevels(this);
            fgl.buildCharacterSelectionScreen(characterSelectionScreen);
            characterSelectionScreen.startText("Select boat and avatar with first 3 red buttons. Start with right most red button");
            currLevel[0] = characterSelectionScreen;
        }

        else if(levelNum == 0){
            this.transition = false;
            level = 0;
            DisplayObjectContainer level0 = new DisplayObjectContainer("level1", "blankFile.png");
            finalGameLevels fgl = new finalGameLevels(this);
            fgl.buildBasicLevel(level0);
            fgl.constructLevel0Enemies(level0);
            level0.startText("level 0");
            currLevel[0] = level0;
        }

        else if(levelNum == 1) {
            this.transition = false;
            level = 1;
            DisplayObjectContainer level1 = new DisplayObjectContainer("level2", "blankFile.png");
            finalGameLevels fgl = new finalGameLevels(this);
            fgl.buildBasicLevel(level1);
            fgl.constructLevel1Enemies(level1);
            level1.setCurAlpha(0f);
            level1.startText("level 1: 2 Equal Angles Will Defeat the Isosceles Birds");
            currLevel[0] = level1;
            return;
        }
        else if(levelNum == 2) {
            System.out.println("BUILDING LEVEL 2");
            this.transition = false;
            level = 2;
            DisplayObjectContainer level2 = new DisplayObjectContainer("level3", "blankFile.png");
            finalGameLevels fgl = new finalGameLevels(this);
            fgl.buildBasicLevel(level2);
            fgl.constructLevel2Enemies(level2);
            level2.setCurAlpha(0f);
            level2.startText("level 2: Dragons Hate To be Similar, Congruent Angles");
            currLevel[0] = level2;
            return;
        }
    }



    private boolean fadedout(DisplayObject level){
        if (level.getCurAlpha() < .001){
            return true;
        }
        return false;
    }
    private boolean fadedin(DisplayObject level){
        if (level.getCurAlpha() < 1f){
            level.setCurAlpha(scaleAlpha(level.getCurAlpha(), .01f));
            return false;
        }
        return true;
    }

    //helper function for dealing with opacity of objects
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



}