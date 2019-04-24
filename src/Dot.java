import java.awt.*;

public class Dot{

	protected Point center;
	protected int radius;
	protected Color color;

	public Dot(Point center) {
		this.center = center;
		radius = 5;
		color = Color.BLUE;
	}


	void paint(Graphics g){
		g.setColor(color);
		g.fillOval(center.x-radius,center.y-radius, radius*2, radius*2 );
	}

	public boolean isInside(Point p){
		return p.distance(center)<radius;
	}

	public void setColor(Color c){
		this.color = c;
	}


}
