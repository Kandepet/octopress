// Version History
// ---------------
// Rubik's Cube 3D simulator
// Karl Hšrnell, March 11 1996
// Last modified April 17
//
// Scriptability and other changes made by Lars Petrus lars@netgate.net
// The design objective behind much code that looks ugly and primitive is
// to keep the size of the applet down. It does quite a lot for a 15k applet.
// Last modified Sept 2 2000.
//
// Further changes (2 and 3 slice moves, and shaded colors) made by Matthew Smith.
// Last Modified July 11th 2002
//
// May/June 2003
// Went wild and reorganized the entire code, and added several features.
// Feb-April 2004
// Finished the work. And added even more stuff. / Lars - cube@lar5.com
//
// You're free to use this code for non-commercial purposes
// as long as due credit is given to Karl Hšrnell, Lars Petrus and Matthew Smith

package com.lar5;

import com.lar5.MoveSequence;
import com.lar5.AnimationThread;

import java.awt.*;
import java.awt.event.*;
import java.lang.Math;
import java.applet.Applet;

/**
 * This class fulfills the responsibilities of being an applet, and handles user input.
 *
 * There are many cube applets these days, most of them named "Cube" or something similar. To distinguish from those I
 * renamed this one "Caesar". For no particular reason, though he *did* cross the Rubicon.
 *
 *  I've tried to be JDK 1.1 compatible, but I haven't tested that it is.
 *
 * Main classes overview:
 *  Caesar          - Command and information center. Handles user input.
 *  CubeState       - The 'physical' cube.
 *  CubeView        - Draws the cube on screen
 *  AnimationThread - Does animations in it's own thread
 *  MoveSequence    - A sequence of SingleMoves representing an algorithm to be displayed
 *  SingleMove      - A move.
 *
 * I don't have an instruction for how to use the applet yet, what parameter does what etc.
 * I hope to have one done fairly soon. Meanwhile, most should be fairly easy to figure out. /Lars 8 April 2004
 *
 *   TODOs - more or less minor planned or wished features
 *
 *   -- Cookies for settings such as color configuration, left/righthandedness etc.
 *   -- Put moves commentary in the text field
 *   -- Some kind of demo mode, where the applet shows that it's more than just a picture by slowly spinning or making a few moves occasionally.
 *   -- Automatic turning of the cube so that the action is always (mostly) visible. This should probably be done so that EditableCube generates
 *      a move sequence. We don't want it to flip all over for every slice move, so it needs to let some stuff through
 *   -- Quiz module. Feed it 100 positions and solutions (with explaining text), and let it quiz the user by displaying positions and see if it solves it right.
 *   -- Paste/write in moves
 *
 *   -- Advanced userMoves timing. Plug into Oinkleburger.com or something like it.
 */

public class Caesar extends Applet implements MouseListener, MouseMotionListener, KeyListener
{
    // "global state"
    public int state = REGULAR_MODE; // By default
    public final static int REGULAR_MODE = 5;
    public final static int SOLVE_MODE = 55;
    public final static int BACKTRACK_MODE = 555;

    int lastX,lastY; // Mouse position

    boolean isEditable = false;
    boolean solving;

    MoveSequence mainSequence;	    // The move sequence
    private AnimationThread player;	// the play thread object, if any
    public CubeState cube;          // all other objects use this reference
    public CubeView view;           // all other objects use this reference

    private ButtonRow activeButtonRow;
    private ButtonRow regularButtonRow;
    private ButtonRow backtrackButtonRow;
    private ButtonRow userButtonRow;
    private ButtonRow solveButtonRow;
    private ButtonRow nullButtonRow;

    // backtracking
    int btMoves;    // Number of backtrack moves
    int btSolves;   // Number of successful solves this session
    int btTries;    // Number of tries this session
    boolean btAttemptInProgress = false;

    // Experimental features
    boolean notInverse = true;  // The "solving backwards" thing. Wish there was a word for it...
    boolean inputMoves = false; // Input moves as text from user
    StringBuffer inputString = new StringBuffer();


    // Applet parameters

    // _P = Parameter
    static final String pMOVE        = "move";
    static final String pPOS         = "pos";
    static final String pSIDECOLORS  = "sidecolors";
    static final String pSPINTIME    = "spintime";
    static final String pINITMOVE    = "initmove";
    static final String pINITREVMOVE = "initrevmove";
    static final String pINITREVMOVEMOVE = "initrevmovemove";

    static final String pBGCOLOR     = "bgcolor";
    static final String pTXTCOLOR    = "txtcolor";
    static final String pTXTBGCOLOR  = "txtbgcolor";

    static final String pBACKTRACK   = "backtrack";

    // _MP = Misc. Parameter
    static final String mpARROWS     = "arrows";
    static final String mpCLOSEPEEK  = "closepeek";
    static final String mpDEBUG      = "debug";
    static final String mpFULLPEEK   = "fullpeek";
    static final String mpRHVIEW     = "rhview";
    static final String mpSHOWALG    = "showalg";
    static final String mpSHADING    = "shading";
    static final String mpSOLVING    = "solving";


    public void init()
    {
        boolean closepeek     = getMiscParam(mpCLOSEPEEK);
        boolean fullpeek      = getMiscParam(mpFULLPEEK);
        boolean useShading    = getMiscParam(mpSHADING) && !isEditable;
        boolean showAlgorithm = getMiscParam(mpSHOWALG);
        boolean arrowsEnabled = getMiscParam(mpARROWS);
        boolean debug         = getMiscParam(mpDEBUG) || isEditable;
        boolean leftHandedView =  !getMiscParam(mpRHVIEW); // "rhview" = "right handed view" (left handed view is default)

        Color bgColor    = findColor(getParameter(pBGCOLOR),  Color.white);
        Color txtColor   = findColor(getParameter(pTXTCOLOR), Color.black);
        Color txtBgColor = findColor(getParameter(pTXTBGCOLOR), bgColor);
        String sideColors = getParameter(pSIDECOLORS);

        String spinTimeParm = getParameter(pSPINTIME);
        double tt1q = (spinTimeParm == null ? 800 : Integer.parseInt(spinTimeParm));	 // Time to turn quarter move, in ms. 800 is default

        cube = new CubeState(getParameter(pPOS), getParameter(pINITMOVE), getParameter(pINITREVMOVE, pINITREVMOVEMOVE), tt1q, this);

        view = new CubeView(closepeek, fullpeek,  useShading, showAlgorithm, arrowsEnabled, bgColor, txtColor, txtBgColor,  sideColors, leftHandedView, debug, this);

        mainSequence = new MoveSequence(getParameter(pMOVE, pINITREVMOVEMOVE), this); // The input string representing the move

        if (mainSequence.isEmpty()) {
            nullButtonRow = ButtonRow.createNullButtons(this, 400);
            activeButtonRow = nullButtonRow;
        } else {
            if (showAlgorithm)
                view.setText(mainSequence.toString());        // todo: this works but is ugly
            regularButtonRow = ButtonRow.createRegularButtons(this, Math.min(view.imageWidth, 400));
            activeButtonRow = regularButtonRow;
        }


        if (getMiscParam(mpSOLVING)) {
            state = SOLVE_MODE;
            solveButtonRow = ButtonRow.createSolveButtons(this,  Math.min(view.imageWidth, 100 + view.imageWidth/4));
            activeButtonRow = solveButtonRow;
        }

        String backtrackParm = getParameter(pBACKTRACK);
        if (backtrackParm != null) {
            btMoves = 5;
            btSolves = 0;
            btTries = 0;
            mixBT();
            backtrackButtonRow = ButtonRow.createBacktrackButtons(this,  Math.min(view.imageWidth, 320));
            activeButtonRow = backtrackButtonRow;
        } else {
            cube.startPosition();
        }

        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);
    }

    /**
     * Get a parameter with either of the names given. name1 has precendence.
     */
    String getParameter(String name1, String name2) {
        String value = getParameter(name1);
        if (value == null)
            value = getParameter(name2);
        return value;
    }

    /**
     * Instead of having a dozen different parameters for all kinds of minor on-and-off settings I combine them into
     * the 'misc' parameter. If a certain string is found somewhere in the misc string the setting is on, otherwise off.
     * @param paramName  name of the misc setting
     * @return  true if the parameter is set
     */
    boolean getMiscParam(String paramName)
    {
        String miscParameters = getParameter("misc");
        if (miscParameters == null)
            return false;

        return (miscParameters.indexOf(paramName) >= 0);
    }

    /**
     * Switches between the regular and the orange user move button row
     * @param set
     */
    public void setUserButtonRow(boolean set)
    {
        if (set) {
            if (userButtonRow == null)
                userButtonRow = ButtonRow.createUsermoveButtons(this, Math.min(view.imageWidth, 400));
            activeButtonRow = userButtonRow;
        } else {
            mainSequence.userUndid();
            activeButtonRow = regularButtonRow;
        }

        activeButtonRow.triggerRedraw();
    }

    void mixForSolving() {
        view.setText("");
        mainSequence = MoveSequence.randomSequence(30, this);
        mainSequence.setSpintime(100);
        getPlayer().queueAction(AnimationThread.MIX_FOR_SOLVE);
    }

    public ButtonRow getButtonRow()
    {
        return activeButtonRow;
    }

    public MoveSequence getMoveSeq()
    {
        return mainSequence;
    }

    /**
     * The AnimationThread only gets created if/when first needed
     */
    protected AnimationThread getPlayer()
    {
        if (player == null || !player.isAlive()) {
            player = new AnimationThread(mainSequence, this);
            player.start();
            while (!player.isReady()) {
//                System.out.println("XXX"); //@@@
                try {
                  Thread.sleep(5); // Let other thread start in peace
                }
                catch (InterruptedException e) {}
            }
        }
        return player;
    }

    protected static Color findColor(String hexString, Color defaultColor)
    {
        if ((hexString != null) && (hexString.length() == 6))
            return new Color(Integer.parseInt(hexString, 16));
        else
            return defaultColor;
    }

    public void paint(Graphics graphics)
    {
        // todo @see http://mindprod.com/jgloss/paint.html#PAINT
        view.paint(graphics, state);
    }

    /**
     * inherited from java.awt.Container
     */
    public void update(Graphics g)
    {
        paint(g);
    }

    protected void drawEditableHelp(Graphics grap) {
        // to override
    }

// ====================================== Event handling =====================================================

    public void mouseDragged(MouseEvent event)
    {
        int x = event.getX();
        int y = event.getY();

        activeButtonRow.mouseEvent(event);

        synchronized (view.offImage) { // Can't change the basis for the paint() math while paint() is running. offImage is just a random object to use as lock. don't want to use 'this'
            view.moveCamera(lastX - x, y - lastY);

            lastX = x;
            lastY = y;
        }

        repaint();
    }

    public void mousePressed(MouseEvent event)
    {
        int x = event.getX();
        int y = event.getY();
//        int rectX[] = new int[4], rectY[] = new int[4];  // screen  & Y coordinates

        if (y < view.imageWidth) { // cube area is square

            int spinreq = 0;         // quarter turns requested by the click
            boolean forwreq = true;  // direction requested by the click

            if (event.isShiftDown())
                spinreq = 1;

            if (event.isControlDown()) {
                if (event.isShiftDown())
                    spinreq = 2;
                else {
                    spinreq = 1;
                    forwreq = false;
                }
            }

            if (spinreq != 0) {
                int clickedSide = view.whatSide(x, y);
                if (clickedSide != -1) {
                    if (event.isAltDown())
                        getPlayer().queueSpinMove(cube.oppositeSide[clickedSide], (event.isMetaDown() ? 2 : 1), !forwreq, spinreq, true, 250, true);
                    else
                        getPlayer().queueSpinMove(clickedSide, (event.isMetaDown() ? 2 : 1), forwreq, spinreq, true, 250, true);
                }
            }
        } else   // The click hit the button area
            activeButtonRow.mouseEvent(event);

        lastX = x;
        lastY = y;
    }

    public void mouseReleased(MouseEvent event)
    {
        activeButtonRow.mouseEvent(event);
    }

    public void mouseClicked(MouseEvent e) {
    }
    public void mouseEntered(MouseEvent e) {
    }
    public void mouseExited(MouseEvent e) {
    }
    public void mouseMoved(MouseEvent e) {
    }

    public void keyTyped(KeyEvent event)
    {
    }

    public void keyPressed(KeyEvent event)
    {
        int spinNumber;
        boolean clockwise = true;
        int side = 0;
        int keyCode = event.getKeyCode();
        boolean shift = event.isShiftDown();

        if (inputMoves) {
            processKeyInput(event);
            repaint();
            return;
        }

        boolean processed = false;
        switch (state) {
            case REGULAR_MODE:
                processed = processKeyREG(event);
                break;
            case SOLVE_MODE:
                processed = processKeySOLVE(event);
                break;
            case BACKTRACK_MODE:
                processed = processKeyBT(event);
                break;
        }

        if (processed)
            return;

        // --- Turning sides ---
        switch (keyCode) {
            case KeyEvent.VK_NUMPAD7:
            case KeyEvent.VK_I:
            case KeyEvent.VK_NUMPAD8:
            case KeyEvent.VK_O:
            case KeyEvent.VK_NUMPAD9:
            case KeyEvent.VK_P:
                clockwise = shift;
                spinNumber =  1;
                break;
            case KeyEvent.VK_NUMPAD4:
            case KeyEvent.VK_K:
            case KeyEvent.VK_NUMPAD5:
            case KeyEvent.VK_L:
            case KeyEvent.VK_NUMPAD6:
            case KeyEvent.VK_SEMICOLON:
                clockwise = !shift;
                spinNumber = 1;
                break;
            case KeyEvent.VK_NUMPAD1:
            case KeyEvent.VK_COMMA:
            case KeyEvent.VK_NUMPAD2:
            case KeyEvent.VK_PERIOD:
            case KeyEvent.VK_NUMPAD3:
            case KeyEvent.VK_SLASH:
                clockwise = !shift;
                spinNumber = 2;
                break;
            default:
                spinNumber = 0;
        }

        if (spinNumber > 0) {
            switch (keyCode) {
                case KeyEvent.VK_NUMPAD7:
                case KeyEvent.VK_I:
                case KeyEvent.VK_NUMPAD4:
                case KeyEvent.VK_K:
                case KeyEvent.VK_NUMPAD1:
                case KeyEvent.VK_COMMA:
                    side = (shift ? 0 : 1);
                    break;
                case KeyEvent.VK_NUMPAD8:
                case KeyEvent.VK_O:
                case KeyEvent.VK_NUMPAD5:
                case KeyEvent.VK_L:
                case KeyEvent.VK_NUMPAD2:
                case KeyEvent.VK_PERIOD:
                    side = (shift ? 4 : 2);
                    break;
                case KeyEvent.VK_NUMPAD9:
                case KeyEvent.VK_P:
                case KeyEvent.VK_NUMPAD6:
                case KeyEvent.VK_SEMICOLON:
                case KeyEvent.VK_NUMPAD3:
                case KeyEvent.VK_SLASH:
                    side = (shift ? 5 : 3);
                    break;
            }
            if (notInverse)
                getPlayer().queueSpinMove(side, (event.isAltDown() ? 3 : (event.isControlDown() ? 2 : 1)), clockwise, spinNumber, true, 300, true);
            else {
                cube.inverseColorTwist(side, (clockwise ? spinNumber : 4 - spinNumber));
                repaint();
            }

        } else {
            // --- Turning the whole cube, or misc commands ---
            switch (keyCode) {
                case KeyEvent.VK_Z:
                case KeyEvent.VK_LEFT:
                    getPlayer().queueSpinMove(2, 3, true, 1, true, 250, true);
                    break;
                case KeyEvent.VK_C:
                case KeyEvent.VK_RIGHT:
                    getPlayer().queueSpinMove(2, 3, false, 1, true, 250, true);
                    break;
                case KeyEvent.VK_S:
                case KeyEvent.VK_UP:
                    getPlayer().queueSpinMove(1, 3, false, 1, true, 250, true);
                    break;
                case KeyEvent.VK_X:
                case KeyEvent.VK_DOWN:
                    getPlayer().queueSpinMove(1, 3, true, 1, true, 250, true);
                    break;
                case KeyEvent.VK_A:
                    getPlayer().queueSpinMove(3, 3, false, 1, true, 250, true);
                    break;
                case KeyEvent.VK_D:
                    getPlayer().queueSpinMove(3, 3, true, 1, true, 250, true);
                    break;

                // --- ... misc stuff...
                case KeyEvent.VK_ENTER: // Undo/Redo
                    if (event.isShiftDown())
                        activeButtonRow.pressButtonByCmd(ButtonRow.bSTEP);
                    else
                        activeButtonRow.pressButtonByCmd(ButtonRow.bSTEP_BACK);
                    repaint();
                    break;

                case KeyEvent.VK_HOME: // restore original viewpoint
                    view.resetCamera();
                    repaint();
                    break;

                case KeyEvent.VK_END: // Enter magic reverse mode
                    notInverse = !notInverse;
                    repaint();
                    break;

                case KeyEvent.VK_MINUS:
                    System.out.println(getPlayer().userMoveText()); //@@@
                    break;

                case KeyEvent.VK_Q:
                    if (shift)
                        inputMoves = true;
                    break;

                case KeyEvent.VK_BACK_SLASH:
                    getPlayer().optimizeUserMoves();
                    view.refresh();
                    repaint();
                    break;

                case KeyEvent.VK_1: // As a general convention, 2 will press button 2, etc
                case KeyEvent.VK_2:
                case KeyEvent.VK_3:
                case KeyEvent.VK_4:
                case KeyEvent.VK_5:
                case KeyEvent.VK_6:
                case KeyEvent.VK_7:
                case KeyEvent.VK_8:
                case KeyEvent.VK_9:
                    activeButtonRow.pressButton(keyCode - KeyEvent.VK_0);
                    repaint();
                    break;

                case KeyEvent.VK_SPACE:   // Let the space key start and stop play, just like in QuickTime etc
                    if (mainSequence.playing)
                        activeButtonRow.pressButtonByCmd(ButtonRow.bPAUSE);
                    else
                        activeButtonRow.pressButtonByCmd(( shift ? ButtonRow.bREVERSE : ButtonRow.bPLAY));
                    break;
    //            default:
    //                System.out.println("Ignored key: " + event); //@@@
            }
        }
    }

    public void keyReleased(KeyEvent event) {
        activeButtonRow.executePressedButton();
    }


    boolean processKeyREG(KeyEvent event)
    {
        return false;
    }

    boolean processKeySOLVE(KeyEvent event)
    {
        switch (event.getKeyCode()) {
            case KeyEvent.VK_EQUALS:
            case KeyEvent.VK_SPACE:
                activeButtonRow.pressButtonByCmd(ButtonRow.bMIX);
                repaint();
                return true;
        }

        return false;
    }

    boolean processKeyBT(KeyEvent event)
    {
        switch (event.getKeyCode()) {
            case KeyEvent.VK_EQUALS:
            case KeyEvent.VK_SPACE:
                activeButtonRow.pressButtonByCmd(ButtonRow.bMIX);
                repaint();
                return true;

            case KeyEvent.VK_ENTER: // Same Undo/Redo as usual, but they have no buttons in BT
                if (event.isShiftDown())
                    getPlayer().redoUserMove();
                else
                    getPlayer().undoUserMove();
                repaint();
                return true;
        }
        return false;
    }

    /**
     * Still under development...
     */
    void processKeyInput(KeyEvent event)
    {
        char keyChar = event.getKeyChar();
//        System.out.println(">>>> " + keyChar + " | " + (int) keyChar); //@@@
        if ("BFRDUL01234<>|' ".indexOf(keyChar) >= 0) {
            inputString.append(keyChar);
        } else {
            if (event.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                inputString.setLength(inputString.length() - 1);
            } else {
                mainSequence = new MoveSequence(inputString.toString(), this);
                inputString.setLength(0);
                inputMoves = false;
            }
        }
        if (inputMoves)
            view.setText("[" + inputString.toString() + "]");
    }

    //===================== BT (backtracker) ===========

    public void mixBT() {
        if (btInProgress())
            btAttemptFinished(false);

        cube.solvedPosition();
        mainSequence = MoveSequence.randomSequence(btMoves, this) ;
        mainSequence.goLast();
        btAttemptInProgress = true;
        getPlayer().queueAction(AnimationThread.BT_MIX_DONE);
        view.setText("Solve in " + btMoves + " move" + (btMoves == 1 ? "" : "s"));
    }

    public void restoreMix() {
        cube.solvedPosition();
        mainSequence.reset();
        mainSequence.goLast();
        getPlayer().clearUserMoves();
        view.setText(btAttemptInProgress ? "Rewind counts as failure" : "");
        repaint();
    }

    public void lessMovesBT() {
        if (btMoves > 1)
            btMoves--;
        btNewRound();
    }

    public void moreMovesBT() {
        btMoves++;
        btNewRound();
    }

    private void btNewRound() {
        btAttemptInProgress = false;
        btSolves = 0;
        btTries = 0;
        mixBT();
        repaint();
    }

    public void btAttemptFinished(boolean succeeded) {
        if (btAttemptInProgress) {
            if (succeeded)
                btSolves++;
            btTries++;
            btAttemptInProgress = false;
        }
    }

    public boolean btInProgress() {
        return btAttemptInProgress;
    }
}
