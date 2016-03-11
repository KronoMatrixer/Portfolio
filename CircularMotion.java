import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * @author Jesse Kinsey (KronoMatrixer)
 * This program will display circular motion and a diagonal gradient background.
 */
public class CircularMotion extends JFrame implements ChangeListener, ActionListener {
	/**
	 * This class contains the main method and define the window.
	 * The main method contains a infinite loop to control time. 
	 * 		All of the listeners and graphic code still function, because the time loop is located at the end of the main method.
	 */
	private static final long serialVersionUID = 3161470840112804177L;
	private static final int ABORT = 0;
	MotionPanel motionPanel = new MotionPanel();
	JPanel gui = new JPanel();
	static JButton close = new JButton("Exit");
	private static CircularMotion app;
	private static int 	num1 = 0, 	//start d (rings)
						num2 = 0, 	//end d
						num3 = 0;	//start d (pie piece)
	JScrollPane scrollPane1 = new JScrollPane();
	
	public static void main(String[] args) throws IOException, InterruptedException, FontFormatException {
		//initiate frame
		int winMod = 3;		//ratio between window and screen
		app = new CircularMotion();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double 		wMod = screenSize.width / winMod, 	//window width
					hMod = screenSize.height / winMod;	//window height
		app.setSize(300, 350);
		app.setLocation(		//sets window location to center of the screen
							(int) (	0 + wMod 
									* (.5 * (winMod - 1))), 
							(int) (0 + hMod 
									* (.5 * (winMod - 1))));
		app.setVisible(true);
		app.setDefaultCloseOperation(EXIT_ON_CLOSE);
		//infinite time loop
		int 	RUN = 1,	//constant conditional that prevents loop from ending
				dir = 1;	//begin with increasing rings' angles
		do {
			if (dir ==2) {		//decrease ring angle
				num2 				-= 	10;
				num1 				+= 	15;
				if(num2<=0) {	//if rings' angles are at 0, increase angle
					dir = 1;}}
			else if (dir == 1) {	//increase ring angle
				num2 				+= 	5;
				num1 				+= 	5;
				if(num2>360) {	//if rings' angles are at 360, decrease angle
					dir = 2;}}
			num3 				-= 	2;
			Thread.sleep(15);
			app.repaint();
			if(num1 == 360) num1 = -360;	//prevents num1 from exceeding int limit
			if(num3 == -360) num3 = 360;	//prevents num3 from exceeding int limit
		} while (RUN == 1);		//never end loop
	}
	
	//panel format
	CircularMotion() {
		setLayout(new BorderLayout());
		motionPanel.setLayout(new BorderLayout());
		motionPanel.add(scrollPane1, BorderLayout.WEST);
		add(motionPanel, BorderLayout.CENTER);
		gui.add(close);
		add(gui, BorderLayout.SOUTH);
		close.addActionListener(this);}
	
	//quality control
	public void stateChanged(ChangeEvent arg0) {
		motionPanel.repaint();}
	
	//graphics
	class MotionPanel extends JPanel {
		/**
		 * This class contains the following components that interact with the time loop in the main method:
		 * 		diagonal gradient background.
		 * 		rings more than 2 pixels thick where one is inside the other with 0 pixels between each other.
		 * 		pie piece.
		 * All graphics in this class will grow and shrink with the window's shortest side as the window is resized.
		 */
		private static final long serialVersionUID = 1L;
		int w = 0;
		int h = 0;
		
		MotionPanel() {}
		
		public void paintComponent(Graphics a) {
			//size graphics
			Dimension screenSize = motionPanel.getSize();
			int diam, lng;
			if(screenSize.height<screenSize.width) {	//if height is shorter than width, size the image based on height
				diam = screenSize.height;
				lng = screenSize.width;}
			else {										//if height is not shorter than width, size the image based on width
				diam = screenSize.width;
				lng = screenSize.height;}
			//background
			int w1 = getWidth();
			int h1 = getHeight();
			a.clearRect(0, 0, w1, h1);
			a.setColor(Color.black);
			a.fillRect(0, 0, screenSize.width, screenSize.height);
			//diagonal gradient
			int gColor[][] = {	{127, 	0, 	0}, 	//inner color of gradient
								{0, 	0, 	0}};	//outer color of gradient
			for (int lCount = 0; lCount < lng; lCount++) {	//line value (based on distance from the origin)
				//update color
				double gCurrColor[] = {
						gColor[0][0] 										//starting value
								+ lCount 									//current line value
								* (gColor[1][0] - gColor[0][0]) / lng, 		//rate of change (difference of starting value and ending value over the distance)
						gColor[0][1] 										//starting value
								+ lCount 									//current line value
								* (gColor[1][1] - gColor[0][1]) / lng, 		//rate of change (difference of starting value and ending value over the distance)
						gColor[0][2] 										//starting value
								+ lCount 									//current line value
								* (gColor[1][2] - gColor[0][2]) / lng};		//rate of change (difference of starting value and ending value over the distance)
				Color currentColor = new Color((int) gCurrColor[0], (int) gCurrColor[1], (int) gCurrColor[2]);
				a.setColor(currentColor);
				//draw gradient by line
				a.drawLine(0, screenSize.height - lCount / 2, screenSize.width - lCount, 0); 	//draw upper left line
				a.drawLine(0, screenSize.height + lCount / 2, screenSize.width + lCount, 0);}	//draw lower right line
			//outer circle
			a.setColor(Color.orange.darker());
			for (int count = 0; count <= 3; count++) {	//line value (based on change in diameter)
				a.drawArc(		(screenSize.width - diam) / 2 	//x of top left (total value outside of the circle width / 2 (to get one side))
									+ count, 					//current line value (rate = 1)
								(screenSize.height - diam) / 2 	//y of top left (total value outside of the circle height / 2 (to get one side))
									+ count,					//current line value (rate = 1)
								diam 							//starting diameter
									- 2 * count, 				//rate of change in diameter * current line value
								diam 							//starting diameter
									- 2 * count, 				//rate of change in diameter * current line value
								num1 							//value based on time loop
									* (-1) 						//flip direction
									+ 180, 						//flip size
								num2 							//value based on time loop
									* (-1) 						//flip direction
									+ 180);}					//flip size
			//inner circle
			a.setColor(Color.red.brighter().brighter());
			for (int count = 0; count <= 3; count++) {	//line value (based on change in diameter)
				a.drawArc(		(screenSize.width - diam) / 2 	//x of top left (total value outside of the circle width / 2 (to get one side))
									+ 5 						//compensates top left point for a diameter of 10 less with same point for the center
									+ count, 					//current line value
								(screenSize.height - diam) / 2 	//y of top left (total value outside of the circle height / 2 (to get one side))
									+ 5 						//compensates top left point for a diameter of 10 less with same point for the center
									+ count, 					//current line value
								diam 							//starting diameter
									- 10 						//diameter of 10 less
									- 2 * count, 				//rate of change in diameter * current line value
								diam 							//starting diameter
									- 10 						//diameter of 10 less
									- 2 * count, 				//rate of change in diameter * current line value
								num1, num2);}					//values based on time loop
			//pie piece
			Color myColor = new Color(255, 127, 0, 128 );
			for (int diamCD = diam - 20; diamCD >= 0; diamCD -= 2) {	//line value (based on diameter)
				for (int diamLoc = 0; diamLoc <= 35; diamLoc++) {		//point value (based on angle from origin)
					myColor = new Color(255, 127, 0, 128 - (128 / 35) * diamLoc );
					a.setColor(myColor);
					a.drawArc(	(screenSize.width - diam) / 2 	//starting x of top left (total value outside of the circle width / 2 (to get one side))
									+ (diam - diamCD) / 2, 		//plus additional area based on line value / 2 (to get one side)
								(screenSize.height - diam) / 2 	//y of top left (total value outside of the circle height / 2 (to get one side))
									+ (diam - diamCD) / 2, 		//plus additional area based on line value / 2 (to get one side)
								diam 							//starting diameter
									- (diam - diamCD), 			//change in diameter
								diam 							//starting diameter
									- (diam - diamCD), 			//change in diameter
								num3 + diamLoc,					//starting point plus the current point value
								2);}}}							//constant angle
		}
	
	public void actionPerformed(ActionEvent a) {
		if(a.getSource() == close) {
			System.exit(ABORT);}}
	
	//quality control
	public void componentResized(ComponentEvent e) {
		motionPanel.repaint();}
	
	public void componentHidden(ComponentEvent e) {}
	public void componentMoved(ComponentEvent e) {}
	public void componentShown(ComponentEvent e) {}
}