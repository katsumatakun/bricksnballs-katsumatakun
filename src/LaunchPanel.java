import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

	public class LaunchPanel extends JPanel implements VanishListener {

		ArrayList<MovingDot> dots;
		ArrayList<Obstacle> os;
		ArrayList<MovingDot> deadDots;
		ArrayList<Obstacle> deadObstacles;
		Dot launchPoint;
		Point s;

		public LaunchPanel(){
			setPreferredSize(new Dimension(500,500));
			dots = new ArrayList<MovingDot>();
			deadDots = new ArrayList<>();
			deadObstacles = new ArrayList<>();
			os = new ArrayList<>();
			generateObs(5);
			//o = new Obstacle(new Point(150,150));
			s = new Point(250,450);
			launchPoint =new Dot(s);
			launchPoint.setColor(Color.GREEN);
			addMouseListener(new MousePlay());
		}

		private void generateObs(int num){
			boolean intersection = false;
			for(int i=0; i<num; i++){
				int x = (int)(Math.random()*300)+100;
				int y = (int)(Math.random()*300)+50;
				Obstacle newObs = new Obstacle(new Point(x,y));
				for(Obstacle obstacle: os){
					if(obstacle.getRegion().intersects(newObs.getRegion())) {
						System.out.println(obstacle.getRegion());
						System.out.println(newObs.getRegion());
						intersection = true;
					}
				}
				if(intersection) {
					i--;
				}
				else {
					os.add(newObs);
				}
				intersection = false;
			}
		}

		@Override
		protected void paintComponent(Graphics g){
			super.paintComponent(g);

			for (Obstacle o : os){
				o.move();
				if(o.bottom() > getHeight()){
					VanishEvent ev = new VanishEvent(o);
					update(ev);
				}
				o.paint(g);
			}

			launchPoint.paint(g);
			for (MovingDot d: dots){
				if (d.top() < 0){
					d.reflectY();
				}
				if(d.bottom() > getHeight()){
					VanishEvent ev = new VanishEvent(d);
					update(ev);
				}
				if ((d.left() < 0) || d.right() > getWidth()){
					d.reflectX();
				}
				for (Obstacle o : os){
					if (d.getRegion().intersects(o.getRegion())){
						o.hitBy(d);
						if(o.getHitPoint() == 0){
							VanishEvent ev = new VanishEvent(o);
							update(ev);
						}
					}
				}
				d.move();
				d.paint(g);
			}


			try{
				Thread.sleep(30);
			}catch (InterruptedException e){
				e.printStackTrace();
			}

			for(MovingDot dd: deadDots){
				dots.remove(dd);
			}
			deadDots.clear();

			for(int x=0; x <deadObstacles.size(); x++){
				os.remove(deadObstacles.get(x));
				generateObs(1);
			}
			deadObstacles.clear();
			repaint();
		}

		private void generateDot(Point p){
			MovingDot d = new MovingDot(s, p, 10);
			dots.add(d);
		}

		private class MousePlay implements MouseListener{
			@Override
			public void mouseClicked(MouseEvent e){
				if(dots.size()<3)
					generateDot(e.getPoint());
			}

			@Override
			public void mousePressed(MouseEvent e){
			}

			@Override
			public void mouseReleased(MouseEvent e){
			}

			@Override
			public void mouseEntered(MouseEvent e){
			}

			@Override
			public void mouseExited(MouseEvent e){
			}
		}


	@Override
	public void update(VanishEvent e){
		if(e.getSource() instanceof MovingDot){
			MovingDot d = (MovingDot)e.getSource();
			deadDots.add(d);
		}

		else if (e.getSource() instanceof Obstacle){
			Obstacle o = (Obstacle)e.getSource();
			deadObstacles.add(o);
		}
	}
}
