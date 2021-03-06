import java.awt.*;
import java.util.ArrayList;

public class Obstacle{

	private double dy;
	private Point center;
	private Rectangle region;
	private int size;
	private Color color;
	private int hitPoint;
	private ArrayList<VanishListener> vanishListeners;

	public Obstacle(Point center, Color color) {
		this.center = center;
		size = (int) (Math.random() * 50) + 20;
		region = new Rectangle(center.x-size/2,center.y-size/2, size, size);
		this.color = color;
		hitPoint = (int) (Math.random() * 8) + 3;
		dy = 1;
		vanishListeners = new ArrayList<>();
	}

	public void move(){
		center.y += dy;
		region.y = center.y-size/2;
	}

	public void addVanishListeners(VanishListener vl){
		vanishListeners.add(vl);
	}

	public void paint(Graphics g){

        g.setColor(color);
		g.fillRect(region.x,region.y, region.width,region.height);
		g.drawString(Integer.toString(hitPoint),center.x-size/2, center.y-size/2);
	}

	public Rectangle getRegion() {
		return region;
	}

	public void hitBy(MovingDot d) {
		if ((d.top() > top()) && (d.bottom() < bottom())) {
			d.reflectX();
		} else {
			if ((d.left() > left()) && (d.right() < right())) {
				d.reflectY();
			}
			else{
				d.reflectX();
				d.reflectY();
			}
		}
		hitPoint--;
		if(hitPoint<=0){
			VanishEvent e = new VanishEvent(this);
			for(VanishListener vl: vanishListeners){
				vl.update(e);
			}
		}
	}

	public int getHitPoint(){return hitPoint;}
	public int top(){
		return region.y;
	}
	public int bottom(){
		return region.y+region.height;
	}
	public int left(){
		return region.x;
	}
	public int right(){
		return region.x +region.width;
	}
	public Color getColor(){
		return color;
	}
}
