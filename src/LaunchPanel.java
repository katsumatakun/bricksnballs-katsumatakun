
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

public class LaunchPanel extends JPanel implements VanishListener{

		ArrayList<MovingDot> dots;
		ArrayList<Obstacle> os;
		ArrayList<MovingDot> deadDots;
		ArrayList<Obstacle> deadObstacles;
		Dot launchPoint;
		Point s;
		int enemyPoint;
		int myPoint;
		JLabel enemy;
		JLabel you;

		public LaunchPanel(){
			setPreferredSize(new Dimension(500,500));
			dots = new ArrayList<MovingDot>();
			deadDots = new ArrayList<>();
			deadObstacles = new ArrayList<>();
			os = new ArrayList<>();
			//o = new Obstacle(new Point(150,150));
			s = new Point(250,450);
			launchPoint =new Dot(s);
			launchPoint.setColor(Color.GREEN);
			addMouseListener(new MousePlay());
			generateObs(5);
			enemyPoint = 100;
			myPoint  = 100;
			you = new JLabel("You: " + myPoint);
			enemy = new JLabel("Enemy: " +  enemyPoint);
			add(you);
			add(enemy);
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
					Thread obth = new Thread(new obsThread(newObs));
					obth.start();
				}
				intersection = false;
			}
		}

		@Override
		protected void paintComponent(Graphics g){
			super.paintComponent(g);
			try{
				for (Obstacle o : os){
					o.paint(g);
				}
			}
			catch (ConcurrentModificationException e){}

			launchPoint.paint(g);
			try {
				for (MovingDot d : dots) {
					d.paint(g);
				}
			}
			catch (ConcurrentModificationException e){}

		}


		private MovingDot generateDot(Point p){
			MovingDot d = new MovingDot(s, p, 10);
			dots.add(d);
			return d;
		}

		private class ballThread implements Runnable {

			private MovingDot md;
			private boolean finish;

			public ballThread(MovingDot md) {
				this.md = md;
				finish = false;
			}

			@Override
			public void run() {
				while (!finish) {
					if (md.top() < 0) {
						md.reflectY();
					}
					if (md.bottom() > getHeight()) {
						dots.remove(md);
						finish = true;
					}
					if ((md.left() < 0) || md.right() > getWidth()) {
						md.reflectX();
					}
					try{
					for (Obstacle o : os) {
						if (md.getRegion().intersects(o.getRegion())) {
							o.hitBy(md);
							enemyPoint--;
							enemy.setText("enemy: " + enemyPoint);
							if(enemyPoint <= 0) {
								JOptionPane.showMessageDialog(null, "You won!!!");
								System.exit(0);
							}
							}
						}
					}
					catch (ConcurrentModificationException e){}
					md.move();
					repaint();
					try {
						Thread.sleep(15);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}

		private class obsThread implements Runnable {

			private Obstacle obs;
			private boolean finish;

			public obsThread(Obstacle obs) {
				this.obs = obs;
				finish = false;
			}

			@Override
			public void run() {
				while (!finish) {
					obs.move();
					if (getHeight() != 0 && (obs.bottom() > getHeight() || obs.getHitPoint() <= 0)) {
						VanishEvent ev = new VanishEvent(obs);
						update(ev);
						finish = true;
						if(obs.bottom() > getHeight()){
							myPoint -= obs.getHitPoint();
							you.setText("you: " + myPoint);
							if(myPoint <= 0) {
								JOptionPane.showMessageDialog(null, "You lost orx.");
								System.exit(0);
							}
						}
					}
					repaint();

					try {
						Thread.sleep(30);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
			private class MousePlay implements MouseListener {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (dots.size() < 3) {
						Thread th = new Thread(new ballThread(generateDot(e.getPoint())));
						th.start();
					}
				}

				@Override
				public void mousePressed(MouseEvent e) {
				}

				@Override
				public void mouseReleased(MouseEvent e) {
				}

				@Override
				public void mouseEntered(MouseEvent e) {
				}

				@Override
				public void mouseExited(MouseEvent e) {
				}
			}



			@Override
			public void update(VanishEvent e) {
			 if (e.getSource() instanceof Obstacle) {
					Obstacle o = (Obstacle) e.getSource();
					os.remove(o);
					generateObs(1);
				}
			}
		}
