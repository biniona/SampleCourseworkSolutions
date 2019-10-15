package edu.virginia.engine.display;

import java.awt.*;
import java.util.ArrayList;

public class DisplayObjectContainer extends DisplayObject {


    //is this list a tree?
    private ArrayList<DisplayObject> children;


    public DisplayObjectContainer(String id) {
        super(id);
        this.setIsContainer();
        children = new ArrayList<DisplayObject>();
    }

    public DisplayObjectContainer(String id, String fileName) {
        super(id,fileName);
        this.setIsContainer();
        children = new ArrayList<DisplayObject>();
    }

    public void addChild(DisplayObject child){
        if (this.children != null){
            this.children.add(child);
            child.setParent(this);
        }
    }

    public void addChildAtIndex(DisplayObject child, int index){
        if (this.children != null){
            this.children.add(index,child);
            child.setParent(this);
        }
    }

    public void removeChildAtIndex(int index){
        if (this.children != null){
            this.children.remove(index);
        }
    }

    public void removeChild(int index){
        if (this.children != null){
            this.children.remove(this.children.size() - 1);
        }
    }

    public void removeAll(){
        if (this.children != null){
            this.children.clear();
        }
    }

    public DisplayObject getChild(int index){
        if (this.children != null){
            this.children.get(index);
        }
        return null;
    }

    public DisplayObject getChild(String id){

        if (this.children != null){
            int length = this.children.size();
            for(int i = 0; i < length ; i++){
                DisplayObject child = this.children.get(i);

                if (id == child.getId()){
                    return child;
                }
                if(child.getIsContainer()){
                    DisplayObjectContainer childAsContainer = (DisplayObjectContainer) child;
                    DisplayObject childResult = childAsContainer.getChild(id);
                    if (childAsContainer.getChild(id) != null){
                        return childResult;
                    }
                }
            }
        }
        return null;
    }

    public ArrayList<DisplayObject> getChildren(){
        return this.children;
    }

    public boolean contains(DisplayObject displayObject){
        if (this.children != null){
            int length = this.children.size();
            String id = displayObject.getId();
            for(int i = 0; i < length ; i++){
                if (id == this.children.get(i).getId()){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void draw(Graphics g){
        super.draw(g);
        Graphics2D g2d = (Graphics2D) g;
        if (this.children != null && displayImage != null) {
            applyTransformations(g2d);
            int length = this.children.size();
            for (int i = 0; i < length; i++) {
                DisplayObject child =  this.children.get(i);
                if (child.getRotationAdjSet()){
                    double rotation = (Math.toRadians(this.getRotation()) * (child.getRotationAdj()-1));
                    child.setParentRotation(rotation);
                    g2d.rotate( rotation, this.getPivotPoint().x, this.getPivotPoint().y);
                    child.draw(g);
                    g2d.rotate(-rotation, this.getPivotPoint().x, this.getPivotPoint().y);

                }
                else {
                    child.draw(g);
                }
            }
            reverseTransformations(g2d);
        }
        //this is the code for if there is no image associated with display object container
        else if(this.children != null){
            applyTransformations(g2d);
            int length = this.children.size();
            for (int i = 0; i < length; i++) {
                DisplayObject child =  this.children.get(i);
                child.draw(g);
            }
            reverseTransformations(g2d);

        }
    }


    @Override
    public void update(ArrayList<Integer> pressedKeys){
        super.update(pressedKeys);
        if (this.children != null) {
            int length = this.children.size();
            for (int i = 0; i < length; i++) {
                DisplayObject child = this.children.get(i);
                child.update(pressedKeys);
            }
        }
    }

}
