package edu.virginia.engine.display;

import edu.virginia.engine.util.GameClock;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.awt.*;
import java.awt.image.BufferedImage;

public class AnimatedSprite extends Sprite {

    private ArrayList<Animation> animations;
    private boolean playing;
    private String fileName;
    private ArrayList<BufferedImage> frames;
    private int currentFrame;
    private int startFrame;
    private int endFrame;
    //where to initalize? VVVVVVV
    private static final int DEFAULT_ANIMATION_SPEED = 500;
    private int animationSpeed;
    private GameClock clock;



    public AnimatedSprite(String id, String imageFileName, Point position, String[] filesToAnimate) {
        super(id, imageFileName);
        //initialzing files
        String[] files = filesToAnimate;
        initializeFrames(files);
        initGameClock();
        this.playing = false;
        this.animationSpeed = DEFAULT_ANIMATION_SPEED;
    }

    public void setAnimationSpeed(int animationSpeed){
        this.animationSpeed = animationSpeed;
    }

    public int getAnimationSpeed(){
        return this.animationSpeed;
    }

    public void setAnimations(Animation animation, int i){
        if (this.animations == null){
            this.animations = new ArrayList<Animation>();
        }
        this.animations.add(i, animation);
    }

    public void setPlaying(Boolean playing){
        this.playing = playing;
    }

    private void initializeFrames(String[] files){
        this.frames = new ArrayList<BufferedImage>();
        for(int i = 0; i < files.length; i++){
            initializeFrame(files[i]);
        }
    }

    private void initializeFrame(String file){
        if (file == null) {
            return;
        }
        System.out.println(file + "\n");
        this.frames.add(this.readImage(file));
    }


    private Animation getAnimation(String id){
        for(int i = 0; i < this.animations.size(); i++){
            if (this.animations.get(i).getId() == id){
                return this.animations.get(i);
            }
        }
        return null;
    }

    public void initGameClock() {
        this.clock = new GameClock();
    }

    public void animate(Animation animation){
        this.playing = true;
        this.startFrame = animation.getStartFrame();
        this.endFrame = animation.getEndFrame();
    }

    public Animation animate(String id){
        this.playing = true;
        return getAnimation(id);
    }

    public void animate(int startFrame, int  endFrame){
        this.playing = true;
        this.startFrame = startFrame;
        this.endFrame = endFrame;
    }


    public BufferedImage stopAnimation(int FrameNumber){
        this.playing = false;
        return this.frames.get(FrameNumber);
    }

    public BufferedImage stopAnimation(){
        return this.stopAnimation(0);
    }

    @Override
    public void draw(Graphics g){

        if (clock.getElapsedTime() >= this.animationSpeed) {
                if (this.currentFrame < this.endFrame) {
                    this.currentFrame++;
                } else {
                    /*
                    I really don't know why i am using
                    stop animation, like couldn't i just

                    */
                    if (this.playing == false) {
                        this.setImage(this.stopAnimation());
                        super.draw(g);
                        return;
                    }
                    else{
                        this.currentFrame = this.startFrame;
                    }
                }

                this.setImage(this.frames.get(this.currentFrame));
                initGameClock();
        }


        super.draw(g);

    }
}