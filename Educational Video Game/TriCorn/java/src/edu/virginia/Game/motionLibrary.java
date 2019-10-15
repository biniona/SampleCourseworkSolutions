package edu.virginia.Game;


import edu.virginia.engine.display.DisplayObject;
import edu.virginia.engine.display.DisplayObjectContainer;
import edu.virginia.engine.display.SineWave;

import java.awt.*;
import java.awt.geom.Area;
import java.util.ArrayList;

public class motionLibrary {

    private finalGame game;

    public motionLibrary(finalGame fg){
     this.game =fg;
    }

    //function that handles enemy motion. enemies just move to the left and they are eventually
    //no more enemies left
    public void enemies(DisplayObjectContainer level, int levelNum){
        DisplayObjectContainer enemies = (DisplayObjectContainer) level.getChild("levelEnemies");
        ArrayList<DisplayObject> enemyList = enemies.getChildren();
        collisions(level, enemyList, enemies);
        if(enemies != null){
            Point enemyPos = enemies.getPosition();
            if (levelNum == 0) {
                enemies.setPosition(new Point(enemyPos.x - game.ENEMYSPEED[levelNum], enemyPos.y));
                if (enemies.getPosition().x < -(300 + (game.SCREENWIDTH * 3 * (game.NUMENEMIES[levelNum] + 1)))) {
                    game.transition = true;
                }
            }
            if (levelNum == 1){
                enemies.setPosition(new Point(enemyPos.x - game.ENEMYSPEED[levelNum], enemyPos.y));
                if (enemies.getPosition().x < -(300 + (game.SCREENWIDTH * 2 * (game.NUMENEMIES[levelNum] + 2)))) {
                    game.transition = true;
                }
            }
            if (levelNum == 2){
                if (checkDragonsCongruent(level)){
                    game.won = true;
                }
            }
        }

    }
    //this is where collisions are dected AND where enemies are removed from enemies list
    //if they have been defeated
    public void collisions(DisplayObjectContainer level, ArrayList<DisplayObject> enemyList, DisplayObjectContainer enemiesParent){
        DisplayObject sail = level.getChild("sail");
        Polygon sailTriangle = sail.getTriangle().createClickableTriangle();
        Area sailArea = new Area(sailTriangle);
        for (int i = 0; i < enemyList.size(); i++ ){

            DisplayObject enemy = enemyList.get(i);
            if (enemy.getIsCircleTriangle()){
                enemy.getCircleTriangle().setAngleProperties();
                if(enemy.getCircleTriangle().getIsosceles()){
                    enemiesParent.removeChildAtIndex(i);
                    break;
                }
                Polygon enemyTriangle = enemy.getCircleTriangle().createClickableTriangle();
                Area enemyArea = new Area(enemyTriangle);
                enemyArea.intersect(sailArea);
                if (!enemyArea.isEmpty()){
                    System.out.println("COLLISION");
                    enemiesParent.removeChildAtIndex(i);
                    game.health--;
                    i--;
                    break;
                }
            }

            else if (enemy.getIsTriangle()){
                Polygon enemyTriangle = enemy.getTriangle().createClickableTriangle();
                Area enemyArea = new Area(enemyTriangle);
                enemyArea.intersect(sailArea);
                if (!enemyArea.isEmpty()){
                    System.out.println("COLLISION");
                    enemiesParent.removeChildAtIndex(i);
                    i--;
                    game.health--;
                    break;
                }
            }

            else {
                enemy.setHitBox("RECTANGLE");
                Shape hitBox = enemy.getHitBox();
                if(hitBox != null) {
                    Area enemyArea = new Area(hitBox);
                    enemyArea.intersect(sailArea);
                    if (!enemyArea.isEmpty()) {
                        System.out.println("COLLISION");
                        enemiesParent.removeChildAtIndex(i);
                        i--;
                        game.health--;
                        break;
                    }
                }

            }
        }
    }
    //this is the function controls the waves, boat, and moon
    public void motion(){
        DisplayObject SinWave = game.currLevel[0].getChild("sinWave");
        DisplayObject sail = game.currLevel[0].getChild("sail");
        DisplayObject moon = game.currLevel[0].getChild("moon");
        DisplayObjectContainer level = (DisplayObjectContainer) game.currLevel[0].getChild("level");
        DisplayObjectContainer boatParent = (DisplayObjectContainer) game.currLevel[0].getChild("boatParent");
        SineWave SW = null;
        if (SinWave != null) { SW = SinWave.getSinWave();}
        if (SW != null) {
            //BOAT RIDING THE WAVES
            if (SW.isLineReady()) {
                int SinWaveYs[] = SW.getYCoords();
                int SWRots[] = SW.getRotations();
                boatParent.setPosition(new Point(boatParent.getPosition().x , SinWaveYs[boatParent.getPosition().x] - 118));
                boatParent.setRotation((SWRots[boatParent.getPosition().x]));
            }
            //SAIL SIZE TO WAVE SPEED
            SinWave.setWaveSpeed((float)(sail.getScaleX()*.5));
            //Moon
            if(moon != null){
                SW.setHeight((int)((game.SCREENHEIGHT-game.ADJ-20) * moon.getScaleX()), (game.SCREENHEIGHT - 20 -(int)((game.SCREENHEIGHT-game.ADJ-20) * moon.getScaleX())));
            }
            levelScaling(moon, level, sail);
        }
    }

    //handles scaling so you can always see the boat and the waves
    //this is called by the motion function.
    private void levelScaling(DisplayObject moon, DisplayObjectContainer level, DisplayObject sail){
        DisplayObject scaleObject = moon;
        if (sail.getScaleX() > moon.getScaleX()) scaleObject = sail;
        level.setScaleX(1 / scaleObject.getScaleX());
        level.setScaleY(1 / scaleObject.getScaleY());
        level.setPosition(new Point((int) ((level.getScaleX() - 1) * -200), (int) ((level.getScaleX() - 1) * -400)));
    }

    public void checkHealth(DisplayObjectContainer level){
        if (game.health > 0){
            game.alive = true;
        }
        else{
            game.alive = false;
        }
        DisplayObject healthBar = level.getChild("health");
        if(game.health == 3){
            healthBar.setImage("hearts3.png");
        }
        else if(game.health == 2){
            healthBar.setImage("hearts2.png");
        }
        else if(game.health == 1){
            healthBar.setImage("hearts1.png");
        }
        else if(game.health == 0){
            healthBar.setImage("heartsNone.png");
        }
    }

    public void characterSelection(){
        if(checkButtonClicked(game.currLevel[0],"button1")){
            changeBoatIcon(game.currLevel[0]);
        }
        else if(checkButtonClicked(game.currLevel[0],"button2")){
            changeHairIcon(game.currLevel[0]);
        }
        else if(checkButtonClicked(game.currLevel[0],"button2.5")){
            updateSkin(game.currLevel[0]);
        }
        else if(checkButtonClicked(game.currLevel[0],"button3")){
            game.transition = true;
        }
    }

    public void updateSkin(DisplayObjectContainer levelN){
        game.skinCounter++;
        game.currSkin = game.skinGradient.getColorFromGradient(game.skinCounter%10);
        DisplayObject head = levelN.getChild("head");
        head.setHeadColor(game.currSkin);
    }

    public void changeBoatIcon(DisplayObjectContainer levelN){
        game.boatCounter++;
        String boatString = game.boats[game.boatCounter%game.boats.length];
        DisplayObject boat = levelN.getChild("boat");
        System.out.println(boatString);
        boat.setImage(boatString);
        game.currBoat = boatString;
    }

    public void changeHairIcon(DisplayObjectContainer levelN){
        game.hairCounter++;
        String hairString = game.hairs[game.hairCounter%game.hairs.length];
        DisplayObject hair = levelN.getChild("hair");
        System.out.println(hairString);
        hair.setImage(hairString);
        game.currHair = hairString;
    }

    public boolean checkButtonClicked(DisplayObjectContainer levelN, String buttonString){
        DisplayObject button = levelN.getChild(buttonString);
        if(button == null){
            System.out.println("NO BUTTON: " + buttonString);
        }
        else if(button.getTriangleClicked()){
            button.setTriangleClicked(false);
            return true;
        }
        return false;
    }

    public boolean checkDragonsCongruent(DisplayObjectContainer levelN){
        DisplayObject dragonHead1 = levelN.getChild("dragon1Head");
        DisplayObject dragonHead2 = levelN.getChild("dragon2Head");
        int[] angles = dragonHead2.getCircleTriangle().getAngles();
        dragonHead1.getCircleTriangle().setAngleProperties(angles);
        return dragonHead1.getCircleTriangle().getIsCongruentDragonClickable();
    }


}
