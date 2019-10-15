package edu.virginia.engine.display;

import java.awt.*;

public class Gradient {
    Color c1;
    Color c2;
    int min;
    int max;

    public Gradient(Color c1, Color c2, int min, int max){
        this.c1 = c1;
        this.c2 = c2;
        this.min = min;
        this.max = max;
    }

    public Color getColorFromGradient(float value){

        if((value > max) || (value < min)){
            System.out.println("VALUE: " + value);
            return Color.GRAY;
        }
        float ratio = value / (float) (max - min);
        int red = (int) (c2.getRed() * ratio + c1.getRed() * (1 - ratio));
        int green = (int) (c2.getGreen() * ratio + c1.getGreen() * (1 - ratio));
        int blue = (int) (c2.getBlue() * ratio + c1.getBlue() * (1 - ratio));
        Color newColor = new Color(red,green,blue);
        return newColor;
    }


}
