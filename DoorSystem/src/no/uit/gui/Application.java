package no.uit.gui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.smartcardio.CardException;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import net.miginfocom.swing.MigLayout;
import no.uit.smartcard.Communication;


public class Application  implements ActionListener {

	private static Application window;
	
	private JFrame frame;
	
	private Communication communication;

	BufferedImage locked, unlocked;
	BufferedImage before;
	int w;
	int h;
	BufferedImage after;
	AffineTransform at;
	AffineTransformOp scaleOp;
	JLabel wIcon1;
	JLabel wIcon2;

	
	
	public void launch(){
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					window.frame.setVisible(true);
			
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		// set the name of the application menu item
		window = new Application();
		window.launch();
		window.run();
	}


	
	/**
	 * Create the application.
	 */
	public Application() {
		initialize();

		
		
		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		frame = new JFrame("House Door");
		frame.setBounds(100, 100, 370 , 562);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new MigLayout("wrap", "[131px][24px][131px][21px][131px]", "[36px][64px][98px][]"));
		

		locked = null;
		unlocked = null;
		try {
			locked = ImageIO.read(new File("locked.jpg"));
			unlocked = ImageIO.read(new File("unlocked.jpg"));
			

			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			communication = new Communication();
		} catch (CardException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		before = locked;
		 w = before.getWidth();
		 h = before.getHeight();
		 after = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		 at = new AffineTransform();
		at.scale(0.5, 0.5);
		 scaleOp = 
		   new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
		after = scaleOp.filter(before, after);
		
		wIcon1 = new JLabel(new ImageIcon(after));
		frame.getContentPane().add(wIcon1, "pos 40 10");
		
		wIcon1.setVisible(true);

		
		before = unlocked;
		w = before.getWidth();
		h = before.getHeight();
		after = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		at = new AffineTransform();
		at.scale(0.35, 0.35);
		 scaleOp = 
		   new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
		after = scaleOp.filter(before, after);
		
		wIcon2 = new JLabel(new ImageIcon(after));
		
		
		frame.getContentPane().add(wIcon2, "pos 40 10");
		
		wIcon2.setVisible(false);
		
	
	}


	@Override
	public void actionPerformed(ActionEvent arg0) {
		
	}

	public void run(){
		while(true){
			try {
				boolean door = communication.connect();
				//byte[] data = communication.readData();
				//communication.closeConnectionToCard();
				
				if (door){
					
					wIcon1.setVisible(false);
					wIcon2.setVisible(true);
					Thread.sleep(4000);
					System.out.println("Locking the door");
					wIcon1.setVisible(true);
					wIcon2.setVisible(false);
			
				}
				Thread.sleep(4000);
			} catch (Exception e) {
				try {
					communication.closeConnectionToCard();
				} catch (CardException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				e.printStackTrace();
			}
	
		}
	}

	
}
