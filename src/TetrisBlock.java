/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
//package tetris;

/**
 *
 * @author kymo
 */
public class TetrisBlock {
	private final int N = 4;
	private final int LEFT_BLOCK_FOLD = 1;
	private final int RIGHT_BLOCK_FOLD = 2;
	private final int BLOCK_LINE = 3;
	private final int BLOCK_DIAMOND = 5;
	private final int LEFT_VERTICAL_BENDING = 6;
	private final int RIGHT_VERTICAL_BENDING = 7;
	private int m_blockStyle;
	private int m_blockWeight;
	private int m_blockHeight;
	private int m_blockGrid[][];
	private int m_blockLocationX;
	private int m_blockLocationY;
	private int m_blockShape[][] = {
	       {0x04e0,0x0464,0x00e4,0x04c4},//four state for  
	       {0x4620,0x6c00,0x4620,0x6c00},
			{0x0f00,0x4444,0x0f00,0x4444},//four shape for BLOCK_LINE
	       {0x2640,0xc600,0x2640,0xc600}, 
	       {0x6220,0x1700,0x2230,0x0740}, 
	       {0x6440,0x0e20,0x44c0,0x8e00}, 
	       {0x0660,0x0660,0x0660,0x0660} 
	};
	
	/**
	 * 
	 * init the block's occupied grids and the start location
	 * @param blockWeight
	 * @param blockHeight
	 * @param blockLocationX
	 * @param blockLocationY
	 * @param blockStyle
	 * @param blockOrientation
	 */
	public TetrisBlock(int blockWeight,int blockHeight,int blockLocationX,int blockLocationY,int blockStyle,int blockOrientation){
		m_blockWeight = blockWeight;
		m_blockHeight = blockHeight;
		m_blockLocationX = blockLocationX;
		m_blockLocationY = blockLocationY;
		m_blockStyle = blockStyle;
		m_blockGrid = new int[N + 1][N + 1];
	}
	
	/**
	 * according to the block's style and it's orientation to fill the grid 
	 */
	public void tetrisInitBlock()
	{
		switch(m_blockStyle)
		{
		case LEFT_BLOCK_FOLD:
			
			break;
		case RIGHT_BLOCK_FOLD:
			break;
		case VERTICAL_BLOCK_LINE:
			break;
		case HORIZONTAL_BLOCK_LINE:
			break;
		case BLOCK_DIAMOND:
			for(int i = 1; i <= m_blockWeight; i ++)
				for(int j = 1; j <= m_blockHeight; j ++)
					m_blockGrid[i][j] = 1;
			break;
		case LEFT_VERTICAL_BENDING:
			break;
		case RIGHT_VERTICAL_BENDING:
			break;
		}
		tetrisBlockShapeTransformation(m_blockStyle,m_blockOrientation);
	}
	
	/**
	 * transformation of the block from current state to another state by orientation
	 * @param style
	 * @param orientation
	 */
	private void tetrisBlockShapeTransformation(int style, int orientation)
	{
		// TODO Auto-generated method stub
		
		for(int i = 1;i <= m_blockWeight;i ++)
		{
			for(int j = 1; j < m_blockHeight; j ++)
			{
				
			}
		}
	}

}
