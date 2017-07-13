/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
//package tetris;

/**
 *
 * @author kymo
 */
public class TetrisControl extends Thread{
    private TetrisUI tui;
    static 
    {
        System.loadLibrary("TetrisControl");
    }
    public native static int beginVR(TetrisUI tui);
    /**
     * set game status
     * @param gameStatus
     */
    TetrisControl(TetrisUI _tui)
    {
        tui = _tui;
    }
    public void run()
    {
        beginVR(tui);
    }
}
