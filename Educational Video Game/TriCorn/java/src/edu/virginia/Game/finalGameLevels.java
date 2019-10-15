package edu.virginia.Game;

import edu.virginia.engine.display.DisplayObject;
import edu.virginia.engine.display.DisplayObjectContainer;
import edu.virginia.engine.display.SineWave;
import edu.virginia.engine.display.Sprite;

import java.awt.*;

public class finalGameLevels {

    private finalGame game;

    public finalGameLevels(finalGame game){
        this.game = game;
    }

    protected void buildBasicLevel(DisplayObjectContainer levelN){
        //initialize necessary components of level
        Sprite boat;
        DisplayObjectContainer boatParent, levelParent, levelEnemies, level, SinWaveParent,head;
        DisplayObject sail, moon, SinWave,hair,health;
        boat = new Sprite("Boat", game.currBoat);
        boatParent = new DisplayObjectContainer("boatParent", "boatParent.png");
        levelParent = new DisplayObjectContainer("levelParent", "blankFile.png");
        levelEnemies = new DisplayObjectContainer("levelEnemies", "blankFile.png");
        level = new DisplayObjectContainer("level", "blankFile.png");
        head = new DisplayObjectContainer("head", "blankFile.png");
        hair = new DisplayObject("hair", game.currHair);
        SinWaveParent = new DisplayObjectContainer("SinWaveParent", "blankFile.png");
        sail = new DisplayObject("sail");
        moon = new DisplayObject("moon");
        health = new DisplayObject("health","hearts3.png");
        SinWave = new DisplayObject("sinWave");

        //give new level a defualt transform
        levelN.setDefaulTransform();

        //set up level hierarchy
        levelN.addChild(levelParent);
        levelN.addChild(health);
        levelParent.addChild(moon);
        levelParent.addChild(level);
        level.addChild(boatParent);
        level.addChild(SinWaveParent);
        level.addChild(levelEnemies);
        SinWaveParent.addChild(SinWave);
        boatParent.addChild(boat);
        boatParent.addChild(sail);
        boatParent.addChild(head);
        head.addChild(hair);

        //setUpAvatarSkinColor
        head.setHeadColor(game.currSkin);
        head.setScaleX(.25);
        head.setScaleY(.25);
        Point avatarPos = new Point(50,85);
        head.setPosition(avatarPos);

        //set up basic level arrangement
        Point healthPos = new Point(game.SCREENWIDTH - 300, 0);
        Point boatParentPos = new Point(100, 0);
        Point boatPivotPoint = new Point (36,60);
        Point boatParentPivotPoint = new Point (36,118);
        Point SinWaveParentPoint = new Point (-800, 0);
        health.setPosition(healthPos);
        boatParent.setPosition(boatParentPos);
        boat.setPivotPoint(boatPivotPoint);
        boatParent.setPivotPoint(boatParentPivotPoint);
        SinWaveParent.setPosition(SinWaveParentPoint);
        SineWave SW = new SineWave(game.SCREENWIDTH*3, (game.SCREENHEIGHT-game.ADJ-20),  game.ADJ, 10);
        SW.setCycles(3);
        SinWave.setSinWave(SW);

        //construct basic level triangle
        Point[] sailPoints = {new Point(50, -20), new Point(-10, 80),new Point(100, 80)};
        sail.setTriangle(game.getScenePanel().newTriangle(sail,sailPoints));
        Point[] moonPoints = {new Point(45, 20), new Point(20, 70),new Point(75, 70)};
        moon.setTriangle(game.getScenePanel().newTriangle(moon,moonPoints));

    }

    protected void buildCharacterSelectionScreen(DisplayObjectContainer levelN){
        //default
        levelN.setDefaulTransform();

        //initialization
        Sprite boat;
        DisplayObject sail, button1, button2, button2Point5, button3, hair;
        DisplayObjectContainer boatParent, head;
        boat = new Sprite("boat", "boat.png");
        boatParent = new DisplayObjectContainer("boatParent", "boatParent.png");
        sail = new DisplayObject("sail");
        button1 = new DisplayObject("button1");
        button2 = new DisplayObject("button2");
        button3 = new DisplayObject("button3");
        button2Point5 = new DisplayObject("button2.5");
        head = new DisplayObjectContainer("head", "blankFile.png");
        hair = new DisplayObject("hair", game.currHair);

        //hierarchy
        levelN.addChild(boatParent);
        boatParent.addChild(boat);
        boatParent.addChild(sail);
        levelN.addChild(head);
        head.addChild(hair);

        //add buttons
        levelN.addChild(button1);
        levelN.addChild(button2);
        levelN.addChild(button2Point5);
        levelN.addChild(button3);

        //headInitialization
        head.setHeadColor(game.currSkin);

        //triangles
        Point[] sailPoints = {new Point(50, -20), new Point(-10, 80),new Point(100, 80)};
        sail.setTriangle(game.getScenePanel().newTriangle(sail,sailPoints));
        Point[] buttonPoints = {new Point(50, -20), new Point(-10, 80),new Point(100, 80)};
        Point[] buttonPointsSmall = {new Point(50/2, -20/2), new Point(-10/2, 80/2),new Point(100/2, 80/2)};

        //buttonTriangles
        button1.setTriangle(game.getScenePanel().newTriangle(button1,buttonPoints));
        button1.getTriangle().setIsButton();
        button2.setTriangle(game.getScenePanel().newTriangle(button2,buttonPointsSmall));
        button2.getTriangle().setIsButton();
        button2Point5.setTriangle(game.getScenePanel().newTriangle(button2Point5,buttonPointsSmall));
        button2Point5.getTriangle().setIsButton();
        button3.setTriangle(game.getScenePanel().newTriangle(button3,buttonPoints));
        button3.getTriangle().setIsButton();

        //Button positions
        int numberOfButtons = 4;
        Point button1Pos = new Point((game.SCREENWIDTH/numberOfButtons) - 100, (3*game.SCREENHEIGHT)/4 );
        Point button2Point5Pos = new Point((2*game.SCREENWIDTH)/numberOfButtons - 100, (6*game.SCREENHEIGHT)/7 );
        Point button2Pos = new Point((2*game.SCREENWIDTH)/numberOfButtons - 100, (3*game.SCREENHEIGHT)/4 );
        Point button3Pos = new Point((3*game.SCREENWIDTH)/numberOfButtons - 100, (3*game.SCREENHEIGHT)/4 );

        button1.setPosition(button1Pos);
        button2.setPosition(button2Pos);
        button2Point5.setPosition(button2Point5Pos);
        button3.setPosition(button3Pos);

        //Other Positions
        Point boatPos = new Point(game.SCREENWIDTH/numberOfButtons - 100, game.SCREENHEIGHT/2 - 100);
        boatParent.setPosition(boatPos);
        Point avatarPos = new Point((game.SCREENWIDTH*3)/numberOfButtons - 100, game.SCREENHEIGHT/2 - 100);
        head.setPosition(avatarPos);

    }


    protected void constructLevel0Enemies(DisplayObjectContainer levelN){
        for(int i = 0; i < game.NUMENEMIES[0]; i++){
            DisplayObjectContainer newEnemy;
            if ((i % 3) == 0) {
                newEnemy = new DisplayObjectContainer("triagle" + i);
                Point[] points = {new Point(50+20, -20), new Point(0+20, 80),new Point(100+20, 80)};
                newEnemy.setTriangle(game.getScenePanel().newTriangle(newEnemy,points));
                //this has to happen after initialization
            }
            else{
                newEnemy = new DisplayObjectContainer("square" + i, "squareEnemy.png");
            }
            Point enemyPos = new Point((int)((game.SCREENWIDTH*3)  * (i+1)), -1000 + (int)(Math.random() *  1500) );
            newEnemy.setPosition(enemyPos);
            DisplayObjectContainer levelEnemies = (DisplayObjectContainer) levelN.getChild("levelEnemies");
            levelEnemies.addChild(newEnemy);
        }
    }

    protected void constructLevel1Enemies(DisplayObjectContainer levelN){
        Sprite isosceles = new Sprite("isosceles", "isosceles.png");
        for(int i = 0; i < game.NUMENEMIES[1]; i++) {
            DisplayObjectContainer newEnemy;
            if(i%3 < 1){
                newEnemy = new DisplayObjectContainer("dragTriagle" + i);
                Point[] dragPoints = {new Point(50 + 20, -20), new Point(0 + 20, -60), new Point(100 + 20, 80)};
                newEnemy.setCircleTriangle(game.getScenePanel().newCirlceTriangle(newEnemy, dragPoints, game.RADIUS), game.RADIUS);
                boolean[] draggable = {false, true, false};
                newEnemy.getCircleTriangle().setDraggable(draggable);
                newEnemy.getCircleTriangle().setIsoscelesEnemy();
                newEnemy.addChild(isosceles);

            }
            else{
                newEnemy = new DisplayObjectContainer("square" + i, "squareEnemy.png");

            }
            //this has to happen after initialization
            Point enemyPos = new Point((int)((game.SCREENWIDTH*2)  * (i+1)), -1000 + (int)(Math.random() *  1500) );
            newEnemy.setPosition(enemyPos);
            DisplayObjectContainer levelEnemies = (DisplayObjectContainer) levelN.getChild("levelEnemies");
            levelEnemies.addChild(newEnemy);
        }
    }


    protected void constructLevel2Enemies(DisplayObjectContainer levelN){
        DisplayObjectContainer dragons, dragon1, dragon2;
        DisplayObject dragon1Head,dragon2Head;
        dragons = new DisplayObjectContainer("dragons", "blankFile.png");
        dragon1 = new DisplayObjectContainer("dragon1", "dragon1.png");
        dragon2 = new DisplayObjectContainer("dragon2", "dragon2.png");
        dragon1Head = new DisplayObjectContainer("dragon1Head");
        dragon2Head = new DisplayObjectContainer("dragon2Head");

        dragons.addChild(dragon1);
        dragons.addChild(dragon2);
        dragon1.addChild(dragon1Head);
        dragon2.addChild(dragon2Head);
        dragons.setScaleX(3);
        dragons.setScaleY(3);
        //build dragon 1 head
        Point[] dragPoints1 = {new Point(-30, 80), new Point(90, 30), new Point(100, 170)};
        dragon1Head.setCircleTriangle(game.getScenePanel().newCirlceTriangle(dragon1Head, dragPoints1, game.RADIUS), game.RADIUS);
        boolean[] draggable1 = {true, true, false};
        dragon1Head.getCircleTriangle().setDraggable(draggable1);
        dragon1Head.getCircleTriangle().setIsDragonClickable();
        //build dragon 2 head
        Point[] dragPoints2 = {new Point(0, 10), new Point(150, -90), new Point(200, 100)};
        dragon2Head.setCircleTriangle(game.getScenePanel().newCirlceTriangle(dragon2Head, dragPoints2, game.RADIUS), game.RADIUS);
        boolean[] draggable2 = {false, false, false};
        dragon2Head.getCircleTriangle().setDraggable(draggable2);
        dragon2Head.getCircleTriangle().setIsDragonStatic();



        //add dragons to levelEnemies
        DisplayObjectContainer levelEnemies = (DisplayObjectContainer) levelN.getChild("levelEnemies");
        levelEnemies.addChild(dragons);


        Point dragonPos = new Point((int)(game.SCREENWIDTH * 1.2), -200);
        dragons.setPosition(dragonPos);



    }


}
