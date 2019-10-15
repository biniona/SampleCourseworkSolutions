package edu.virginia.engine.display;

import javafx.scene.shape.Circle;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

import javax.swing.JPanel;

/**
 * This class is essentially something I needed to do to get around Java's swing stuff. It is the JPanel we are drawing
 * all of our graphics on. You can ignore this class.
 * */
@SuppressWarnings("serial")
public class GameScenePanel extends JPanel {

	/* The game associated with this panel */
	private Game gameRef;

	/**
	 * Constructor
	 * */
	public GameScenePanel(Game gameRef) {
		super();
		this.setLayout(null);
		this.setGameRef(gameRef);
		this.setBounds(0,0,gameRef.getUnscaledWidth(), gameRef.getUnscaledHeight());
	}

	public Game getGameRef() {
		return gameRef;
	}

	public void setGameRef(Game sceneRef) {
		this.gameRef = sceneRef;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		gameRef.nextFrame(g);
	}

	@Override
	public String toString() {
		return gameRef.getId() + " (width = " + this.getWidth()
				+ ", height = " + this.getHeight();
	}

	public TrianglePanel newTriangle(DisplayObject triangleObject, Point[] points){
		TrianglePanel newPanel = new TrianglePanel(triangleObject, points);
		return newPanel;
	}

	public CircularTrianglePanel newCirlceTriangle(DisplayObject triangleObject, Point[] points, int radius){
		CircularTrianglePanel newPanel = new CircularTrianglePanel(triangleObject, points, radius);
		return newPanel;
	}

	//https://stackoverflow.com/questions/27678603/how-to-check-mouse-click-in-2d-graphics-java
	public class TrianglePanel implements MouseListener, MouseMotionListener {
		private Polygon triangle;
		private Point[] trianglePoints;
		private Point[] triangleClickable;
		private DisplayObject triangleObject;
		private Point start;
		private Point end;
		private boolean holding = false;
		private int triangleWidth;
		private int triangleHeight;
		private boolean isButton = false;
		private AffineTransform affineTransform;

		public TrianglePanel(DisplayObject triangleObject, Point[] triPoints){
			//Create triangle
			affineTransform = new AffineTransform();
			triangle = new Polygon();
			triangle.addPoint(triPoints[0].x, triPoints[0].y);
			triangle.addPoint(triPoints[1].x, triPoints[1].y);
			triangle.addPoint(triPoints[2].x, triPoints[2].y);
			triangleWidth =  100;
			triangleHeight = 80;
			trianglePoints = new Point[3];
			triangleClickable = new Point[3];
			trianglePoints = triPoints;
			this.triangleObject = triangleObject;
			//Add mouse Listener
			addMouseListener(this);
			addMouseMotionListener(this);
			//Set size to make sure that the whole triangle is shown
			setPreferredSize(new Dimension(300, 300));
		}

		public Point[] getTrianglePoints(){
			return this.trianglePoints;
		}

		public void setTriangleClickable(Point2D[] triangleClickable){
			for(int i = 0; i< triangleClickable.length; i++){
				this.triangleClickable[i] = new Point((int)triangleClickable[i].getX(), (int)triangleClickable[i].getY());
			}
		}

		public void setIsButton(){this.isButton = true;}

		public Polygon getTriangle(){
			return triangle;
		}

		/** Draws the triangle as this frame's painting */
		public void paintComponent(Graphics g){
			Graphics2D g2d = (Graphics2D)g;

			g2d.draw(triangle);
		}


		public Polygon createClickableTriangle(){
			Polygon triangleTransorm = new Polygon();
			for(int i = 0; i< this.triangleClickable.length; i++){
				triangleTransorm.addPoint(this.triangleClickable[i].x, this.triangleClickable[i].y);
			}
			return triangleTransorm;
		}
		//Required methods for MouseListener, though the only one you care about is click
		public void mousePressed(MouseEvent e) {
			start = e.getPoint();
			Point local = this.triangleObject.globalToLocal(start);
			//createTheTransformed triangle
			Polygon triangleTransorm = createClickableTriangle();
			//if (triangle.contains(local)){ holding = true; }
			if (triangleTransorm.contains(e.getPoint())){ holding = true; }
			if (holding) System.out.println("HOLDING");
		}
		public void mouseReleased(MouseEvent e) {
			end = e.getPoint();
			holding = false;
		}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}

		/** Called whenever the mouse clicks.
		 * Could be replaced with setting the value of a JLabel, etc. */
		public void mouseClicked(MouseEvent e) {
			if(this.isButton) {
				Polygon clickable =  createClickableTriangle();
				Point p = e.getPoint();
				Point2D triPoint2d = new Point2D.Double(p.x, p.y);
				try {
					Point2D screenPoint2d = affineTransform.inverseTransform(triPoint2d, new Point2D.Double());
					Point screenPoint = new Point((int) screenPoint2d.getX(), (int) screenPoint2d.getY());
					if (clickable.contains(screenPoint)) {
						triangleObject.setTriangleClicked(true);
					}
				} catch (NoninvertibleTransformException e1) {
					e1.printStackTrace();
				}
			}
		}


		public void mouseDragged(MouseEvent e){
			if(holding && (isButton == false)){
				Point p = e.getPoint();
				Point diff = pointSub(p, start);
				double adj = (-diff.y*.001) + (diff.x*.001) ;
				if (!((triangleObject.getScaleY()+adj) > 3) && !((triangleObject.getScaleY()+adj) < 1)) {
					triangleObject.setScaleX(triangleObject.getScaleY() + adj);
					triangleObject.setScaleY(triangleObject.getScaleY() + adj);
					//this is all so that the sail maintains its position relative to the boat
					//when it is rescaled
					if ((triangleObject.getId() == "sail")) {
						triangleObject.setPosition(new Point((int) -(triangleWidth / 2 * (triangleObject.getScaleX() - 1)), (int) -(triangleHeight * (triangleObject.getScaleY() - 1))));
					}
					else if ((triangleObject.getId() == "moon")) {
						triangleObject.setPosition(new Point((int) -(triangleWidth / 2 * (triangleObject.getScaleX() - 1)), (int) -(triangleHeight/2 * (triangleObject.getScaleY() - 1))));
					}
				}
			}
		}

		public void mouseMoved(MouseEvent e){};

		public Point pointSub(Point p1, Point p2){
			return new Point(p1.x - p2.x, p1.y - p2.y);
		}


	}

	public class CircularTrianglePanel implements MouseListener, MouseMotionListener {
		private boolean isDragonClickable = false;
		private boolean isDragonStatic = false;
		private boolean isCongruentDragonClickable = false;
		private boolean isIsoscelesEnemy = false;
		private boolean isIsosceles = false;
		private Polygon triangle;
		private Ellipse2D.Double circle1;
		private Ellipse2D.Double circle2;
		private Ellipse2D.Double circle3;
		private Point[] trianglePoints;
		private Point[] circleClickable;
		private DisplayObject triangleObject;
		private Point start;
		private Point end;
		private boolean[] draggable;
		private boolean[] holdingArr;
		private int triangleWidth;
		private int triangleHeight;
		private int radius;
		private AffineTransform affineTransform;
		private int[] angles;
		private Gradient angleGradient;
		private Gradient lineGradient;


		public CircularTrianglePanel(DisplayObject triangleObject, Point[] triPoints, int rad){
			//Create triangle
			angles = new int[3];
			draggable = new boolean[3];
			affineTransform = new AffineTransform();
			holdingArr = new boolean[3];
			draggable = new boolean[3];
			for(int i=0;i<draggable.length;i++) draggable[i] = true;
			radius = rad;
			triangle = new Polygon();
			circle1 = new Ellipse2D.Double();
			circle2 = new Ellipse2D.Double();
			circle3 = new Ellipse2D.Double();
			triangle.addPoint(triPoints[0].x, triPoints[0].y);
			triangle.addPoint(triPoints[1].x, triPoints[1].y);
			triangle.addPoint(triPoints[2].x, triPoints[2].y);
			double circleAdj = (double)radius/2d;
			circle1.setFrame(triPoints[0].x - circleAdj, triPoints[0].y - circleAdj, (double) radius,(double) radius);
			circle2.setFrame(triPoints[1].x - circleAdj, triPoints[1].y - circleAdj, (double) radius,(double) radius);
			circle3.setFrame(triPoints[2].x - circleAdj, triPoints[2].y - circleAdj, (double) radius,(double) radius);
			triangleWidth =  100;
			triangleHeight = 80;
			trianglePoints = new Point[3];
			circleClickable = new Point[3];
			trianglePoints[0] = triPoints[0];
			trianglePoints[1] = triPoints[1];
			trianglePoints[2] = triPoints[2];
			calculateAngles();
			this.triangleObject = triangleObject;

			//angle gradient
			this.angleGradient = new Gradient(Color.green, Color.blue, 0, 180);
			//TODO line gradient

			//Add mouse Listener
			addMouseListener(this);
			addMouseMotionListener(this);
			//Set size to make sure that the whole triangle is shown
			setPreferredSize(new Dimension(300, 300));
		}

		public void setIsoscelesEnemy(){ this.isIsoscelesEnemy = true; }
		public void setIsDragonClickable(){ this.isDragonClickable = true; }
		public void setIsDragonStatic(){ this.isDragonStatic = true; }
		public boolean getIsoscelesEnemy(){ return this.isIsoscelesEnemy; }
		public boolean getIsosceles(){ return this.isIsosceles; }
		public boolean getIsCongruentDragonClickable(){return this.isCongruentDragonClickable;}

		public void calculateAngles(){
			angles[0] = (angleBetween(trianglePoints[0], trianglePoints[1],trianglePoints[2]));
			angles[1] = (angleBetween(trianglePoints[1], trianglePoints[2],trianglePoints[0]));
			angles[2] = (angleBetween(trianglePoints[2], trianglePoints[0],trianglePoints[1]));
		}

		public void setAngleProperties(){
			boolean held = false;
			for(int i = 0; i < holdingArr.length; i++){
				if (holdingArr[i]){
					held = true;
				}
			}

			if(isIsoscelesEnemy && !held){

				for(int i = 0; i < angles.length; i++){
					for(int j = 0; j < angles.length; j++){
						if (i == j){
							continue;
						}
						//System.out.print("ANGLES I " + i + " : " + angles[i]);
						//System.out.println(" ANGLES J " + j + " : " + angles[j]);

						if (Math.abs((int)(angles[i] - angles[j])) < 7){
							isIsosceles = true;
							return;
						}

					}
				}
			}


		}

		public void setAngleProperties(int[] otherAngles){
			boolean held = false;
			for(int i = 0; i < holdingArr.length; i++){
				if (holdingArr[i]){
					held = true;
				}
			}
			if(this.isDragonClickable && !held){
				for(int i = 0; i < angles.length; i++){
					boolean congruent = true;
					for(int j = 0; j < angles.length; j++){

						if (Math.abs((int)(angles[i] - otherAngles[j])) > 14){
							congruent = false;
						}
					}
					this.isCongruentDragonClickable = congruent;
				}
			}
		}

		public boolean[] getDraggable(){
			return draggable;
		}

		public Gradient getAngleGradient(){
			return angleGradient;
		}

		public int[] getAngles(){
			return angles;
		}

		public void setDraggable(boolean[] values){
			for(int i = 0; i < draggable.length; i++){
				draggable[i] = values[i];
			}
		}

		public void setAffineTransform(AffineTransform affineTransform) {
			this.affineTransform = affineTransform;
		}

		public Point[] getTrianglePoints(){
			return this.trianglePoints;
		}

		public void transformTriangleClickable(){
			Point2D[] transformedTriPoints = new Point2D[3];
			for(int i = 0; i < transformedTriPoints.length; i++){
				Point2D triPoint2d = new Point2D.Double(trianglePoints[i].x , trianglePoints[i].y);
				Point2D screenPoint = affineTransform.transform(triPoint2d, new Point2D.Double());
				transformedTriPoints[i] = screenPoint;
			}
			setCircleClickable(transformedTriPoints);
		}


		public void setCircleClickable(Point2D[] triangleClickable){
			double circleAdj = (double)radius/2d;
			for(int i = 0; i < triangleClickable.length; i++){
				this.circleClickable[i] = new Point((int)(triangleClickable[i].getX()-circleAdj), (int)(triangleClickable[i].getY()-circleAdj));
			}
		}

		public Polygon createClickableTriangle(){
			Polygon clickable = new Polygon();
			for(int i = 0; i< this.circleClickable.length; i++){
				clickable.addPoint(this.circleClickable[i].x, this.circleClickable[i].y);
			}
			return clickable;
		}

		public Polygon getTriangle(){
			return triangle;
		}

		public Ellipse2D.Double[] getAdjustedEllipses(){
			Ellipse2D.Double circle1Adjust,circle2Adjust,circle3Adjust;
			circle1Adjust = new Ellipse2D.Double();
			circle2Adjust = new Ellipse2D.Double();
			circle3Adjust = new Ellipse2D.Double();
			circle1Adjust.setFrame(circleClickable[0].x,circleClickable[0].y, triangleObject.getScaleX()*radius, triangleObject.getScaleY()*radius);
			circle2Adjust.setFrame(circleClickable[1].x,circleClickable[1].y, triangleObject.getScaleX()*radius, triangleObject.getScaleY()*radius);
			circle3Adjust.setFrame(circleClickable[2].x,circleClickable[2].y, triangleObject.getScaleX()*radius, triangleObject.getScaleY()*radius);
			Ellipse2D.Double[] arr = {circle1Adjust,circle2Adjust,circle3Adjust};
			return arr;
		}


		//Required methods for MouseListener, though the only one you care about is click
		public void mousePressed(MouseEvent e) {
			start = e.getPoint();
			Ellipse2D.Double[] adjEllipses = getAdjustedEllipses();
			for(int i = 0; i < adjEllipses.length; i++){

				if (adjEllipses[i].contains(start)) {
					holdingArr[i] = true;
					System.out.println("HOLDING " + i);
				}

			}
		}
		public void mouseReleased(MouseEvent e) {
			end = e.getPoint();
			for(int i = 0; i < holdingArr.length; i++){
				holdingArr[i] = false;
			}
		}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}

		/** Called whenever the mouse clicks.
		 * Could be replaced with setting the value of a JLabel, etc. */
		public void mouseClicked(MouseEvent e) {

		}


		public void mouseDragged(MouseEvent e){
			for(int i = 0; i < holdingArr.length; i++) {
				if (holdingArr[i] && draggable[i]) {
					Point p = e.getPoint();
					Point2D triPoint2d = new Point2D.Double(p.x , p.y);
					try
					{
						Point2D screenPoint2d = affineTransform.inverseTransform(triPoint2d, new Point2D.Double());
						Point screenPoint = new Point((int) screenPoint2d.getX(), (int) screenPoint2d.getY());
						trianglePoints[i] = screenPoint;
						calculateAngles();
					}
					catch (NoninvertibleTransformException e1) {
						e1.printStackTrace();
					}

					triangle = new Polygon();
					triangle.addPoint(trianglePoints[0].x, trianglePoints[0].y);
					triangle.addPoint(trianglePoints[1].x, trianglePoints[1].y);
					triangle.addPoint(trianglePoints[2].x, trianglePoints[2].y);

				}

			}
		}

		public void mouseMoved(MouseEvent e){};

		private int angleBetween(Point center, Point current, Point previous) {
			int angle =  (int) Math.toDegrees(Math.atan2(current.x - center.x,current.y - center.y)-
					Math.atan2(previous.x- center.x,previous.y- center.y));
			angle = Math.abs(angle);
			if(angle > 180) {angle = (180-(angle%180));}
			return angle;
		}


	}


}
