package edu.virginia.engine.display;

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;

/**
 * A very basic display object for a java based gaming engine
 * 
 * */
public class DisplayObject {

	/* All DisplayObject have a unique id */
	private String id;
	private Point position;
	private Point pivotPoint;
	private int rotation;
	private Boolean visible;
	private float curAlpha;
	private float oldAlpha;
	private double scaleX;
	private double scaleY;
	private int rotationAdj = 0;
	private boolean rotationAdjSet = false;
	private DisplayObject parent;
	private int rotationDiff = 0;
	private double parentRotation = 0;
	private double rotationDiffConstant = 0;
	private Shape hitBox;
	private String text;
	private boolean writeText;
	private boolean hasPhysics = false;
	public static final int GRAVITY = 1;
	public static final int BOUNCINESS = 10;
	private boolean stationary;
	private int upVelocity = 0;
	private boolean isImage = true;
	private boolean isContainer = false;
	private boolean isHead = false;
	private Color headColor;
	private AffineTransform af;

	//TriangleValues
	private boolean isTriangle = false;
	private GameScenePanel.TrianglePanel triangle;
	private boolean triangleClicked = false;

	//CircularTriangle Values
	private boolean isCircleTriangle = false;
	private GameScenePanel.CircularTrianglePanel circleTriangle;
	private int triRadius;

	//Sin Wave Values
	private SineWave SW;
	private float waveSpeed = 1f;
	private float wavePos = 0f;
	private boolean isSinWave = false;


	/* The image that is displayed by this object */
	protected BufferedImage displayImage;
	protected int displayImageHeight;
	protected int displayImageWidth;


	public void setHeadColor(Color color){
		this.isHead = true;
		this.headColor = color;
	}

	public SineWave getSinWave(){
		return this.SW;
	}
	public void setWaveSpeed(float waveSpeed){
		this.waveSpeed = waveSpeed;
	}
	public float getWaveSpeed(){
		return this.waveSpeed;
	}

	public void setUpVelocity(int upVelocity){
		this.upVelocity = upVelocity;
	}

	public int getUpVelocity(){
		return this.upVelocity;
	}

	public void setIsContainer(){
		this.isContainer = true;
	}
	public boolean getIsContainer(){
		return this.isContainer;
	}

	public void setHasPhyiscs(boolean b, boolean stationary){
		this.hasPhysics = b;
		if (b == true){
			this.stationary = stationary;
		}
	}
	public boolean getHasPhyiscs(){
		return this.hasPhysics;
	}

	public void setTriangle(GameScenePanel.TrianglePanel triangle){
		this.isTriangle = true;
		this.triangle = triangle;
		Point myPoint = new Point(0,0);
		this.setPosition(myPoint);
		this.setPivotPoint(myPoint);
		this.setRotation(0);
		this.visible = true;
		this.curAlpha = 1.0f;
		this.oldAlpha = 0.0f;
		this.scaleX = 1.0;
		this.scaleY = 1.0;
	}

	public void setTriangleClicked(boolean triangleClicked){this.triangleClicked = triangleClicked;}
	public boolean getTriangleClicked(){return this.triangleClicked;}
	public GameScenePanel.TrianglePanel getTriangle(){return this.triangle;}
	public GameScenePanel.CircularTrianglePanel getCircleTriangle(){
		return this.circleTriangle;
	}
	public boolean getIsCircleTriangle(){return isCircleTriangle;}
	public boolean getIsTriangle(){return isTriangle;}
	public void setCircleTriangle(GameScenePanel.CircularTrianglePanel triangle, int triRadius){
		this.isCircleTriangle = true;
		this.circleTriangle = triangle;
		this.triRadius = triRadius;
		Point myPoint = new Point(0,0);
		this.setPosition(myPoint);
		this.setPivotPoint(myPoint);
		this.setRotation(0);
		this.visible = true;
		this.curAlpha = 1.0f;
		this.oldAlpha = 0.0f;
		this.scaleX = 1.0;
		this.scaleY = 1.0;
	}

	public void setIsImage(boolean isImage){
		this.isImage = isImage;
	}

	public void setSinWave(SineWave SW){
		this.isSinWave = true;
		this.SW = SW;
		System.out.println("SIN WAVE SET");
	}

	public String whereIsIt(DisplayObject collidingObject){
		int[] boundsObj = this.getBoundingPoints();
		int[] boundsColl = collidingObject.getBoundingPoints();
		final int DISTANCE = 20;
		StringBuilder retStringBuilder = new StringBuilder();
		if (Math.abs(boundsObj[0] - boundsColl[1]) < DISTANCE){
			retStringBuilder.append(" LEFT ");
		}
		if (Math.abs(boundsObj[1] - boundsColl[0]) < DISTANCE){
			retStringBuilder.append(" RIGHT ");
		}
		if (Math.abs(boundsObj[2] - boundsColl[3]) < (DISTANCE + 6)){
			retStringBuilder.append(" TOP ");
		}
		if (Math.abs(boundsObj[3] - boundsColl[2]) < DISTANCE){
			retStringBuilder.append(" BOTTOM ");
		}
		String retString = retStringBuilder.toString();
		return retString;
	}

	public int[] getBoundingPoints (){
		displayImageHeight = (int) (this.displayImage.getHeight() * this.scaleX) ;
		displayImageWidth = (int) (this.displayImage.getWidth() * this.scaleY);
		int[] bounds = new int[4];
		bounds[0] = this.position.x;
		bounds[1] = this.position.x + displayImageWidth;
		bounds[2] = this.position.y;
		bounds[3] = this.position.y + displayImageHeight;

		return bounds;
	}

	public Shape getHitBox(){
		return hitBox;
	}


	public void setHitBox(String shape){
			if (displayImage != null){
				if(shape == "RECTANGLE") {
					//adjusting in setHitBox
					displayImageHeight = (int) (this.displayImage.getHeight()) ;
					displayImageWidth = (int) (this.displayImage.getWidth());
					Point2D triPoint2d = new Point2D.Double(0 , 0);
					Point2D triPoint2d2 = new Point2D.Double(displayImageWidth, displayImageHeight);

					//try
					//{
						if(this.af!=null) {
							Point2D screenPoint2d = this.af.transform(triPoint2d, new Point2D.Double());
							Point2D screenPoint2d2 = this.af.transform(triPoint2d2, new Point2D.Double());
							Point screenPoint = new Point((int) screenPoint2d.getX(), (int) screenPoint2d.getY());
							Point screenPoint2 = new Point((int) screenPoint2d2.getX(), (int) screenPoint2d2.getY());
							Rectangle2D bounds = new Rectangle(screenPoint.x, screenPoint.y, screenPoint2.x, screenPoint2.y);
							this.hitBox = bounds;
							if(id.contains("80")){
								System.out.println(screenPoint);
							}
						}

					//}
					//catch (NoninvertibleTransformException e1) {
					//	e1.printStackTrace();
					//}


					//adjusting in draw
					//displayImageHeight = (int) (displayImage.getHeight()) ;
					//displayImageWidth = (int) (displayImage.getWidth());
					//Rectangle bounds = new Rectangle(0, 0, displayImageWidth, displayImageHeight);
					//AffineTransform tx = new AffineTransform();
					//tx.rotate(Math.toRadians(this.rotation), this.position.x, this.position.y);
					//Shape newShape = tx.createTransformedShape(bounds);
					//this.hitBox = newShape;

				}
			}
			//todo: make code for more shapes
			else{
				throw new java.lang.Error("ERROR: DID NOT ENTER VALID SHAPE");
			}
	}

	public void startText(String text){
		this.writeText = true;
		this.text = text;
	}

	public boolean collidesWith(DisplayObject other){
		if(hitBox != null){
			this.setHitBox("RECTANGLE");
			Area areaA = new Area(this.getHitBox());
			other.setHitBox("RECTANGLE");
			areaA.intersect(new Area(other.getHitBox()));
			return !areaA.isEmpty();
		}
		return false;
	}


	/**
	 * Constructors: can pass in the id OR the id and image's file path and
	 * position OR the id and a buffered image and position
	 */
	public DisplayObject(String id) {
		this.setId(id);
	}

	public DisplayObject(String id, String fileName) {
		this.setId(id);
		this.setImage(fileName);
		Point myPoint = new Point(0,0);
		this.setPosition(myPoint);
		this.setPivotPoint(myPoint);
		this.setRotation(0);
		this.visible = true;
        this.curAlpha = 1.0f;
        this.oldAlpha = 0.0f;
        this.scaleX = 1.0;
        this.scaleY = 1.0;
	}

	public void setDefaulTransform(){
		Point myPoint = new Point(0,0);
		this.setPosition(myPoint);
		this.setPivotPoint(myPoint);
		this.setRotation(0);
		this.visible = true;
		this.curAlpha = 1.0f;
		this.oldAlpha = 0.0f;
		this.scaleX = 1.0;
		this.scaleY = 1.0;
	}

	//setters

	public boolean getRotationAdjSet(){
		return this.rotationAdjSet;
	}

	public int getRotationAdj(){
		return this.rotationAdj;
	}


	public void setRotationAdj(int rotationAdj){
		if(this.rotationAdj != rotationAdj) {
			this.rotationDiffConstant = (rotationAdj * this.parentRotation) - (this.rotationAdj * this.parentRotation) ;
			System.out.println(this.rotationDiffConstant);
		}
		this.rotationAdj = rotationAdj;
		this.rotationAdjSet = true;
	}

	public double getRotationDiffConstant(){
		return this.rotationDiffConstant;
	}
	public void setParentRotation(double parentRotation){
		this.parentRotation = parentRotation;
	}
	public double getParentRotation(){
		return this.parentRotation;
	}

	public void setParent(DisplayObject parent){
		this.parent = parent;
	}

	public Point localToGlobal(Point point){
		if (this.parent != null){
			Point scaledPoint = new Point((int)(point.x * 1/getScaleX()), (int)(point.y * 1/getScaleY()));
			return addPoint(scaledPoint, this.parent.localToGlobal(this.parent.getPosition()));
		}
		return this.getPosition();
	}

	public Point globalToLocal(Point point){
		if (this.parent != null){
			return subPoint(point, this.parent.localToGlobal(this.parent.getPosition()));
		}
		return this.getPosition();
	}

	public Point addPoint(Point point1, Point point2){
		int newX = point1.x + point2.x;
		int newY = point1.y + point2.y;
		Point newPoint = new Point(newX, newY);
		return newPoint;
	}

	public Point subPoint(Point point1, Point point2){
		int newX = point1.x - point2.x;
		int newY = point1.y - point2.y;
		Point newPoint = new Point(newX, newY);
		return newPoint;
	}


	public void setPosition(Point position) {
		this.position = position;
	}

	public void setPivotPoint(Point pivotPoint) {
		this.pivotPoint = pivotPoint;
	}

	public void setRotation(int rotation){
		this.rotation = rotation;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setVisible(Boolean visible){
		this.visible = visible;
	}
	public void setCurAlpha(float curAlpha){
		this.setOldAlpha(this.curAlpha);
		this.curAlpha = curAlpha;
	}

	private void setOldAlpha(float oldAlpha){
		this.oldAlpha = oldAlpha;
	}
	public void setScaleX(double scaleX){
		this.scaleX = scaleX;
	}
	public void setScaleY(double scaleY){
		this.scaleY = scaleY;
	}

	//getters

	public Point getPosition() {
		return this.position;
	}

	public Point getPivotPoint() {
		return this.pivotPoint;
	}

	public int getRotation(){
		return this.rotation;
	}

	public String getId() {
		return id;
	}

	public Boolean getVisible() {
		return this.visible;
	}

	public float getCurAlpha() {
		return this.curAlpha;
	}

	public float getOldAlpha(){
		return this.oldAlpha;
	}

	public double getScaleX(){
		return this.scaleX;
	}

	public double getScaleY() {
		return this.scaleY;
	}

	/**
	 * Returns the unscaled width and height of this display object
	 * */
	public int getUnscaledWidth() {
		if(displayImage == null) return 0;
		return displayImage.getWidth();
	}

	public int getUnscaledHeight() {
		if(displayImage == null) return 0;
		return displayImage.getHeight();
	}

	public BufferedImage getDisplayImage() {
		return this.displayImage;
	}

	public void setImage(String imageName) {
		if (imageName == null) {
			return;
		}
		displayImage = readImage(imageName);
		if (displayImage == null) {
			System.err.println("[DisplayObject.setImage] ERROR: " + imageName + " does not exist!");
		}
	}


	/**
	 * Helper function that simply reads an image from the given image name
	 * (looks in resources\\) and returns the bufferedimage for that filename
	 * */
	public BufferedImage readImage(String imageName) {
		BufferedImage image = null;
		try {
			String file = ("resources" + File.separator + imageName);
			image = ImageIO.read(new File(file));
		} catch (IOException e) {
			System.out.println("[Error in DisplayObject.java:readImage] Could not read image " + imageName);
			e.printStackTrace();
		}
		return image;
	}

	public void setImage(BufferedImage image) {
		if(image == null) return;
		displayImage = image;
	}


	/**
	 * Invoked on every frame before drawing. Used to update this display
	 * objects state before the draw occurs. Should be overridden if necessary
	 * to update objects appropriately.
	 * */
	protected void update(ArrayList<Integer> pressedKeys) {
		
	}

	/**
	 * Draws this image. This should be overloaded if a display object should
	 * draw to the screen differently. This method is automatically invoked on
	 * every frame.
	 * */
	public void draw(Graphics g) {

			/*
			 * Get the graphics and apply this objects transformations
			 * (rotation, etc.)
			 */
			Graphics2D g2d = (Graphics2D) g;

			if (this.writeText) {
				g.drawString(this.text, 0, 20);
			}



			if (this.isSinWave){
				//g2d.drawPolygon(this.SW.ptsX, this.SW.pts, this.SW.points);
				//g2d.drawPolyline(this.SW.ptsX, this.SW.pts, this.SW.points);
				if(this.SW.ready) {
					this.wavePos += this.waveSpeed;
					SW.CreateLine((int) this.wavePos);
					for (int i = 0; i < (this.SW.points - 1); i++) {
                        g2d.setStroke(new BasicStroke((int)(5 * this.scaleX)));
						int line[] =  this.SW.sinWaveLines[i];
						g2d.drawLine(line[0], line[1], line[2], line[3]);
					}
				}
			}
            else if(isHead){
				applyTransformations(g2d);
				g2d.setPaint(headColor);
				g2d.fillOval(0,0,50,50);
				g2d.setPaint(Color.black);
				reverseTransformations(g2d);
			}
			else if ((displayImage != null) && (this.isImage)) {

				applyTransformations(g2d);
				this.af = g2d.getTransform();

				af = g2d.getTransform();
				/* Actually draw the image, perform the pivot point translation here */
				g2d.drawImage(displayImage, 0, 0,
						(int) (getUnscaledWidth()),
						(int) (getUnscaledHeight()), null);


				/*
				 * undo the transformations so this doesn't affect other display
				 * objects
				 */
				reverseTransformations(g2d);
			}
			else if (this.isTriangle){
				applyTransformations(g2d);
				AffineTransform at = g2d.getTransform();
				Point2D[] triPoints= new Point2D[3];
				Point[] triangleClickable = triangle.getTrianglePoints();
				Point2D[] transformedTriPoints = new Point2D[triPoints.length];
				//https://stackoverflow.com/questions/5204322/how-to-get-absolute-coordinates-after-transformation
				for(int i = 0; i < triPoints.length; i++){
					Point2D triPoint2d = new Point2D.Double(triangleClickable[i].x , triangleClickable[i].y);
					Point2D screenPoint = at.transform(triPoint2d, new Point2D.Double());
					transformedTriPoints[i] = screenPoint;
				}
				triangle.setTriangleClickable(transformedTriPoints);
				//g2d.draw(triangle.createClickableTriangle());
				if (id == "moon"){
					g2d.setPaint(Color.GRAY);
					g2d.fillPolygon(triangle.getTriangle());
					g2d.setPaint(Color.black);
				}
				if (id.contains("button")){
					g2d.setPaint(Color.RED);
					g2d.fillPolygon(triangle.getTriangle());
					g2d.setPaint(Color.black);
				}
				g2d.draw(triangle.getTriangle());
				reverseTransformations(g2d);
			}

			else if (this.isCircleTriangle) {
				applyTransformations(g2d);
				AffineTransform at = g2d.getTransform();
				circleTriangle.setAffineTransform(at);
				Point[] triangleClickable = circleTriangle.getTrianglePoints();
				//gradient
				Gradient gradient = circleTriangle.getAngleGradient();
				boolean[] draggable = circleTriangle.getDraggable();
				for(int i = 0; i < triangleClickable.length; i++) {
					int[] angles = circleTriangle.getAngles();
					Color col = gradient.getColorFromGradient(angles[i]);
					if (draggable[i]) {
						drawCenteredCircle(g2d, triangleClickable[i].x, triangleClickable[i].y, this.triRadius, col);
					}
					else{
						drawCenteredRectangle(g2d, triangleClickable[i].x, triangleClickable[i].y, this.triRadius, col);
					}
				}
				//updateTheTriangleClickable array
				circleTriangle.transformTriangleClickable();
				g2d.draw(circleTriangle.getTriangle());
				reverseTransformations(g2d);
			}
	}

	public void drawCenteredCircle(Graphics2D g, int x, int y, int r, Color fill) {
		x = x-(r/2);
		y = y-(r/2);
		g.setPaint(fill);
		g.fillOval(x,y,r,r);
		g.setPaint(Color.black);
	}
	public void drawCenteredRectangle(Graphics2D g, int x, int y, int r, Color fill) {
		x = x-(r/2);
		y = y-(r/2);
		g.setPaint(fill);
		g.fillRect(x,y,r,r);
		g.setPaint(Color.black);
	}

	/**
	 * Applies transformations for this display object to the given graphics
	 * object
	 * */
	protected void applyTransformations(Graphics2D g2d) {
		if (this.position != null) {
			g2d.translate(this.position.x, this.position.y);

			g2d.rotate(Math.toRadians(this.getRotation()), this.getPivotPoint().x, this.getPivotPoint().y);
			g2d.scale(this.scaleX, this.scaleY);
			float curAlpha;
			this.oldAlpha = curAlpha = ((AlphaComposite) g2d.getComposite()).getAlpha();
			g2d.setComposite(AlphaComposite.getInstance(3, curAlpha *
					this.curAlpha));
		}

	}


	/**
	 * Reverses transformations for this display object to the given graphics
	 * object
	 * */
	protected void reverseTransformations(Graphics2D g2d) {
		if(this.position != null) {
			g2d.setComposite(AlphaComposite.getInstance(3, this.oldAlpha));
			g2d.scale(1 / this.scaleX, 1 / this.scaleY);
			g2d.rotate(-Math.toRadians(this.getRotation()), this.getPivotPoint().x, this.getPivotPoint().y);
			g2d.translate(-this.position.x, -this.position.y);
		}
	}

}
