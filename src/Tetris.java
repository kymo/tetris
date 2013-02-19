/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
//package tetris;

import java.awt.EventQueue;

/**
 *
 * @author kymo
 */
public class Tetris {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        TetrisUI tetris = new TetrisUI();
        tetris.initUI();
        tetris.gameStart();
    }
}
