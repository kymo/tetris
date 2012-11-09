//package tetris;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import java.util.Timer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.geom.*;
import java.util.TimerTask;

public class TetrisUI extends JFrame  {
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
        private DrawComponent dc;
        private int blockX ,blockY;
	public void initUI(){
            JPanel leftPanel = new JPanel(new BorderLayout());
            JPanel rightPanel = new JPanel(new GridLayout(6,1));
            rightPanel.add(tipNext);
            rightPanel.add(tipScore);
            rightPanel.add(gameHalt);
            rightPanel.add(totalScore);
            rightPanel.add(gameStart);
            setVisible(true);
            blockX = blockY = 1;
            dc = new DrawComponent(blockX ,blockY);
            add(dc);
            add(leftPanel,"West");
            add(rightPanel,"East");
            pack();
            this.setSize(frameWeight,frameHeight);
            this.setLocation(frameLocaX,frameLocaY);
	}
        public void movieStart()
        {
            Timer timer = new Timer();
            timer.schedule(new TimerTask(){
                public void run(){
                    blockX += 20;
                    dc.repaint();
                    if(blockX > 600)
                        blockX = 0;
                    dc.changeBlock(blockX);
                }
            },0,5*100);
        }
       
}

class DrawComponent extends JComponent{
    private final int N = 10;
    private final int M = 20;
    private boolean[][] blockFilled = new boolean[N][M];
    private int blockX,blockY;
    Thread hThread;
    public void changeBlock(int _blockX){
        blockY = _blockX;
    }
    public DrawComponent(int _blockX ,int _blockY)
    {
        blockX = _blockX ;
        blockY = _blockY;
    }
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        g2.setColor(Color.BLACK);
        System.out.println("he");
        g2.fillRect(0, 0, 300, 600);
        g2.setColor(Color.DARK_GRAY);
        g2.fillRect(blockX,blockY,30,30);
      //  throw new UnsupportedOperationException("Not yet implemented");
    }
}
