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
	private int m_blockDirection;//the block's shape
	
	private int m_blockStyle;//the block's style  
	public int m_blockGrid[][];//the filled grid of the block
	public int m_blockBottomY[];//the block's bottom Y coordinate
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
		changeBlockBottomY();
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
		tetrisBlockForm(m_blockDirection, blockShape);
		System.out.println("hello this is form Block");
	}
	public void changeBlockBottomY()
	{
		for(int j = 0;j < 4;j ++)
			for(int k = 0;k < 4;k ++)
				if(m_blockGrid[j][k] == 1)
					m_blockBottomY[k] = m_blockCoordinateY + j * 20 + 20;
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
		System.out.println(hexBlockShape);
		System.out.println("fu");
		for(int j = 0; j < 4;j ++)
			m_blockBottomY[j] = 0;
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
	public void tetrisBlockShapeTransformation()
	{
		m_blockDirection = (m_blockDirection + 1) % 4;
		int blockShape = m_blockShape[m_blockStyle - 1][m_blockDirection];
		tetrisBlockForm(m_blockDirection, blockShape);
	}
}
