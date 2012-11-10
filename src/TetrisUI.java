//package tetris;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

import java.util.Random;
import java.util.Timer;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.*;
import java.util.TimerTask;

public class TetrisUI extends JFrame implements ActionListener {

	//the init data for the UI frame
	private final int frameWeight = 400;
	private final int frameHeight = 600;
	private final int frameLocaX = 0;
	private final int frameLocaY = 0;
	//the draw panel size
	private final int DRAWPANEL_WEIGHT = 300;
	private final int DRAWPANEL_HEIGHT = 500;
	private final int GRID_X = 15;
	private final int GRID_Y = 25;
	
	//the control tools in the UI frame
	private JButton m_bGameStart = new JButton("Start");
	private JButton m_bGameHalt = new JButton("Halt");
	private JLabel m_lTipNext = new JLabel("Next");
	private JLabel m_lTipScore = new JLabel("Score");
	private JButton m_bGameOver = new JButton("Over");
	private JLabel m_lTotalScore = new JLabel("0");
	private int m_blockGrid[][];
	
	//the control param of the game
	private boolean isGameHalt;
	
	private int m_blockStyle;//the current block's style
	private TetrisBlock cTetrisBlock;//the block which is moving
	private TetrisControl cTetrisControl;//the control logic of this game
    private DrawComponent cGameDc;//the game draw panel
    private DrawNextBlockComponent cNextBlockDC;//the tips for next block
    
    public class key extends KeyAdapter
    {
    	public void keyPressed(KeyEvent e)
    	{
    		System.out.println(e.getKeyCode());
    		if(e.getKeyCode() == KeyEvent.VK_RIGHT)
    		{
    			
    			if(cTetrisBlock.getTetrisCoordinateX() + 20 <= DRAWPANEL_WEIGHT)
    				cTetrisBlock.setCoordinateXY(cTetrisBlock.getTetrisCoordinateX() + 20,
    						cTetrisBlock.getTetrisCoordinateY());
    		}
    		else if(e.getKeyCode() == KeyEvent.VK_LEFT)
    		{
    			if(cTetrisBlock.getTetrisCoordinateX() - 20 >= 0)
    				cTetrisBlock.setCoordinateXY(cTetrisBlock.getTetrisCoordinateX() - 20,
    						cTetrisBlock.getTetrisCoordinateY());
    		}
    		else if(e.getKeyCode() == KeyEvent.VK_DOWN)
    		{
    			if(cTetrisBlock.getTetrisCoordinateY() + 20 <= DRAWPANEL_HEIGHT)
    				cTetrisBlock.setCoordinateXY(cTetrisBlock.getTetrisCoordinateX(),
    						cTetrisBlock.getTetrisCoordinateY() + 20);
    		}
    		else if(e.getKeyCode() == KeyEvent.VK_UP)
    		{
    			cTetrisBlock.tetrisBlockShapeTransformation();
    		}
    	}
    }
    public TetrisUI()
    {
    	Random rdm = new Random();
    	m_blockStyle = Math.abs(rdm.nextInt()) % 7 + 1;
    	cTetrisBlock = new TetrisBlock(100, 0, m_blockStyle, 0);
    	System.out.println("nnnnn");
    	System.out.println(cTetrisBlock.getTetrisCoordinateX());
    	System.out.println(cTetrisBlock.getTetrisCoordinateY());
    	System.out.println("...'");
    	m_blockGrid = new int[GRID_Y][GRID_X];
    	for(int j = 0;j < GRID_Y;j ++)
    		for(int k = 0; k < GRID_X;k ++)
    			m_blockGrid[j][k] = 0;
    	
    	isGameHalt = false;
    }
	public void initUI(){
		key key1 = new key();
        cNextBlockDC = new DrawNextBlockComponent();
        cNextBlockDC.setLayout(new BorderLayout());
        JPanel leftPanel = new JPanel(new BorderLayout());
        JPanel rightPanel = new JPanel(new GridLayout(12,1));
        m_lTipNext.setSize(30,50);
        rightPanel.add(m_lTipNext);
        rightPanel.add(cNextBlockDC);
        rightPanel.add(m_lTipScore);
        rightPanel.add(m_lTotalScore);
        rightPanel.add(m_bGameHalt);
        rightPanel.add(m_bGameStart);
        rightPanel.add(m_bGameOver);
        setVisible(true);
        cGameDc = new DrawComponent(cTetrisBlock);
        add(cGameDc);
        add(rightPanel,"East");
        pack();
        this.setSize(frameWeight,frameHeight);
        this.setLocation(frameLocaX,frameLocaY);
        m_bGameHalt.addActionListener((ActionListener) this);
        m_bGameStart.addActionListener((ActionListener) this);
        this.addKeyListener(key1);
	}
    public void movieStart()
    {
        Timer timer = new Timer();
        timer.schedule(new TimerTask(){
            public void run(){
            	//System.out.println(cTetrisBlock.getTetrisCoordinateX() + " " + cTetrisBlock.getTetrisCoordinateY());
            	if(! isGameHalt)
            	{
	                cTetrisBlock.setCoordinateXY(cTetrisBlock.getTetrisCoordinateX(),cTetrisBlock.getTetrisCoordinateY() + 20);
	            	cGameDc.repaint();
	                for(int j = 0;j < 4;j ++)
	                	System.out.print(cTetrisBlock.m_blockBottomY[j] + " ");
	                System.out.println();
	                
	                if(isToEnd(cTetrisBlock.m_blockBottomY)){
	                	for(int i = 0;i < 4;i ++)
	                		for(int j = 0;j < 4; j ++)
	                			if(cTetrisBlock.m_blockGrid[i][j] == 1)
	                			{
	                				int corX = cTetrisBlock.getTetrisCoordinateX() + j * 20;
	                				int corY = cTetrisBlock.getTetrisCoordinateY() + i * 20;
	                				System.out.println(corX/20 + " ;;;" + corY/20);
	                				m_blockGrid[corY / 20][corX / 20] = 1;
	                			}
	                }
            	}
            }
        },0,5*120);
    }
    
    public boolean isToEnd(int [] blockBottomY)
    {
    	boolean flag = false;
    	for(int i = 0;i < 4;i ++)
    	{
    		//System.out.println(cTetrisBlock.getTetrisCoordinateX() + " " + cTetrisBlock.getTetrisCoordinateY());
    		int corX = cTetrisBlock.getTetrisCoordinateX() + 20 * i;
    		int corY = blockBottomY[i];
    		//System.out.println(corX + " .... " + corY);
    		if(corY == DRAWPANEL_HEIGHT || m_blockGrid[corY / 20][corX / 20] == 1)
    		{
    			flag = true;
    			break;
    		}
    	}
    	return flag;
    }
	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == m_bGameStart){
			m_bGameStart.setEnabled(false);
			cTetrisControl.setGameStatus(1);
			isGameHalt = false;
		}
		else if(e.getSource() == m_bGameHalt){
			m_bGameHalt.setEnabled(false);
			isGameHalt = true;
			cTetrisControl.setGameStatus(-1);
		}
		else if(e.getSource() == m_bGameOver)
		{
			cTetrisControl.setGameStatus(0);
		}
	}  
}

class DrawNextBlockComponent extends JPanel{
	private int m_nextBlockStyle;
	private final int N = 4;
	public void setNextBlockStyle(int nextBlockStyle){
		m_nextBlockStyle = nextBlockStyle;
	}
	public void paintComponent(Graphics g){
		Graphics2D g2 = (Graphics2D)g;
		 //draw the background
        g2.setColor(Color.BLACK);
        g2.fillRect(0,0, 48, 48);
	}
}

class DrawComponent extends JComponent{
    TetrisBlock cTetrisBlock;
  //the draw panel size
	private final int DRAWPANEL_WEIGHT = 300;
	private final int DRAWPANEL_HEIGHT = 500;
    private final int BLOCK_SIZE = 20;
    public DrawComponent(TetrisBlock  tetrisBlock){
    	cTetrisBlock = tetrisBlock;
    }
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        //draw the background
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, DRAWPANEL_WEIGHT, DRAWPANEL_HEIGHT);
        g2.setColor(Color.DARK_GRAY);
        //draw the block which's on moving
        
        for(int j = 0;j <= 15;j ++)
        {
        	int corX = 20 * j;
        	g2.setColor(Color.white);
        	g2.drawLine(corX, 0, corX, 500);
        }
        for(int j = 0;j <= 25;j ++)
        {
        	int corY = 20*j;
        	g2.setColor(Color.white);
        	g2.drawLine(0,corY,300,corY);
        }
        for(int j = 0;j <= 4;j ++)
        {
        	int corX = cTetrisBlock.getTetrisCoordinateX() + 20 * j;
        	int corY = cTetrisBlock.getTetrisCoordinateY();
        	g2.setColor(Color.BLUE);
        	g2.drawLine(corX , corY, corX, corY + 80);
        }
        for(int j = 0;j <= 4;j ++)
        {
        	int corX = cTetrisBlock.getTetrisCoordinateX();
        	int corY = cTetrisBlock.getTetrisCoordinateY() + 20 * j;
        	g2.setColor(Color.BLUE);
        	g2.drawLine(corX , corY, corX + 80, corY);
        }
        g2.setColor(Color.white);
        
        for(int i = 0;i < 4;i ++){
        	for(int j = 0;j < 4;j ++){
        		if(cTetrisBlock.m_blockGrid[i][j] == 1){
            		//System.out.println(cTetrisBlock.getTetrisCoordinateX() + "  " + cTetrisBlock.getTetrisCoordinateY() );
        			int corX = cTetrisBlock.getTetrisCoordinateX() + 20 * j;
        			int corY = cTetrisBlock.getTetrisCoordinateY() + 20 * i;
        			g2.fillRect(corX, corY, 20, 20);
        		}
        	}
        	//System.out.println();
        }
        //g2.fillRect(cTetrisBlock.getTetrisCoordinateX(),cTetrisBlock.getTetrisCoordinateY(),BLOCK_SIZE,BLOCK_SIZE);
    }
    
}
