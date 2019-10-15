package edu.virginia.engine.display;

public class Animation {

    private String id;
    private int startFrame;
    private int endFrame;

    public Animation(String id, int startFrame, int endFrame){
        this.setId(id);
        this.setStartFrame(startFrame);
        this.setEndFrame(endFrame);
    }

    //getters
    public String getId(){
        return this.id;
    }
    public int getStartFrame(){
        return this.startFrame;
    }
    public int getEndFrame(){
        return this.endFrame;
    }
    //setters
    public void setId(String id){
        this.id = id;
    }
    public void setStartFrame(int startFrame){
        this.startFrame = startFrame;
    }
    public void setEndFrame(int endFrame){
        this.endFrame = endFrame;
    }
}
