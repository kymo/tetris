//package tetris;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

import java.util.Random;
import java.util.Timer;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Font;
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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.*;
import java.util.TimerTask;

public class TetrisUI extends JFrame implements ActionListener {

    //the init data for the UI frame
    private final int frameWeight = 520;
    private final int frameHeight = 600;
    private final int frameLocaX = 0;
    private final int frameLocaY = 0;
    //the draw panel size
    private final int DRAWPANEL_WEIGHT = 300;
    private final int DRAWPANEL_HEIGHT = 500;
    private final int GRID_X = 15;
    private final int GRID_Y = 25;
    private final String kymoBlog = "http://kymowind.blog.163.com";
    
    //the control tools in the UI frame
    private JButton m_bGameStart = new JButton("Start");
    private JButton m_bGameHalt = new JButton("Halt");
    private JLabel m_lTipForScore = new JLabel("Score");
    private JLabel m_lGameTip = new JLabel("");
    private JButton m_bGameOver = new JButton("Over");
    private JButton m_bVoiceControl = new JButton("Voice Control");
    private JLabel m_lTotalScore = new JLabel("0s");
    public int m_blockGrid[][];//the related block grid of the game panel
    private int m_userScore;//user's total score

    //the control param of the game
    private boolean isGameHalt;//is game halt
    private boolean isKeyPressed;//is key pressed
    private boolean isCoordinateLock;//signal for changing value of coordinate
    boolean isTransforStart;//is transformation start (signal)
    boolean isGameStart;//is game start
    boolean isGameOver;//is game over
    boolean isNextReady;//is next block ready
    private int m_blockStyle;//the current block's style
    private TetrisBlock cTetrisBlock;//the block which is moving
    private TetrisControl cTetrisControl;//the control logic of this game
    private DrawComponent cGameDc;//the game draw panel
    private DrawNextBlockComponent cNextBlockDC;//the tips for next block
    /**
     * the structure function of TetrisUI
     * @param none
     */
    public TetrisUI()
    {
        Random rdm = new Random();
        m_blockStyle = Math.abs(rdm.nextInt()) % 7 + 1;
        cTetrisBlock = new TetrisBlock(100, 0, m_blockStyle, 0);
        ////System.out.println("block style " + m_blockStyle);
        cNextBlockDC = new DrawNextBlockComponent();
        rdm = new Random();
        //set next block style
        setBlockStyle(Math.abs(rdm.nextInt()) % 7 + 1);
        //set the next block game dc
        getNextBlockDC().setNextBlockStyle(getBlockStyle());
        m_blockGrid = new int[GRID_Y][GRID_X];
        //the param of control
        isCoordinateLock = false;
        isKeyPressed = false;
        isTransforStart = false;
        isGameStart = false;
        isGameOver = false;
        isNextReady = false;
        m_userScore = 0;
        //set text
        m_lTotalScore.setText(Integer.toString(m_userScore));
        m_bGameHalt.setEnabled(false);
        for(int j = 0;j < GRID_Y;j ++)
            for(int k = 0; k < GRID_X;k ++)
                m_blockGrid[j][k] = 0;
        isGameHalt = false;
    }
    /**
     * inti the UI and some params
     * @param none
     */
    public void initUI(){
        //cNextBlockDC.setLayout(new BorderLayout());
        getContentPane().setLayout(null);
        //the total panel
        JPanel outer = new JPanel();
        getContentPane().add(outer);
        outer.setBounds(4,4,frameWeight - 20,frameHeight - 50);
        outer.setBorder(BorderFactory.createTitledBorder("Game DC"));
        outer.setLayout(null);
        
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(null);
        //the panel to show next block
        JPanel tipNext = new JPanel();
        tipNext.setBounds(10,30,130,80);
        tipNext.setBorder(BorderFactory.createTitledBorder("Next Block"));
        tipNext.setLayout(null);
        cNextBlockDC.setBounds(25,20,48,48);
        tipNext.add(cNextBlockDC);
        rightPanel.add(tipNext);
        
        //the panel to show the score
        JPanel playerScore = new JPanel();
        playerScore.setBounds(10,110,130,40);
        playerScore.setBorder(BorderFactory.createTitledBorder("Score"));
        playerScore.setLayout(null);
        m_lTotalScore.setBounds(40,15,100,20);
        playerScore.add(m_lTotalScore);
        rightPanel.add(playerScore);
        
        //the panel to show the button
        JPanel buttonArea = new JPanel();
        buttonArea.setBounds(10,160,130,170);
        buttonArea.setBorder(BorderFactory.createTitledBorder("Game Control"));
        buttonArea.setLayout(null);
        m_bGameHalt.setBounds(10,30,110,30);
        buttonArea.add(m_bGameHalt);
        m_bGameStart.setBounds(10,62,110,30);
        buttonArea.add(m_bGameStart);
        m_bGameOver.setBounds(10,94,110,30);
        buttonArea.add(m_bGameOver);
        m_bVoiceControl.setBounds(10,126,110,30);
        buttonArea.add(m_bVoiceControl);
        
        rightPanel.add(buttonArea);
        //the Panel to show the tips for the game
        JPanel gameTip = new JPanel();
        gameTip.setBounds(10,330,130,160);
        gameTip.setBorder(BorderFactory.createTitledBorder("Game Tip"));
        gameTip.setLayout(null);
        m_lGameTip.setBounds(10,20,130,100);
        Font fnt=new Font("楷体_GB2312",Font.ITALIC,15);
        m_lGameTip.setText("<html><body>" +
                "<font style='color:#2f4f4f;size:10px;'>" +
                "1.由方向键控制游戏<br/>,向上有变形;<br/>2." +
                "语音控制命令见配<br/>置文件;<br/>3.Author:");
        //the link for blog
        JLabel linkForBlog = new JLabel("<html><body><a style='color:#00755e;' href ='#'>kymo</a></body></html>");
        linkForBlog.setCursor(new Cursor(Cursor.HAND_CURSOR));
        linkForBlog.setBounds(20,113,130,15);
        //add mouse event
        linkForBlog.addMouseListener(new MouseAdapter()
        {
            public void mouseClicked(MouseEvent e)
            {
                ////System.out.println("click !");
                try {
                    Desktop.getDesktop().browse(  
                            new java.net.URI(kymoBlog));
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (URISyntaxException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }  
            }
        });
        //the link to open voice xml file
        JLabel linkForXml = new JLabel("<html><body><a style='color:#00755e;' href ='#'>open voice xml</a></body></html>");
        linkForXml.setCursor(new Cursor(Cursor.HAND_CURSOR));
        linkForXml.setBounds(20,128,130,15);
        //add mouse event
        linkForXml.addMouseListener(new MouseAdapter()
        {
            public void mouseClicked(MouseEvent e)
            {
                //JLabel xmlFileContent = new JLabel();
                try
                {  
                    Runtime.getRuntime().exec("cmd /c start "+ System.getProperty("user.dir") + "\\voice.xml");  
                }
                catch(IOException ex)
                {  
                    ////System.out.println(ex);  
                }  
            }
        });
        gameTip.add(linkForBlog);
        gameTip.add(linkForXml);
        gameTip.add(m_lGameTip);
        rightPanel.add(gameTip);

        //pack();
        rightPanel.setBounds(340,30,150,500);
        rightPanel.setBorder(BorderFactory.createTitledBorder("Game Control&Tips"));
     
        outer.add(rightPanel);
        cGameDc = new DrawComponent(this,cTetrisBlock);
        cGameDc.setBounds(30,30,300,500);
        outer.add(cGameDc);
        setVisible(true);
        setResizable(false);
        pack();
        this.setSize(frameWeight,frameHeight);
        this.setLocation(frameLocaX,frameLocaY);
        m_bGameHalt.addActionListener((ActionListener) this);
        m_bGameStart.addActionListener((ActionListener) this);
        m_bGameOver.addActionListener((ActionListener) this);
        m_bVoiceControl.addActionListener((ActionListener) this);
        //if the windows is close ,the game thread will end
        this.addWindowListener(new WindowAdapter()
        {
           public void windowClosing(WindowEvent e)
           {
                isGameOver = true;
                System.exit(0);
            }
        });
        
        //the listen of the keyboard
        Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
           @Override
            public void eventDispatched(AWTEvent event) {
                // TODO Auto-generated method stub
                   if(((KeyEvent)event).getID() == KeyEvent.KEY_RELEASED)
                       isKeyPressed = false;
                if (((KeyEvent) event).getID() == KeyEvent.KEY_PRESSED) {
                    //放入自己的键盘监听事件
                    //((KeyEvent) event).getKeyCode();// 获取按键的code
                    //((KeyEvent) event).getKeyChar();// 获取按键的字符////System.out.println(e.getKeyCode());
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
                                /*
                                for(int j = 0; j < 4; j ++)
                                    //System.out.print(cTetrisBlock.m_blockLeftX[j] + " ");
                                */
                                ////System.out.println();
                                if(! isLeftToEnd(cTetrisBlock.m_blockLeftX))
                                    cTetrisBlock.setCoordinateXY(cTetrisBlock.getTetrisCoordinateX() - 20,
                                            cTetrisBlock.getTetrisCoordinateY());
                                cGameDc.repaint();
                            }
                        }
                        else if(((KeyEvent) event).getKeyCode() == KeyEvent.VK_DOWN)
                        {
                            if(! isCoordinateLock)
                            {
                                isKeyPressed = true;
                                if(! isToEnd(cTetrisBlock.m_blockBottomY))
                                    cTetrisBlock.setCoordinateXY(cTetrisBlock.getTetrisCoordinateX(),
                                        cTetrisBlock.getTetrisCoordinateY() + 20);
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
    /**
     * game start
     */
    public void gameStart()
    {
        Timer timer = new Timer();
        timer.schedule(new TimerTask()
        {
            public void run()
            {
                //////System.out.println(cTetrisBlock.getTetrisCoordinateX() + " " + cTetrisBlock.getTetrisCoordinateY());
                if(! isGameHalt && isGameStart && ! isKeyPressed && ! isTransforStart)
                {
                    isCoordinateLock = true;
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
                        //clean the formed block line
                        int lineNumber = cleanPanel();
                        m_userScore += scoreOfLineNumber(lineNumber);
                        m_lTotalScore.setText(Integer.toString(m_userScore));
                        //generate new block
                        if(! isNextReady)
                            setNextReady(true);
                        cTetrisBlock.generateNewBlock(m_blockStyle, 0);
                        //set the next block and show it
                        if(! isGenerateLegal())
                            isGameOver = true;
                    }
                    
                    else
                    {
                        cTetrisBlock.setCoordinateXY(cTetrisBlock.getTetrisCoordinateX(),cTetrisBlock.getTetrisCoordinateY() + 20);
                    }
                    cGameDc.repaint();
                    cNextBlockDC.repaint();
                    isCoordinateLock = false;
                }
                //if game is over
                if(isGameOver)
                {
                    
                    int choose = JOptionPane.showConfirmDialog(null, "游戏结束，是否重新开始？","提示:", JOptionPane.YES_NO_OPTION);
                    if(choose == JOptionPane.OK_OPTION)
                    {
                        for(int j = 0;j < GRID_Y;j ++)
                            for(int k = 0;k < GRID_X;k ++)
                                m_blockGrid[j][k] = 0;
                        //start again you need to init the block
                        Random rdm = new Random();
                        m_blockStyle = Math.abs(rdm.nextInt()) % 7 + 1;
                        cTetrisBlock.generateNewBlock(m_blockStyle, 0);
                        
                        m_userScore = 0;
                        isGameOver = false;
                        isGameStart = false;
                        isGameHalt = false;
                        m_bGameStart.enable(true);
                        cGameDc.repaint();
                    }
                    else if(choose == JOptionPane.NO_OPTION)
                    {
                        System.exit(0);
                    }
                }
            }
        },0,5*120);
    }
    /**
     * 
     * @return
     */
    private boolean isGenerateLegal() {
        // TODO Auto-generated method stub
        for(int j = 0; j < 4; j ++)
        {
            for(int k = 0; k < 4; k ++)
            {
                if(cTetrisBlock.m_blockGrid[j][k] == 1)
                {
                    int corX = cTetrisBlock.getTetrisCoordinateX() + k * 20;
                    int corY = cTetrisBlock.getTetrisCoordinateY() + j * 20;
                    if(m_blockGrid[corY / 20][corX / 20] == 1)
                        return false;
                }
            }
        }
        return true;
    }
    /**
     * according to the wiped out line number ,get the score
     * @param lineNumber
     * @return
     */
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
    /**
     * clean panel
     * @return
     */
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
                //////System.out.println("hello once agmel" + j);
                for(int k = j;k >= 1;k --)
                    for(int i = 0;i < GRID_X;i ++)
                        m_blockGrid[k][i] = m_blockGrid[k - 1][i];
                for(int i = 0;i < GRID_X;i ++)
                    m_blockGrid[0][i] = 0;
                j ++ ;
            }
        }
        return step;
    }
    /**
     * the method for c++ dll
     */
    public void dealWithLeft()
    {
        System.out.println("nihaoma");
        if(! isCoordinateLock)
        {
            if(! isLeftToEnd(cTetrisBlock.m_blockLeftX))
                cTetrisBlock.setCoordinateXY(cTetrisBlock.getTetrisCoordinateX() - 20,
                        cTetrisBlock.getTetrisCoordinateY());
            cGameDc.repaint();
        }
    }
    
    public void dealWithRight()
    {
        if(! isCoordinateLock)
        {
            if(! isRightToEnd(cTetrisBlock.m_blockRightX))
                cTetrisBlock.setCoordinateXY(cTetrisBlock.getTetrisCoordinateX() + 20,
                        cTetrisBlock.getTetrisCoordinateY());
            cGameDc.repaint();
        }
    }
    public void dealWithDown()
    {
        if(! isCoordinateLock)
        {
            if(! isToEnd(cTetrisBlock.m_blockBottomY))
                cTetrisBlock.setCoordinateXY(cTetrisBlock.getTetrisCoordinateX(),
                    cTetrisBlock.getTetrisCoordinateY() + 20);
            cGameDc.repaint();
        }
    }
    public void dealWithTrans()
    {
        if(! isCoordinateLock)
        {
            isTransforStart = true;
            cTetrisBlock.tetrisBlockShapeTransformation(m_blockGrid);
            cGameDc.repaint();
            isTransforStart = false;
        }
    }
    public void dealWithGameOver()
    {
        System.exit(0);
    }
    public int getVoiceState()
    {
        return 0;
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
            ////////System.out.println(cTetrisBlock.getTetrisCoordinateX() + " " + cTetrisBlock.getTetrisCoordinateY());
            int corX = cTetrisBlock.getTetrisCoordinateX() + 20 * i;
            if(corX >= DRAWPANEL_WEIGHT)
                continue;
            int corY = blockBottomY[i];
            //////////System.out.println("bottomY:" + corY);
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
     * set next ready
     */
    public void setNextReady(boolean isNextR)
    {
        isNextReady = isNextR;
    }
    /**
     * set block style
     */
    public void setBlockStyle(int blockStyle)
    {
        m_blockStyle = blockStyle;
    }
    /**
     * get block style
     */
    public int getBlockStyle()
    {
        return m_blockStyle;
    }
    /**
     * get next block area panel
     */
    public DrawNextBlockComponent getNextBlockDC()
    {
        return cNextBlockDC;
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
            //////////System.out.println("leftX:" + corX);
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
            //////////System.out.println("RightR:" + corX);
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
            isGameStart = true;
            isGameHalt = false;
            isGameOver = false;
            m_bGameStart.setEnabled(false);
            m_bGameHalt.setEnabled(true);
            setNextReady(true);
        }
        else if(e.getSource() == m_bGameHalt){
            m_bGameHalt.setEnabled(false);
            m_bGameStart.setEnabled(true);
            isGameHalt = true;
            m_bGameStart.setText("continue");
        }
        else if(e.getSource() == m_bGameOver)
        {
            isGameStart = false;
            isGameOver = true;
            ////System.out.println("ok \n");
            System.exit(0);
        }
        
        //if is voice control
        else if(e.getSource() == m_bVoiceControl)
        {
            if(m_bVoiceControl.getText().equalsIgnoreCase("Voice control"))
            {
                m_bVoiceControl.setText("Cancel VC");
                //start voice control
                TetrisControl tc = new TetrisControl(this);
                tc.start();
            }
            else
            {
                m_bVoiceControl.setText("Voice control");
            }
        }
    }  
}

class DrawNextBlockComponent extends JPanel{
    private int m_nextBlockStyle;
    private final int N = 4;
    private TetrisBlock cTetrisBlock;
    public void setNextBlockStyle(int nextBlockStyle)
    {
        m_nextBlockStyle = nextBlockStyle;
        cTetrisBlock = new TetrisBlock(5, 5, m_nextBlockStyle, 0);
    }
    public void paintComponent(Graphics g){
        Graphics2D gs = (Graphics2D)g;
         //draw the background
        gs.setColor(Color.lightGray);
        gs.fillRect(0,0, 48, 48);
        for(int i = 0;i < 4;i ++){
            for(int j = 0;j < 4;j ++){
                if(cTetrisBlock.m_blockGrid[i][j] == 1){
                    ////System.out.println(cTetrisBlock.getTetrisCoordinateX() + "  " + cTetrisBlock.getTetrisCoordinateY() );
                    //draw the block
                    int corX = cTetrisBlock.getTetrisCoordinateX() + 10 * j;
                    int corY = cTetrisBlock.getTetrisCoordinateY() + 10 * i;
                    gs.setColor(new Color(100,10,30));
                    gs.fillRect(corX, corY, 10, 10);
                    //draw the outline
                    gs.setColor(new Color(230,230,200));
                    gs.drawLine(corX, corY, corX, corY + 10);
                    gs.drawLine(corX, corY, corX + 10, corY);
                    gs.drawLine(corX + 10, corY + 10, corX, corY + 10);
                    gs.drawLine(corX + 10, corY + 10, corX + 10, corY);
                }
            }
        }
    }
}

class DrawComponent extends JComponent{
    TetrisUI cTetrisUI;
    TetrisBlock cTetrisBlock;
  //the draw panel size
    private final int DRAWPANEL_WEIGHT = 300;
    private final int DRAWPANEL_HEIGHT = 500;
    private final int BLOCK_SIZE = 20;
    public DrawComponent(TetrisUI tetrisUI,TetrisBlock tetrisBlock){
        cTetrisUI = tetrisUI;
        cTetrisBlock = tetrisBlock;
    }
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        //draw the background
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, DRAWPANEL_WEIGHT, DRAWPANEL_HEIGHT);
      
        for(int j = 0 ;j < 25;j ++)
            for(int k = 0 ; k < 15;k ++)
                if(cTetrisUI.m_blockGrid[j][k] == 1)
                {
                    int corX = 20 * k;
                    int corY = 20 * j;
                    g2.setColor(Color.DARK_GRAY);
                    g2.fillRect(20*k, 20*j, BLOCK_SIZE,  BLOCK_SIZE);
                    g2.setColor(Color.white);
                    g2.drawLine(corX, corY, corX, corY + 20);
                    g2.drawLine(corX, corY, corX + 20, corY);
                    g2.drawLine(corX + 20, corY + 20, corX, corY + 20);
                    g2.drawLine(corX + 20, corY + 20, corX + 20, corY);
                }
        //draw the block which's on moving
        /*
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
        */
        if(cTetrisUI.isGameStart)
        {
            if(cTetrisUI.isNextReady)
            {
                cTetrisUI.setNextReady(false);
                Random rdm = new Random();
                cTetrisUI.setBlockStyle(Math.abs(rdm.nextInt()) % 7 + 1);
                cTetrisUI.getNextBlockDC().setNextBlockStyle(cTetrisUI.getBlockStyle());
                ////System.out.println("block style " + cTetrisUI.getBlockStyle());
                cTetrisUI.getNextBlockDC().repaint();
            }
            for(int i = 0;i < 4;i ++){
                for(int j = 0;j < 4;j ++){
                    if(cTetrisBlock.m_blockGrid[i][j] == 1){
                        g2.setColor(Color.white);
                        int corX = cTetrisBlock.getTetrisCoordinateX() + 20 * j;
                        int corY = cTetrisBlock.getTetrisCoordinateY() + 20 * i;
                        g2.fillRect(corX, corY, 20, 20);
                        //outline
                        g2.setColor(Color.gray);
                        g2.drawLine(corX, corY, corX, corY + 20);
                        g2.drawLine(corX, corY, corX + 20, corY);
                        g2.drawLine(corX + 20, corY + 20, corX, corY + 20);
                        g2.drawLine(corX + 20, corY + 20, corX + 20, corY);
                        
                    }
                }
            }
        }
        //g2.fillRect(cTetrisBlock.getTetrisCoordinateX(),cTetrisBlock.getTetrisCoordinateY(),BLOCK_SIZE,BLOCK_SIZE);
    }
    
}
