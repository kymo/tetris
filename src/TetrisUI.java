//package tetris;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

import java.util.Random;
import java.util.Timer;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
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
	public int m_blockGrid[][];
	private int m_userScore;

	//the control param of the game
	private boolean isGameHalt;
	private boolean isKeyPressed;
	private boolean isCoordinateLock;
	boolean isTransforStart;
	boolean isGameStart;
	
	private int m_blockStyle;//the current block's style
	private TetrisBlock cTetrisBlock;//the block which is moving
	private TetrisControl cTetrisControl;//the control logic of this game
    private DrawComponent cGameDc;//the game draw panel
    private DrawNextBlockComponent cNextBlockDC;//the tips for next block
    public TetrisUI()
    {
    	Random rdm = new Random();
    	m_blockStyle = Math.abs(rdm.nextInt()) % 7 + 1;
    	cTetrisBlock = new TetrisBlock(100, 0, m_blockStyle, 0);
    	m_blockGrid = new int[GRID_Y][GRID_X];
    	isCoordinateLock = false;
    	isKeyPressed = false;
    	isTransforStart = false;
    	isGameStart = false;
    	m_userScore = 0;
    	
    	for(int j = 0;j < GRID_Y;j ++)
    		for(int k = 0; k < GRID_X;k ++)
    			m_blockGrid[j][k] = 0;
    	
    	isGameHalt = false;
    }
	public void initUI(){
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
        cGameDc = new DrawComponent(this,cTetrisBlock);
        add(cGameDc);
        add(rightPanel,"East");
        pack();
        this.setSize(frameWeight,frameHeight);
        this.setLocation(frameLocaX,frameLocaY);
        m_bGameHalt.addActionListener((ActionListener) this);
        m_bGameStart.addActionListener((ActionListener) this);
        Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
           @Override
			public void eventDispatched(AWTEvent event) {
				// TODO Auto-generated method stub
        	   	if(((KeyEvent)event).getID() == KeyEvent.KEY_RELEASED)
        	   		isKeyPressed = false;
				if (((KeyEvent) event).getID() == KeyEvent.KEY_PRESSED) {
                    //放入自己的键盘监听事件
					//((KeyEvent) event).getKeyCode();// 获取按键的code
                    //((KeyEvent) event).getKeyChar();// 获取按键的字符System.out.println(e.getKeyCode());
					if(! isGameHalt && isGameStart)
					{
			    		if(((KeyEvent) event).getKeyCode() == KeyEvent.VK_RIGHT)
			    		{
			    			if(! isCoordinateLock)
			    			{
			    				isKeyPressed = true;
			    				if(! isRightToEnd(cTetrisBlock.m_blockRightX))
			    					cTetrisBlock.setCoordinateXY(cTetrisBlock.getTetrisCoordinateX() + 20,
			    							cTetrisBlock.getTetrisCoordinateY());
			    				cGameDc.repaint();
			    			}
			    		}
			    		else if(((KeyEvent) event).getKeyCode() == KeyEvent.VK_LEFT)
			    		{
			    			if(! isCoordinateLock)
			    			{
				    			isKeyPressed = true;
				    			for(int j = 0; j < 4; j ++)
				    				System.out.print(cTetrisBlock.m_blockLeftX[j] + " ");
				    			System.out.println();
				    			if(! isLeftToEnd(cTetrisBlock.m_blockLeftX))
				    			{
			    					cTetrisBlock.setCoordinateXY(cTetrisBlock.getTetrisCoordinateX() - 20,
				    						cTetrisBlock.getTetrisCoordinateY());
					    			cGameDc.repaint();
				    			}
			    			}
			    		}
			    		else if(((KeyEvent) event).getKeyCode() == KeyEvent.VK_DOWN)
			    		{
			    			if(! isCoordinateLock)
			    			{
				    			isKeyPressed = true;
				    			if(! isToEnd(cTetrisBlock.m_blockBottomY))
				    			{
			    					cTetrisBlock.setCoordinateXY(cTetrisBlock.getTetrisCoordinateX(),
			    						cTetrisBlock.getTetrisCoordinateY() + 20);
			    				}
			    				cGameDc.repaint();
			    			}
			    		}
			    		else if(((KeyEvent) event).getKeyCode() == KeyEvent.VK_UP)
			    		{
			    			if(! isCoordinateLock)
			    			{
			    				isKeyPressed = true;
			    				isTransforStart = true;
			    				cTetrisBlock.tetrisBlockShapeTransformation(m_blockGrid);
			    				cGameDc.repaint();
			    				isTransforStart = false;
			    			}
			    		}
					}
                }
			}
        }, AWTEvent.KEY_EVENT_MASK);
	}
    public void movieStart()
    {
        Timer timer = new Timer();
        timer.schedule(new TimerTask(){
            public void run(){
				//System.out.println(cTetrisBlock.getTetrisCoordinateX() + " " + cTetrisBlock.getTetrisCoordinateY());
            	if(! isGameHalt && isGameStart && ! isKeyPressed && ! isTransforStart)
            	{
            		isCoordinateLock = true;
            		/*
            		for(int j = 0; j < 4; j ++)
            			System.out.print(cTetrisBlock.m_blockRightX[j] + " ");
            		System.out.println();
	            	*/
	            	if(isToEnd(cTetrisBlock.m_blockBottomY))
	            	{
	                	for(int i = 0;i < 4;i ++)
	                		for(int j = 0;j < 4; j ++)
	                			if(cTetrisBlock.m_blockGrid[i][j] == 1)
	                			{
	                				int corX = cTetrisBlock.getTetrisCoordinateX() + j * 20;
	                				int corY = cTetrisBlock.getTetrisCoordinateY() + i * 20;
	                				m_blockGrid[corY / 20][corX / 20] = 1;
	                			}
	                	//generate new block
	                	int lineNumber = cleanPanel();
	                	m_userScore += scoreOfLineNumber(lineNumber);
	                	
	                	Random rdm = new Random();
	                	m_blockStyle = Math.abs(rdm.nextInt()) % 7 + 1;
	                	cTetrisBlock.generateNewBlock(m_blockStyle, 0);
	                }
	            	else
	            	{
	            		cTetrisBlock.setCoordinateXY(cTetrisBlock.getTetrisCoordinateX(),cTetrisBlock.getTetrisCoordinateY() + 20);
	            	}
	            	cGameDc.repaint();
	            	isCoordinateLock = false;
            	}
            }

			private int scoreOfLineNumber(int lineNumber) {
				// TODO Auto-generated method stub
				switch(lineNumber)
				{
					case 0:
						return 0;
					case 1:
						return 10;
					case 2:
						return 20;
					case 3:
						return 40;
					case 4:
						return 50;
				}
				return 0;
			}

			private int cleanPanel() {
				// TODO Auto-generated method stub
				int j;
				int ternal = 0;
				int step = 0;
				for(j = GRID_Y - 1;j >= 0;j --)
				{
					ternal = 0;
					for(int k = 0;k < GRID_X;k ++)
					{
						if(m_blockGrid[j][k] == 0)
						{
							ternal = 1;
							break;
						}
					}
					if(ternal == 0)
					{
						step ++;
						for(int k = j;k >= 1;k --)
							for(int i = 0;i < GRID_X;i ++)
								m_blockGrid[k][i] = m_blockGrid[k - 1][i];
						for(int i = 0;i < GRID_X;i ++)
							m_blockGrid[0][i] = 0;
					}
				}
				return step;
			}
        },0,5*120);
    }
    
    
    /**
     * is to the lower bottom
     * @param blockBottomY
     * @return
     */
    public boolean isToEnd(int [] blockBottomY)
    {
    	boolean flag = false;
    	for(int i = 0;i < 4;i ++)
    	{
    		//System.out.println(cTetrisBlock.getTetrisCoordinateX() + " " + cTetrisBlock.getTetrisCoordinateY());
    		int corX = cTetrisBlock.getTetrisCoordinateX() + 20 * i;
    		if(corX >= DRAWPANEL_WEIGHT)
    			continue;
    		int corY = blockBottomY[i];
    		//System.out.println("bottomY:" + corY);
    		if(corY >= DRAWPANEL_HEIGHT)
    		{
    			flag = true;
    			break;
    		}
    		else if( corX >= 0 && m_blockGrid[corY / 20][corX / 20] == 1)
    		{
    			flag = true;
    			break;
    		}
    	}
    	
    	return flag;
    }
    
    /**
     * judge whether current block can move to left
     * @param blockLeftX
     * @return
     */
    public boolean isLeftToEnd(int [] blockLeftX)
    {
    	boolean flag = false;
    	for(int i = 0; i < 4; i ++)
    	{
    		int corX = blockLeftX[i];
    		int corY = cTetrisBlock.getTetrisCoordinateY() + 20 * i;
    		//System.out.println("leftX:" + corX);
    		if(corX == -1)
    			continue;
    		if(corX <= 0 )
    		{
    			flag = true;
    			break;
    		}
    		else if(m_blockGrid[corY / 20][(corX - 20 )/ 20] == 1)
    		{
    			flag = true;
    			break;
    		}
    	}
		return flag;
    }
    
    /**
     * judge whether current block can move to right
     * @param blockRightX
     * @return
     */
    public boolean isRightToEnd(int [] blockRightX)
    {
    	boolean flag = false;
    	for(int i = 0; i < 4; i ++)
    	{
    		int corX = blockRightX[i];
    		int corY = cTetrisBlock.getTetrisCoordinateY() + 20 * i;
    		if(corX == -1)
    			continue;
    		//System.out.println("RightR:" + corX);
    		if(corX >= DRAWPANEL_WEIGHT)
    		{
    			flag = true;
    			break;
    		}
    		else if(m_blockGrid[corY / 20][(corX)/ 20] == 1)
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
			isGameStart = true;
			isGameHalt = false;
		}
		else if(e.getSource() == m_bGameHalt){
			m_bGameHalt.setEnabled(false);
			isGameHalt = true;
			m_bGameStart.setText("continue");
		}
		else if(e.getSource() == m_bGameOver)
		{
			isGameStart = false;
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
    TetrisUI cTetrisUI;
    TetrisBlock cTetrisBlock;
  //the draw panel size
	private final int DRAWPANEL_WEIGHT = 300;
	private final int DRAWPANEL_HEIGHT = 500;
    private final int BLOCK_SIZE = 20;
    public DrawComponent(TetrisUI  tetrisUI,TetrisBlock tetrisBlock){
    	cTetrisUI = tetrisUI;
    	cTetrisBlock = tetrisBlock;
    }
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        //draw the background
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, DRAWPANEL_WEIGHT, DRAWPANEL_HEIGHT);
        g2.setColor(Color.DARK_GRAY);
      
        for(int j = 0 ;j < 25;j ++)
        	for(int k = 0 ; k < 15;k ++)
        		if(cTetrisUI.m_blockGrid[j][k] == 1)
        			g2.fillRect(20*k, 20*j, BLOCK_SIZE,  BLOCK_SIZE);
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
