package com.lar5;

import com.lar5.Caesar;
import com.lar5.SingleMove;

/**
 * This class holds a sequence of SingleMoves.
 * The moves are numbered from 1 to numMoves.
 * When the final move has been made curMove==numMoves+1
 */
class MoveSequence
{
    private Caesar jc;               // the applet object
    protected SingleMove[] moveList; // The list of scripted moves
    private double spintime = 0.0;   // 0 -> default time

    public int numMoves,numRealMoves;	// The total number of moves
    public int curMove,curRealMove;	// Number of the current move
    public volatile boolean playing;   // If we're animating. For ordering pausing. volatile since it can be changed by other threads any time.
    public boolean userHasMoved = false;// The user has changed the cube, and our scripted moves no longer apply


    public MoveSequence(String moveParameter, Caesar applet)
    {
        jc = applet;

        if (moveParameter == null)
            moveParameter = ""; // handle lack of move parameter gracefully

        moveList = new SingleMove[moveParameter.length()+1]; // too many moves, so don't rely on moveList.length
        numRealMoves = 0;
        int i = 0;
        while (i < moveParameter.length()) {
            char sideChar = moveParameter.charAt(i);
            i++;
            char quadChar = '1';
            if (i < moveParameter.length() && moveParameter.charAt(i) != ' ') // The '1' is implied so 'R' means 'R1'
                quadChar = moveParameter.charAt(i);
            i++;
            SingleMove mv = moveList[numMoves] = new SingleMove(sideChar, quadChar, jc);

            while (i < moveParameter.length() && moveParameter.charAt(i) == ' ') // allow spaces for readability
                i++;

            numMoves++;
            if (mv.isReal())
                numRealMoves++;
        }

        curMove = curRealMove = 1;
        playing = false;
    }

    public void setSpintime(double spintime) {
        this.spintime = spintime;
    }

    public String toString() {
        return toString(false);
    }

    public String toString(boolean showCursor)
    {
        StringBuffer buff = new StringBuffer(numMoves * 3);
        for (int i = 0; i < numMoves; i++) {
            if (showCursor && i > 0 && i == curMove - 1)
                buff.append("¥ ");
            SingleMove move = moveList[i];
            if (move.isReal())
                buff.append(move.toString(true, true)).append(" ");
        }
        return buff.toString();
    }

    public boolean isEmpty() {
        return (moveList[0] == null);
    }

    public void reverseSequence() {
        // 1. reverse order
        for (int i = 0; i < (numMoves / 2); i++) {
            SingleMove s = moveList[i]; // swap
            moveList[i] = moveList[numMoves - 1 - i];
            moveList[numMoves - 1 - i] = s;
        }
        // 2. reverse each move
        for (int i = 0; i < numMoves; i++) {
            SingleMove s = moveList[i];
            s.reverse();
        }
        // 3. reset
        curMove = curRealMove = 1;
    }


    public void userTwisted() {
        if (!userHasMoved) {
            userHasMoved = true;	// Now the algorithm won't work, since the position is out of sync
            jc.view.refresh();
            jc.repaint();
        }
    }

    public void reset()
    {
        curMove = 1;
        curRealMove = 1;
        userUndid();
    }

    /**
     * Forget about any user moves and go back to normal state
     */
    public void userUndid() {
        userHasMoved = false;
        jc.view.updateArrows();
    }

    public boolean stillPlaying()
    {
        return playing;
    }

    public void goFirst()
    {
        jc.cube.startPosition();
        reset();
        jc.view.refresh();
    }

    public void rewind()
    {
        playing = true;
        while (stillPlaying())
            rewStep();
    }

    SingleMove getMoveAt(int pos) {
        return moveList[pos-1];
    }

    public SingleMove getCurrentMove()
    {
        return getMoveAt(curMove);
    }

    public boolean atEnd()
    {
        return !(curMove <= numMoves);
    }

    public boolean atStart()
    {
        return curMove == 1;
    }


    public void rewStep()	// Undo the "current" step
    {
        if (!atStart()) {
            SingleMove mv = getMoveAt(curMove - 1);	   // *previous* move
            jc.cube.spin(mv.rawSide(), mv.getSlices(), !mv.forward(), mv.qTurns(), true, spintime);
            curMove--;
            if (mv.isReal())
                curRealMove--;
            jc.view.refresh();
        } else {
            playing = false;
        }
    }

    public void pause()
    {
        playing = false; // That'll stop it!
    }

    public void doStep()	// Do the "current" step
    {
        if (!atEnd()) {
            SingleMove mv = getCurrentMove();
            jc.cube.spin(mv.rawSide(), mv.getSlices(), mv.forward(), mv.qTurns(), true, spintime);
            curMove++;
            if (mv.isReal())
                curRealMove++;
            jc.view.refresh();
        } else {
            playing = false;	// only way to end playing
        }
    }

    public void play()
    {
        playing = true;
        while (stillPlaying()) // check for pause button
            doStep();
    }

    public void goLast()
    {
        while (!atEnd()) {
            SingleMove mv = getCurrentMove();
            jc.cube.spin(mv.rawSide(), mv.getSlices(), mv.forward(), mv.qTurns(), false, spintime);
            curMove++;
            if (mv.isReal())
                curRealMove++;
        }
        jc.repaint();
    }

    public void doFull()
    {
        reset();
        goLast();
    }

    /**
     * Generates a good random seqeuence of moves in a MoveSequence object.
     * @param number  number of moves requested
     * @param app
     * @return
     */
    static public MoveSequence randomSequence(int number, Caesar app)
    {
        StringBuffer mixString = new StringBuffer(number*2);
        int btMixMoves[] = MoveSequence.randomMoves(number);
        for (int l = 0; l < btMixMoves.length; l++)
            mixString.append(CubeState.SIDE_LETTERS.charAt(btMixMoves[l])).append(1 + (int) (3 * Math.random()));
        return new MoveSequence(mixString.toString(), app);
    }

    /**
     * Generate n random moves, as an array with n elements of 0..5.
     * Does not generate "silly" sequences like (1) moving the same side twice in a row, or
     * (2) things like "F B F". Only generates the sides, since that's the hard part.
     *
     * @param n  number of moves requested
     * @return
     */
    static public int[] randomMoves(int n) {
        int retval[] = new int[n];
        int j;

        for (int i=0; i<n; i++) {
            if (i==0) {
                // first move, any side is fine
                j =  (int) (6 * Math.random()); // 0 -> 5
            } else {
                // This could be done simpler, but I want a true random distribution
                if (i >= 2 && retval[i-1] == CubeState.oppositeSide[retval[i-2]]) {
                    // Avoid two sides
                    j =  (int) (4 * Math.random()); // 0 -> 3
                    if (j >= Math.min(retval[i-1], retval[i-2])) j++;
                    if (j >= Math.max(retval[i-1], retval[i-2])) j++;
                } else {
                    j =  (int) (5 * Math.random()); // 0 -> 4

                    // avoid latest side
                    if (j >= retval[i-1])
                        j++;
                }
            }
            retval[i] = j;
        }
        return retval;
    }

}