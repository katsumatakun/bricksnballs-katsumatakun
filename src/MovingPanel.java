import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class MovingPanel extends JPanel {

	MovingDot d;

	public MovingPanel() {
		setPreferredSize(new Dimension(500,500));
		d = new MovingDot(new Point(250, 250));

		d.setMotion(1,2);

	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);


		d.move();
		if ((d.top() < 0) || d.bottom() > getHeight()){
			d.reflectY();
		}
		if ((d.left() < 0 ) || d.right() > getWidth()){
			d.reflectX();
		}

		d.paint(g);
		try {
			Thread.sleep(15);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}


		repaint();

	}
}
