package com.lar5;

import java.awt.event.MouseEvent;
import java.awt.*;

/**
 * A ButtonRow object handles the button row at the bottom. It draws them, handles user inout and
 * calls other objects to perform the button actions.
 *
 * This would be ideal to build as a base class with subclasses for each button row type, and even
 * for every button, but since every added class seems to add 1k to the jar file, it's done this way instead.
 */
public class ButtonRow {

    Caesar jc;                  // the applet object
    boolean activeButtons[];	// Which buttons are on and off (greyed out)
    int pressedButton = 0;		// Which button, if any, is pressed
    private boolean redrawButtons = true;// The buttons need to be redrawn.
    private int brox, broy;     // Button Row Origin X/Y
    private int buttonWidth;
    private int gap = 2;        // space between buttons

    static final private int PLAY = 7;
    static final private int BT = 8;
    static final private int NONE = 9;
    static final private int USER = 11;
    static final private int SOLVE = 12;
    private int mode;

    private Color lightColor, mediumColor, darkColor;

    // Different button looks
    static final int bRESET     = 0;
    static final int bREVERSE   = 1; // reverse play
    static final int bSTEP_BACK = 2;
    static final int bPAUSE     = 3;
    static final int bSTEP      = 4;
    static final int bPLAY      = 5;
    static final int bTOEND     = 6;
    static final int bPLUS      = 7;
    static final int bMINUS     = 8;
    static final int bMIX       = 9;

    private int[] buttons;

    /**
     * Common to all constructors
     */
    private ButtonRow(Caesar applet, int buttonRowWidth, int type) {
        jc = applet;
        brox = (jc.getBounds().width - buttonRowWidth)/2;
        broy = jc.getBounds().height - 20;
        mode = type;
    }

    /**
     * Something has changed so the buttons need to be redrawn next time we're drawing
     */
    public void triggerRedraw() {
        redrawButtons = true;
    }

    /**
     * Let the button row handle its own mouse events
     */
    public void mouseEvent(MouseEvent event)
    {
        int whichButton = whatButton(event.getX(), event.getY());

        switch (event.getID()) {
            case MouseEvent.MOUSE_PRESSED:
                // Hidden feature: Print current position string on console when button area is control-clicked
                if (event.isControlDown()) {
                    jc.cube.printPosition();
                    System.out.println(jc.getMoveSeq().toString(false));
                }

                pressButton(whichButton);
                jc.repaint();
                break;
            case MouseEvent.MOUSE_DRAGGED:
                if (pressedButton > 0) {	// If you leave the button, it becomes unpressed
                    if (whichButton != pressedButton)
                        pressButton(0);	// Cancel press
                }
                break;
            case MouseEvent.MOUSE_RELEASED:
                if (pressedButton > 0) {
                    if (whichButton != pressedButton)
                        pressButton(0);	// Cancel press
                    else
                        handleButtonCommand(pressedButton);
                }
                break;
            default:
                throw new IllegalArgumentException();
        }
    }


    public void executePressedButton() {
        handleButtonCommand(pressedButton);
    }

    public void handleButtonCommand(int buttonNumber) {
        if (buttonNumber > 0 && buttonNumber < activeButtons.length && activeButtons[buttonNumber]) {

            switch (mode) {
                case PLAY:
                    handleRegButtonCmd(buttonNumber);
                    break;
                case BT:
                    handleBtButtonCmd(buttonNumber);
                    break;
                case USER:
                    handleUserButtonCmd(buttonNumber);
                    break;
                case SOLVE:
                    handleSolveButtonCmd(buttonNumber);
                    break;
                // ignore NULL case
            }
        }
        pressButton(0);     // Unpress
        jc.repaint();	// Redraw the buttons, at least
    }

    /**
     * Perform a command by name, regaradless of which button it's on.
     */
    public void pressButtonByCmd(int commandID) {

        for (int b=1; b < buttons.length; b++) {
            if (buttons[b] == commandID)
                pressButton(b);
        }
    }

    private int leftEdge(int buttonNumber) {
        return brox + 1 + (buttonNumber - 1) * (buttonWidth + gap);
    }

    private int middle(int buttonNumber) {
        return leftEdge(buttonNumber) + buttonWidth/2;
    }

    private int rightEdge(int buttonNumber) {
        return leftEdge(buttonNumber) + buttonWidth;
    }

    /**
     * returns which button, if any, the point (xx, yy) is in.
     * @param xx  relative x coordinate within the button area
     * @param yy  relative y coordinate within the button area
     * @return
     */
    private int whatButton(int xx, int yy) {
        if (yy >= broy + 2 && yy <= broy + 15) {
            for (int i=1; i <= buttons.length; i++)
                if (xx >= leftEdge(i) && xx <=  rightEdge(i)) return i;
        }
        return 0;
    }

    // =================== Drawing code =========================

    public void drawButtons(Graphics grap)
    {
        boolean[] buttonsToShow = null;
        switch (mode) {
            case PLAY:
                buttonsToShow = regularButtonsToShow();
                break;
            case BT:
                buttonsToShow = backtrackButtonsToShow();
                break;
            case SOLVE:
                buttonsToShow = solveButtonsToShow();
                break;
            case USER:
                buttonsToShow = userButtonsToShow();
                break;
            case NONE:
                buttonsToShow = nullButtonsToShow();
                break;
        }

        if (activeButtons != buttonsToShow || redrawButtons) { // Need to redraw?
            activeButtons = buttonsToShow;
            grap.setColor(jc.view.colorModel.bgcolor);
            grap.fillRect(0, broy, jc.getBounds().width, 20);  // button are is 20 pixels high

            for (int b=1; b < buttons.length; b++) {

                drawButton(b, grap); // sets color for drawing below!

                switch (buttons[b]) {
                    case bRESET:
                        drawTriangleL(middle(b) + 1, grap);
                        grap.drawLine(middle(b) + 4, broy + 5, middle(b) + 4, broy + 11);
                        grap.drawLine(middle(b) + 3, broy + 6, middle(b) + 3, broy + 10);
                        grap.drawLine(middle(b) + 2, broy + 7, middle(b) + 2, broy + 9);
                        break;
                    case bREVERSE:
                        drawTriangleL(middle(b) + 3, grap);
                        break;
                    case bSTEP_BACK:
                        drawTriangleL(middle(b) + 3, grap);
                        grap.fillRect(middle(b) - 4, broy + 5, 2, 7);
                        break;
                    case bPAUSE:
                        grap.fillRect(middle(b) - 3, broy + 5, 2, 7);
                        grap.fillRect(middle(b) + 1, broy + 5, 2, 7);
                        break;
                    case bSTEP:
                        drawTriangleR(middle(b) - 4, grap);
                        grap.fillRect(middle(b) + 2, broy + 5, 2, 7);
                        break;
                    case bPLAY:
                        drawTriangleR(middle(b) -3, grap);
                        break;
                    case bTOEND:
                        grap.drawLine(middle(b) - 4, broy + 5, middle(b) - 4, broy + 11);
                        grap.drawLine(middle(b) - 3, broy + 6, middle(b) - 3, broy + 10);
                        grap.drawLine(middle(b) - 2, broy + 7, middle(b) - 2, broy + 9);
                        drawTriangleR(middle(b) - 1, grap);
                        break;
                    case bPLUS:
                        grap.fillRect(middle(b) -4, broy + 8, 8, 2); // -
                        grap.fillRect(middle(b) -1, broy + 5, 2, 8); // |
                        break;
                    case bMINUS:
                        grap.fillRect(middle(b) -4, broy + 8, 8, 2); // -
                        break;
                    case bMIX:
                        int mixWidth = grap.getFontMetrics().stringWidth("Mix");
                        grap.drawString("Mix", middle(b) - mixWidth/2, broy+12);
                        break;
                }
            }

            redrawButtons = false;
        }
    }

    /**
     * Handle the graphical effects of a button being pressed. Not until it's released will any action be executed.
     * @param buttonNumber
     */
    public void pressButton(int buttonNumber)
    {
        if (buttonNumber < activeButtons.length && activeButtons[buttonNumber]) {
            pressedButton = buttonNumber;
            redrawButtons = true;
        }
    }

    /**
     * Draws button number buttonNumber. Sets color depending on active status
     */
    private void drawButton(int buttonNumber, Graphics grap)
    {
        grap.setColor(mediumColor);
        if (activeButtons[buttonNumber]) {
            grap.fill3DRect(leftEdge(buttonNumber), broy+2, buttonWidth, 13, buttonNumber != pressedButton);
            grap.setColor(darkColor); // sets color for button image
        } else {
            grap.fillRect(leftEdge(buttonNumber), broy+2, buttonWidth, 13);
            grap.setColor(lightColor); // sets color for button image
        }
    }

    /**
     * Draws a triangle pointing right
     */
    private void drawTriangleR(int xx, Graphics grap)
    {
        grap.fillRect(xx,     broy + 5, 1, 7);
        grap.fillRect(xx + 1, broy + 6, 2, 5);
        grap.fillRect(xx + 3, broy + 7, 2, 3);
        grap.fillRect(xx + 5, broy + 8, 2, 1);
    }

    /**
     * Draws a triangle pointing left
     */
    private void drawTriangleL(int xx, Graphics grap)
    {
        grap.fillRect(xx,     broy + 5, 1, 7);
        grap.fillRect(xx - 2, broy + 6, 2, 5);
        grap.fillRect(xx - 4, broy + 7, 2, 3);
        grap.fillRect(xx - 6, broy + 8, 2, 1);
    }



    // ###################################################################################################
    // ###################################################################################################
    // ###################################################################################################
    // Ideally this should be a base class with sub classes for each Button Row type. That adds quite a bit of
    // size to the class files, so we'll simulate it  instead. Below is all the stuff that would be in subclasses
    // ###################################################################################################

    // the zero'th value is just a place holder, but it *is* used.
    private static final int[] regularButtons          = {-99, bRESET, bREVERSE, bSTEP_BACK, bPAUSE, bSTEP, bPLAY, bTOEND};

    static final boolean startActiveButtonsREG[]       = {true,   false, false, false, false, true,  true,  true};	// At start (no rewinding permitted)
    static final boolean middleActiveButtonsREG[]      = {true,   true,  true,  true,  false, true,  true,  true};	// In between
    static final boolean endActiveButtonsREG[]         = {true,   true,  true,  true,  false, false, false, false}; // At end (no forward)
    static final boolean playingActiveButtonsREG[]     = {true,   false, false, false, true,  false, false, false}; // Playing
//    static final boolean userTurningActiveButtonsREG[] = {true,   true,  false, true,  false, false, false, false}; // User has moved, we're off the script


    /**
     * Regular version
     */
    static public ButtonRow createRegularButtons(Caesar applet, int buttonRowWidth) {
        ButtonRow newRow = new ButtonRow(applet, buttonRowWidth, PLAY);
        newRow.initAsREG(buttonRowWidth);
        return newRow;
    }

    public void initAsREG(int buttonRowWidth) {
        buttons = regularButtons;
        buttonWidth = (buttonRowWidth/(buttons.length - 1)) - gap;

        lightColor  = Color.lightGray;
        mediumColor = Color.gray;
        darkColor   = Color.black;
    }

    private boolean[] regularButtonsToShow() {
        if (jc.getMoveSeq().playing)
            return playingActiveButtonsREG;

        if (jc.getMoveSeq().userHasMoved) { // Switch to user move buttons
//            return userTurningActiveButtonsREG;
            jc.setUserButtonRow(true);
        }

        if (jc.getMoveSeq().atStart())
            return startActiveButtonsREG;

        if (jc.getMoveSeq().atEnd())
            return endActiveButtonsREG;

        return middleActiveButtonsREG;
    }

    private void handleRegButtonCmd(int pressedButton) {
        switch (pressedButton) {
            case 1:
                jc.getPlayer().queueAction(AnimationThread.FIRST);
                break;
            case 2:
                jc.getPlayer().queueAction(AnimationThread.REW_PLAY);
                break;
            case 3:
                jc.getPlayer().queueAction(AnimationThread.REW_STEP);
                break;
            case 4:
                jc.getMoveSeq().pause(); // let the movesequence play out. no interrupt.
                break;
            case 5:
                jc.getPlayer().queueAction(AnimationThread.STEP);
                break;
            case 6:
                jc.getPlayer().queueAction(AnimationThread.PLAY);
                break;
            case 7:
                jc.getPlayer().queueAction(AnimationThread.LAST);
                break;
        }
    }

    // ###################################################################################################

    private static final int[] userButtons       = {-99, bRESET, bREVERSE, bSTEP_BACK, bPAUSE, bSTEP, bPLAY, bTOEND};

    private static final boolean startActiveButtonsUS[]  = {true,   true, false, false, false, true,  false, false}; // At start (no rewinding permitted)
    private static final boolean middleActiveButtonsUS[] = {true,   true, false, true,  false, true,  false, false}; // In between
    private static final boolean endActiveButtonsUS[]    = {true,   true, false, true,  false, false, false, false}; // At end (no forward)


    /**
     * User-is-turning version 
     */
    static public ButtonRow createUsermoveButtons(Caesar applet, int buttonRowWidth) {
        ButtonRow newRow = new ButtonRow(applet, buttonRowWidth, USER);
        newRow.initAsUS(buttonRowWidth);
        return newRow;
    }

    public void initAsUS(int buttonRowWidth) {
        buttons = userButtons;
        buttonWidth = (buttonRowWidth/(buttons.length - 1)) - gap;

        lightColor  = Color.lightGray;
        mediumColor = new Color(208, 144, 0);
        darkColor   = Color.black;
    }

    private boolean[] userButtonsToShow() {
        if (!jc.getMoveSeq().userHasMoved) {
            jc.setUserButtonRow(false);
        }

        if (jc.getPlayer().userMovesCursor == 0)
            return startActiveButtonsUS;

        if (jc.getPlayer().userMovesCursor == jc.getPlayer().userMoves.size())
            return endActiveButtonsUS;

        return middleActiveButtonsUS;
    }

    private void handleUserButtonCmd(int pressedButton) {
        switch (pressedButton) {
            case 1:
                jc.getPlayer().rewindUserMoves();
                jc.setUserButtonRow(false);
                jc.view.refresh();
                break;
            case 3:
                jc.getPlayer().undoUserMove();
                break;
            case 5:
                jc.getPlayer().redoUserMove();
                break;
            case 2: // these are only for show
            case 4:
            case 6:
            case 7:
                break;
        }
    }



    // ###################################################################################################

    private static final int[] backtrackButtons        = {-99,   bMINUS, bPLUS, bPLAY, bSTEP, bRESET, bMIX};

    static final boolean afterMixActiveButtonsBT[]     = {true,   true,  true,  true,  true,  false, true};	// After a mix
    static final boolean userTurningActiveButtonsBT[]  = {true,   true,  true,  false, false, true,  true};    // User has moved
    static final boolean whileSteppingActiveButtonsBT[]= {true,   true,  true,  true,  true,  true,  true};    // User is stepping through solution

    /**
     * Backtrack version
     */
    static public ButtonRow createBacktrackButtons(Caesar applet, int buttonRowWidth) {
        ButtonRow newRow = new ButtonRow(applet, buttonRowWidth, BT);
        newRow.initAsBT(buttonRowWidth);
        return newRow;
    }

    public void initAsBT(int buttonRowWidth) {
        buttons = backtrackButtons;
        buttonWidth = (buttonRowWidth/(buttons.length - 1)) - gap;

        lightColor  = Color.lightGray; //new Color(100, 230, 100);
        mediumColor = new Color(60, 170, 60);
        darkColor   = Color.black;
    }

    private boolean[] backtrackButtonsToShow() {
        if (jc.getPlayer().userMoves.size() > 0 ||  // If the user has moved, he must rewind before play is possible
                jc.getMoveSeq().atStart()) // If play has completed, same thing
            return userTurningActiveButtonsBT;

        if (!jc.getMoveSeq().atEnd()) // If seq is in middle, we must be stepping
            return whileSteppingActiveButtonsBT;

        return afterMixActiveButtonsBT;
    }

    private void handleBtButtonCmd(int pressedButton) {
        switch (pressedButton) {
            case 1:
                jc.lessMovesBT();
                break;
            case 2:
                jc.moreMovesBT();
                break;
            case 3: // play
                jc.btAttemptFinished(false);
                jc.getPlayer().clearUserMoves();
                jc.getPlayer().queueAction(AnimationThread.REW_PLAY);
                break;
            case 4: // step
                jc.btAttemptFinished(false);
                jc.getPlayer().clearUserMoves();
                jc.getPlayer().queueAction(AnimationThread.REW_STEP);
                break;
            case 5:// rewind
                jc.restoreMix();
                jc.btAttemptFinished(false);
                break;
            case 6:
                jc.mixBT();
                break;
        }
    }


    // ###################################################################################################

    private static final int[] solveButtons = {-99,   bSTEP_BACK, bMIX, bSTEP};

    static final boolean activeFFButtonsSV[]  = {true,   false, true, false};
    static final boolean activeTFButtonsSV[]  = {true,    true, true, false};
    static final boolean activeFTButtonsSV[]  = {true,   false, true,  true};
    static final boolean activeTTButtonsSV[]  = {true,    true, true,  true};

    /**
     * Solve version
     */
    static public ButtonRow createSolveButtons(Caesar applet, int buttonRowWidth) {
        ButtonRow newRow = new ButtonRow(applet, buttonRowWidth, SOLVE);
        newRow.initAsSV(buttonRowWidth);
        return newRow;
    }

    public void initAsSV(int buttonRowWidth) {
        buttons = solveButtons;
        buttonWidth = (buttonRowWidth/(buttons.length - 1)) - gap;

        lightColor  = Color.lightGray;
        mediumColor = new Color(80, 170, 170);
        darkColor   = Color.black;
    }

    private boolean[] solveButtonsToShow() {
        boolean back = (jc.getPlayer().userMovesCursor != 0);
        boolean forw = (jc.getPlayer().userMovesCursor != jc.getPlayer().userMoves.size());

        if ( back &&  forw) return activeTTButtonsSV;
        if ( back && !forw) return activeTFButtonsSV;
        if (!back &&  forw) return activeFTButtonsSV;
        if (!back && !forw) return activeFFButtonsSV;

        return null;
    }

    private void handleSolveButtonCmd(int pressedButton) {
        switch (pressedButton) {
            case 1:
                jc.getPlayer().undoUserMove();
                break;
            case 2:
                jc.mixForSolving();
                break;
            case 3:
                jc.getPlayer().redoUserMove();
                break;
        }
    }


    // ###################################################################################################
    private static final int[] nullButtons   = {-99};

    static final boolean activeButtonsNull[] = {true};

    /**
     * Placeholder version
     */
    static public ButtonRow createNullButtons(Caesar applet, int buttonRowWidth) {
        ButtonRow newRow = new ButtonRow(applet, buttonRowWidth, NONE);
        newRow.initAsNull();
        return newRow;
    }

    public void initAsNull() {
        buttons = nullButtons;
        buttonWidth = 10;

        lightColor = mediumColor = darkColor = Color.lightGray;
    }

    private boolean[] nullButtonsToShow() {
        return activeButtonsNull;
    }

}
