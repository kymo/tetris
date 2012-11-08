import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.geom.*;

public class TetrisUI extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JButton gameStart = new JButton("Start");
	private JButton gameHalt = new JButton("Halt");
	private JLabel tipNext = new JLabel("Next");
	private JLabel tipScore = new JLabel("Score");
	private JLabel totalScore = new JLabel("0");
	private final int frameWeight = 400;
	private final int frameHeight = 600;
	private final int frameLocaX = 0;
	private final int frameLocaY = 0;
	public void initUI()
	{
		JPanel leftPanel = new JPanel(new BorderLayout());
		JPanel rightPanel = new JPanel(new GridLayout(6,1));
		rightPanel.add(tipNext);
		rightPanel.add(tipScore);
		rightPanel.add(gameHalt);
		rightPanel.add(totalScore);
		rightPanel.add(gameStart);
		add(leftPanel,"West");
		add(rightPanel,"East");
		pack();
		show();
		this.setSize(frameWeight,frameHeight);
		this.setLocation(frameLocaX,frameLocaY);
	}
}
