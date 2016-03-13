import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class DraggableOverlay extends JFrame implements ActionListener, MouseListener, MouseMotionListener {

	/**
	 * @author Jesse Kinsey	
	 * 
	 * 	Features:
	 * 		- Transparent overlay
	 * 		- Easy to drag and resize (resizing is buggy when the mouse is faster than the computer's ability to update the window)
	 * 		- Windows and Mac friendly
	 */
	private static final long serialVersionUID = -8196844974759929846L;
	private static DraggableOverlay app;
	MainPanel window = new MainPanel();
	JPanel memberPanel = new JPanel();
	boolean dragBoolean = false, resizeBoolean = false;
	int dragCount = 0, pilotID = 1;
	int mouseLocation[] = new int[2];
	JLabel sampleLabel = new JLabel("Name:");
	JTextField sampleTextField = new JTextField();
	JButton addButton = new JButton("Add");
	JButton removeButton = new JButton("Remove");
	JButton exitButton = new JButton("Exit");
	String sampleTableColumns[] = {"name"};
	DefaultTableModel sampleModel = new DefaultTableModel(sampleTableColumns, 0);
	JTable sampleTable = new JTable(sampleModel);
	JScrollPane samplePane = new JScrollPane(sampleTable);
	private static Scanner input;

	public static void main(String[] args) throws InterruptedException {
		app = new DraggableOverlay();
		app.setSize(300, 250);
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		app.setLocation((int) (screen.width-350), 50);
		app.setTitle("NEMFA");
		app.setResizable(false);
		app.setUndecorated(true);
		app.setOpacity(.45f);
		app.setAlwaysOnTop(true);
		app.setVisible(true);
		app.setDefaultCloseOperation(EXIT_ON_CLOSE);
		input = new Scanner(System.in);
		//speeds up reaction time of window when being dragged to reduce accidental resizing
		int RUN = 1;
		do {
			Thread.sleep(1);
			app.repaint();
		} while (RUN == 1);
	}
	
	DraggableOverlay() {
		setLayout(new BorderLayout());
		
		//window
		add(window, BorderLayout.CENTER);
		window.setLayout(new GridBagLayout());
		
		//list of current items
		GridBagConstraints listSection = new GridBagConstraints();
		listSection.insets = new Insets(20, 20, 20, 0);
		listSection.weightx = 2;
		listSection.weighty = 2;
		listSection.gridx = 1;
		listSection.gridwidth = 2;	
		listSection.gridy = 1;
		listSection.gridheight = 6;
		listSection.fill = GridBagConstraints.BOTH;
		window.add(samplePane, listSection);
		
		//layout for gui
		GridBagConstraints gui = new GridBagConstraints();
		gui.fill = GridBagConstraints.HORIZONTAL;
		gui.insets = new Insets(20, 0, -20, 20);
		gui.weightx = 1;
		gui.gridx = 3;
		gui.gridwidth = 1;
		
		//text box
		gui.gridy = 1;
		window.add(sampleLabel, gui);
		gui.gridy += 1;
		window.add(sampleTextField, gui);
		
		//buttons
		gui.gridy += 1;
		window.add(addButton, gui);
		gui.gridy += 1;
		window.add(removeButton, gui);
		gui.gridy += 1;
		window.add(exitButton, gui);
		
		//action listeners
		exitButton.addActionListener(this);
		addButton.addActionListener(this);
		removeButton.addActionListener(this);
		
		//drag listeners
		window.addMouseMotionListener(this);
		samplePane.addMouseMotionListener(this);
		sampleTextField.addMouseMotionListener(this);
		addButton.addMouseMotionListener(this);
		removeButton.addMouseMotionListener(this);
		exitButton.addMouseMotionListener(this);
	}
	
	class MainPanel extends JPanel {

		/**
		 * 
		 */
		private static final long serialVersionUID = 3653986364268731808L;
		
		MainPanel() {}
		
		public void paintComponent(Graphics a) {}
		
	}

	public void actionPerformed(ActionEvent e) {
		//activates if window is not being dragged
		if(dragBoolean == false) {
			if(e.getSource() == addButton) {
				addMember(sampleTextField.getText());}
			else if(e.getSource() == removeButton) {
				try {
					removeMember(sampleTable.getSelectedRow());}
				catch(java.lang.ArrayIndexOutOfBoundsException ee) {}}
			else if(e.getSource() == exitButton) {
				this.dispose();
			}
		}
		
		//resets dragBoolean to allow action listeners to activate again
		dragBoolean = false;}

	public void mouseClicked(MouseEvent arg0) {}

	public void mouseEntered(MouseEvent arg0) {}

	public void mouseExited(MouseEvent arg0) {}

	public void mousePressed(MouseEvent arg0) {}

	public void mouseReleased(MouseEvent arg0) {}
	
	public void mouseDragged(MouseEvent e) {
		//detects window resizes and prevents the window from being dragged during resizing
		if(e.getSource() == window) {
			resizeBoolean = resizeWindow(e.getX(), e.getY(), resizeBoolean);}
		
		//move window, if window wasn't resized
		if (resizeBoolean == false) {
			setLocation(getLocation().x + e.getX() - mouseLocation[0], getLocation().y + e.getY() - mouseLocation[1]);}
		
		//reset resizeBoolean so the window can be dragged again
		resizeBoolean = false;
		
		//prevents action listeners from activating
		dragBoolean = true;}
	
	//boolean that decides how to resize the window and output whether the window was resized
	boolean resizeWindow(int x, int y, boolean resize) {
		int actionZone = 10, lagBuffer = 25;
		if(x <= actionZone) {
			resizeLeft(x);}
		if(y <= actionZone) {
			resizeTop(y);}
		if(x >= getSize().getWidth()-actionZone) {
			resizeRight(x);}
		if(y >= getSize().getHeight()-actionZone) {
			resizeBottom(y);}
		if(x <= actionZone + lagBuffer || y <= actionZone + lagBuffer || x >= getSize().getWidth() - actionZone - lagBuffer || y >= getSize().getHeight() - actionZone - lagBuffer) {
			resize = true;}
		return resize;}
	
	//resizes window from the left
	void resizeLeft(int x) {
		setSize(getSize().width - x, getSize().height);
		setLocation(getLocation().x + x, getLocation().y);}
	
	//resizes the window from the top
	void resizeTop(int y) {
		setSize(getSize().width, getSize().height - y);
		setLocation(getLocation().x, getLocation().y + y);}
	
	//resizes window from the right
	void resizeRight(int x) {
		setSize(x, getSize().height);}
	
	//resizes window from the bottom
	void resizeBottom(int y) {
		setSize(getSize().width, y);}
	
	public void mouseMoved(MouseEvent e) {
		//stores the mouse location so that the window can be dragged accurately
		mouseLocation[0] = e.getX();
		mouseLocation[1] = e.getY();
		
		//fixes the click twice after dragging glitch
		dragBoolean = false;}

	public static ArrayList<SampleTableRow> sampleTableRow = new ArrayList<SampleTableRow>();
	
	public class SampleTableRow {
		String name;
		public SampleTableRow(String n) {
			name = n;}}

	public void addMember(String name) {
		sampleTableRow.add(new SampleTableRow(name));
		String newRow[] = {sampleTextField.getText()};
		sampleModel.addRow(newRow);
		app.repaint();}
	
	public void removeMember(int index) {
		sampleTableRow.remove(index);
		sampleModel.removeRow(index);
		app.repaint();}

}
