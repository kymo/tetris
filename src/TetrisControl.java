/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
//package tetris;

/**
 *
 * @author kymo
 */
public class TetrisControl {
    
	private final int GAME_START = 1;
	private final int GAME_HALT = -1;
	private final int GAME_OVER = 0;
	private int m_gameStatus;
	public void gameStart(){
		
	}
	/**
	 * set game status
	 * @param gameStatus
	 */
	public void setGameStatus(int gameStatus){
		m_gameStatus = gameStatus;
	}
	/**
	 * get game status
	 * @return
	 */
	public int getGameStatus(){
		return m_gameStatus;
	}
	public void cleanFormedBlock(){
		
	}
    
    public void monitorBlockState()
    {
    	
    }
    
}
