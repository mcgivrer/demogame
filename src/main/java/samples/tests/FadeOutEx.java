package samples.tests;

import java.awt.AlphaComposite;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

class Surface extends JPanel implements ActionListener {

	private BufferedImage img;
	private Timer timer;
	private float alpha = 1f;

	private final int DELAY = 16;
	private final int INITIAL_DELAY = 200;

	public Surface() {

		loadImage();
		setSurfaceSize();
		initTimer();
	}

	private void loadImage() {
		try{
		img = ImageIO.read(this.getClass().getResourceAsStream("/res/images/background-1.jpg"));
		}catch(IOException ioe){
			System.err.println("Unable to read timage");
		}
	}

	private void setSurfaceSize() {

		int h = img.getHeight(this);
		int w = img.getWidth(this);
		setPreferredSize(new Dimension(w, h));
	}

	private void initTimer() {

		timer = new Timer(DELAY, this);
		timer.setInitialDelay(INITIAL_DELAY);
		timer.start();
	}

	private void doDrawing(Graphics g) {

		Graphics2D g2d = (Graphics2D) g.create();

		AlphaComposite acomp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha);
		g2d.setComposite(acomp);
		g2d.drawImage(img, 0, 0, null);

		g2d.dispose();
	}

	@Override
	public void paintComponent(Graphics g) {

		super.paintComponent(g);
		doDrawing(g);
	}

	private void step() {

		alpha += -0.05f;

		if (alpha <= 0) {

			alpha = 0;
			timer.stop();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		step();
		repaint();
	}
}

public class FadeOutEx extends JFrame {

	public FadeOutEx() {

		initUI();
	}

	private void initUI() {

		add(new Surface());

		pack();

		setTitle("Fade out");
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public static void main(String[] args) {

		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {

				FadeOutEx ex = new FadeOutEx();
				ex.setVisible(true);
			}
		});
	}
}
