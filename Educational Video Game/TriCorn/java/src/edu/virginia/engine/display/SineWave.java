package edu.virginia.engine.display;


public class SineWave {

    static int STATICSCALEFACTOR = 100;
    int SCALEFACTOR = STATICSCALEFACTOR;
    int cycles;
    int points;
    double[] sines;
    int[] yCoords;
    int[] xCoords;
    int[] rotations;
    boolean ready = false;
    boolean lineReady = false;
    int Height;
    int Width;
    int Adj;
    int bobHeight = 0 ;
    float currBobHeight = 0f;
    float bobAdjust = .5f;
    boolean xyInit = false;
    int[][] sinWaveLines;

    public int[] getYCoords(){
        return yCoords;
    }
    public int[] getRotations(){
        return this.rotations;
    }

    public boolean isLineReady(){
        return lineReady;
    }

    public SineWave(int Width, int Height, int Adj, int bobHeight){
        this.Height = Height;
        this.Width = Width;
        this.Adj = Adj;
        this.bobHeight = bobHeight;
    }

    public int getHeight(){
        return this.Height;
    }

    public void setHeight(int Height, int Adj){
        this.Height = Height;
        this.Adj = Adj;
    }


    public  void setCycles(int cycles) {
        this.cycles = cycles;
        System.out.println(SCALEFACTOR);
        this.points = SCALEFACTOR * cycles * 2;
        this.sines = new double[points];
        for (int i = 0; i < points; i++) {
            double radians = (Math.PI / SCALEFACTOR) * i;
            this.sines[i] = Math.sin(radians);
        }
        this.ready = true;
    }

    public void CreateLine(int loop) {
        int maxWidth = this.Width;
        double hstep = (double) maxWidth / (double) points;
        int maxHeight = this.Height;
        int[] pts;
        int[] ptsX;
        pts = new int[points];
        ptsX = new int[points];
        for (int i = 0; i < points; i++){
            ptsX[i] = i;
            pts[i] = (int) (sines[(i+loop)%points] * maxHeight / 2 * .95 + maxHeight / 2);
            //pts[i] = (int) (sines[(i)%points] * maxHeight / 2 * .95 + maxHeight / 2);

        }
        if(!xyInit){
            //xCoords = new int[ma];
            yCoords = new int[maxWidth];
            rotations = new int[maxWidth];
        }

        sinWaveLines = new int[points][4];
        for (int i = 1; i < points; i++) {
            int x1,x2,y1,y2;
            sinWaveLines[i][0] = x1 = (int) ((i - 1) * hstep); //x1
            sinWaveLines[i][1] = y1 =  (pts[i - 1]) + (this.Adj) + (int) (this.currBobHeight); //y1
            sinWaveLines[i][2] = x2 = (int) (i * hstep); //x2
            sinWaveLines[i][3] = y2 =  pts[i] + (this.Adj) + (int) (this.currBobHeight);//y2
            int deltaX, deltaY;
            if(i > 10){
                deltaX = x2 - sinWaveLines[i-10][0];
                deltaY = y2 - sinWaveLines[i-10][1];
            }
            else {
                deltaX = x2 - x1;
                deltaY = y2 - y1;
            }

            int rad = (int) Math.toDegrees(Math.atan2(deltaY, deltaX));
            int yForBoat = (maxWidth/points) * i;
            for(int p = 0; p < maxWidth/points; p++){
                rotations[(yForBoat+(maxWidth-50)+p)%maxWidth] = rad;
                yCoords[yForBoat+p] = (pts[(i + 10)%points]) + (this.Adj);
            }

        }
        if (Math.abs(this.currBobHeight) == this.bobHeight) this.bobAdjust = -this.bobAdjust;
        this.currBobHeight = ((this.currBobHeight + this.bobAdjust));
        lineReady = true;
    }

}