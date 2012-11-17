

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
//package tetris;

/**
 * @author kymo
 */
public class TetrisBlock {
	private final int N = 4;
	private final int LEFT_BLOCK_FOLD = 1;
	private final int RIGHT_BLOCK_FOLD = 2;
	private final int BLOCK_LINE = 3;
	private final int BLOCK_DIAMOND = 4;
	private final int LEFT_VERTICAL_BENDING = 5;
	private final int RIGHT_VERTICAL_BENDING = 6;
	private final int BLOCK_NONE = 7;
	private final int BLOCK_INIT_DIRECTION = 0;
	private final int DRAWPANEL_WEIGHT = 300;
	private final int DRAWPANEL_HEIGHT = 500;
	private int m_blockDirection;//the block's shape
	
	private int m_blockStyle;//the block's style  
	public int m_blockGrid[][];//the filled grid of the block
	public int m_blockBottomY[];//the block's bottom Y coordinate
	public int m_blockLeftX[];//the block's left X coordinate
	public int m_blockRightX[];//the block's right X coordinate
	public int m_blockUy;//the block's upper boundary's y coordinate
	public int m_blockLy;// the block's lower boundary's y coordinate
	public int m_blockLx;//the block's left border's x coordinate
	public int m_blockRx;//the block's right border's x coordinate
	public boolean isToEnd;//is the block's get to the bottom
	private int m_blockCoordinateX;//the block's filled grid's left-top x coordinate 
	private int m_blockCoordinateY;//the block's filled grid's left-top y coordinate 
	private int m_blockShape[][] = {
	       {0x6220,0x1700,0x2230,0x0740}, //four shapes for LEFT_BLOCK_FOLD
	       {0x6440,0x0e20,0x44c0,0x8e00}, //four shapes for RIGHT_BLOCK_FOLD
	       {0x0f00,0x4444,0x0f00,0x4444},//four shapes for BLOCK_LINE
	       {0x0660,0x0660,0x0660,0x0660}, // four shapes for BLOCK_DIAMOND
	       {0x4620,0x6c00,0x4620,0x6c00},//four shapes for LEFT_BLOCK_VERTICAL_BENDING
	       {0x2640,0xc600,0x2640,0xc600}, //four shapes for RIGHT_BLOCK_VERTICAL_BENDING
	       {0x04e0,0x0464,0x00e4,0x04c4}//four shapes for _|_ 
	};
	
	
	/**
	 * get the x coordinate of current block
	 * @return
	 */
	int getTetrisCoordinateX(){
		return m_blockCoordinateX;
	}
	/**
	 * get the y coordinate of current block
	 * @return
	 */
	int getTetrisCoordinateY()
	{
		return m_blockCoordinateY;
	}
	
	/**
	 * set the coordinate (X,Y)
	 * @param X
	 * @param Y
	 */
	void setCoordinateXY(int X,int Y)
	{	
		m_blockCoordinateX = X;
		m_blockCoordinateY = Y;
		changeBlockScope();
			
	}
	/**
	 * init the block's occupied grids and the start location
	 * @param blockWeight
	 * @param blockHeight
	 * @param blockLocationX
	 * @param blockLocationY
	 * @param blockStyle
	 * @param blockOrientation
	 */
	
	public TetrisBlock(int blockLocationX,int blockLocationY,int blockStyle,int blockOrientation){
		m_blockDirection = BLOCK_INIT_DIRECTION;
		m_blockCoordinateX = blockLocationX;
		m_blockCoordinateY = blockLocationY;
		m_blockStyle = blockStyle;
		m_blockGrid = new int[N + 1][N + 1];
		int blockShape ;
		blockShape = m_blockShape[m_blockStyle - 1][m_blockDirection];
		m_blockBottomY = new int[5];
		m_blockLeftX = new int[5];
		m_blockRightX = new int[5];
		isToEnd = false;
		tetrisBlockForm(m_blockDirection, blockShape);
	}
	
	/**
	 * 
	 */
	public void generateNewBlock(int blockStyle, int blockOrientation)
	{
		m_blockDirection = BLOCK_INIT_DIRECTION;
		m_blockCoordinateX = 100;
		m_blockCoordinateY = 0;
		m_blockStyle = blockStyle;

		for(int j = 0; j < 4; j ++)
		{
			m_blockBottomY[j] = -1;
			m_blockLeftX[j] = -1;
			m_blockRightX[j] = -1;
		}
		int blockShape ;
		blockShape = m_blockShape[m_blockStyle - 1][m_blockDirection];
		tetrisBlockForm(m_blockDirection, blockShape);
	}
	/**
	 * change the block's bottom's y coordinate and left x and right x coordinate
	 * and also the boundary of the block
	 */
	public void changeBlockScope()
	{
		for(int j = 0; j < 4; j ++)
		{
			m_blockBottomY[j] = -1;
			m_blockLeftX[j] = -1;
			m_blockRightX[j] = -1;
		}
		for(int j = 0;j < 4;j ++)
			for(int k = 0;k < 4;k ++)
				if(m_blockGrid[j][k] == 1)	
					m_blockBottomY[k] = m_blockCoordinateY + j * 20 + 20;
		for(int j = 0; j < 4; j ++)
			for(int k = 0; k < 4;k ++)
				if(m_blockGrid[j][k] == 1)
				{
					m_blockLeftX[j] = m_blockCoordinateX + k * 20;
					break;
				}
		for(int j = 0; j < 4; j ++)
			for(int k = 3; k >= 0; k --)
				if(m_blockGrid[j][k] == 1)
				{
					m_blockRightX[j] = m_blockCoordinateX + k * 20 + 20;
					break;
				}
		
	}
	/**
	 * according the the direction and blockShape to form the block
	 * @param direction
	 * @param blockShape
	 */
	public void tetrisBlockForm(int direction, int blockShape)
	{
		String hexBlockShape = Integer.toBinaryString(blockShape);
		int len = hexBlockShape.length();
		if(len < 16)
			for(int j = 0;j < 16 - len; j ++)
				hexBlockShape = '0' + hexBlockShape;
		for(int j = 0;j < 16;j ++)
		{
			m_blockGrid[j / 4][j % 4] = hexBlockShape.charAt(j) - '0';
			if(hexBlockShape.charAt(j) - '0' == 1)
				m_blockBottomY[j % 4] = (j / 4 + 1) * 20 + m_blockCoordinateY;
		}
	}
	/**
	 * transformation of the block from current state to another state by orientation
	 * @param style
	 */
	public void tetrisBlockShapeTransformation(int [][]  frameBlockGrid)
	{
		//m_blockDirection = (m_blockDirection + 1) % 4;
		//int blockShape = m_blockShape[m_blockStyle - 1][m_blockDirection];
		//tetrisBlockForm(m_blockDirection, blockShape);
		int blockDirection;
		if((blockDirection = isTransformationLegal(frameBlockGrid)) != -1)
		{
			System.out.println("erroe " + blockDirection);
			m_blockDirection = blockDirection;
			int blockShape = m_blockShape[m_blockStyle - 1][m_blockDirection];
			tetrisBlockForm(m_blockDirection, blockShape);
		}
	}
	private int isTransformationLegal(int [][] frameBlockGrid) {
		// TODO Auto-generated method stub
		int blockDirection = (m_blockDirection + 1) % 4;
		int blockShape = m_blockShape[m_blockStyle - 1][blockDirection];
		int [][]blockGrid = new int[4][4];
		//tetrisBlockForm(m_blockDirection, blockShape);
		String hexBlockShape = Integer.toBinaryString(blockShape);
		int len = hexBlockShape.length();
		if(len < 16)
			for(int j = 0;j < 16 - len; j ++)
				hexBlockShape = '0' + hexBlockShape;
		for(int j = 0;j < 16;j ++)
			blockGrid[j / 4][j % 4] = hexBlockShape.charAt(j) - '0';
		

		System.out.println("cor: " + m_blockCoordinateX + " " + m_blockCoordinateY);
		for(int j = 0; j < 4;j ++)
			for(int k = 0; k < 4;k ++)
			{
				if(blockGrid[j][k] == 1)
				{
					System.out.println("j,k:" + j + " "+ k );
					int rightX = m_blockCoordinateX + 20 * k + 20;
					int bottomY = m_blockCoordinateY + 20 * j + 20;
					int leftX = m_blockCoordinateX + 20 * k;
					System.out.println("three boundary: " + leftX + " " + rightX + " " + bottomY);
					if(! (leftX >= 0 && rightX <= DRAWPANEL_WEIGHT && bottomY <= DRAWPANEL_HEIGHT))
					{
						blockDirection = -1;
						return blockDirection;
					}
					int corX = m_blockCoordinateX + 20 * k;
					int corY = m_blockCoordinateY + 20 * j;
					if(corX >= 0 && corX <= DRAWPANEL_WEIGHT - 20 && corY >= 0 && corY <= DRAWPANEL_HEIGHT - 20)
					{
						if(frameBlockGrid[corY / 20][corX / 20] == 1)
						{
							blockDirection = -1;
							return blockDirection;
						}
					}
				}
			}
		return blockDirection;
	}
}
