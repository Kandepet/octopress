package com.lar5;

/**
 * An object that represents one single move in a move sequence (or possibly elsewhere)
 */
class SingleMove
{
    int logicalSide;// logical side
    int slices;
    boolean forward;// Clockwise turning?
    int num;        // Number of quarter turns (1 or 2)
    boolean raw;    // When rotating the whole cube, only what we see is important, not what we ### todo: obsolete?
    Caesar jc;


    public String toString() {
        return toString(false, true);
    }

    /**
     *
     * @param adjust  Show side compensated for offsets or not
     * @param cute   'plain' display is "R1 U2 F3", 'cute' is "R U2 F'"
     * @return
     */
    public String toString(boolean adjust, boolean cute) {
        String code = "";
        int sideToUse = (adjust ? jc.cube.mapSide(logicalSide) : logicalSide);

        switch (slices) {
            case 1:
                code = CubeState.SIDE_LETTERS.substring(sideToUse, sideToUse+1);
                break;
            case 2:
                code = "fbdlur".substring(sideToUse, sideToUse+1);
                break;
            case 3:
                code = " xzy".substring(sideToUse, sideToUse+1); // todo R> etc ?
                break;
        }
        code += numCode(forward, (num == 1), cute);

        return code;
    }

    /**
     *
     * @param forward - clockwise move
     * @param single  - one quarter turn, as opposed to two
     * @param cute    - human readable?
     * @return
     */
    public static String numCode(boolean forward, boolean single, boolean cute) {
        String code = "";

        if (forward) {
            if (single) {
                if (!cute)
                    code = "1";
            } else
                code = "2";
        } else {
            if (single) {
                code = (cute ? "'" : "3");
            } else
                code = "Z";
        }
        return code;
    }

    /**
     * The idea is that 'F' and 'f' denotes the same move, just displayed differently.
     *
     * For x, y, and z I use the conventions of http://www.necrophagous.co.uk/cubestation/cubenotation.html TODO: Or do I? What about raw=true?
     */
    public SingleMove(char code, char numberChar, Caesar _mainApp)
    {
        jc = _mainApp;

        switch (code) {
            case 'B':
            case 'f':
                logicalSide = 0;
                break;
            case 'F':
            case 'b':
            case 'z':
                logicalSide = 1;
                break;
            case 'U':
            case 'd':
            case 'y':
                logicalSide = 2;
                break;
            case 'R':
            case 'l':
            case 'x':
                logicalSide = 3;
                break;
            case 'D':
            case 'u':
                logicalSide = 4;
                break;
            case 'L':
            case 'r':
                logicalSide = 5;
                break;
            default:
                throw new IllegalArgumentException("Bad move code character: '" + code + "'");
        }

        raw = false;
        switch (code) {
            case 'B':
            case 'F':
            case 'U':
            case 'R':
            case 'D':
            case 'L':
                slices = 1;
                break;
            case 'b':
            case 'f':
            case 'u':
            case 'r':
            case 'd':
            case 'l':
                slices = 2;
                break;
            case 'x':
            case 'y':
            case 'z':
                slices = 3;
                raw = true; // When spinning the whole cube, keeping track of which logical side you're working on becomes pointless
                break;
            default:
                throw new IllegalArgumentException("Bad move code character: '" + code + "'");
        }

        switch (numberChar) {
            case '0':
            case '1':
            case '2':
            case '\"':
            case '>':
            case '|':
                forward = true;
                break;
            case '3':
            case '\'':
            case '4':
            case 'Z': // Z is a weird looking 2, gettit?
            case '<':
                forward = false;
                break;
            default:
                throw new IllegalArgumentException("Bad move number character: '" + numberChar + "'");
        }

        switch (numberChar) {
            case '1':
            case '3':
            case '\'':
                num = 1;
                break;
            case '0':
            case '2':
            case '\"':
            case '4':
            case 'Z': // Z is a weird looking 2, gettit?
                num = 2;
                break;

            case '>':
            case '<':
                num = 1;
                slices = 3;
                break;
            case '|':
                num = 2;
                slices = 3;
                break;
        }
    }

    /**
     * Changes this move into its reverse
     */
    public void reverse() {
        forward = !forward;
    }

    /**
     * A number of quarter turns that we can do math with
     * @return
     */ 
    int mathNumber()
    {
        return (forward ? num : 4 - num);
    }

    public int rawSide()
    {
        return (raw ? logicalSide : jc.cube.mapSide(logicalSide));
    }

    public int getSlices()
    {
        return slices;
    }

    public boolean isReal()
    {
        return slices != 3;
    }

    public int qTurns()
    {
        return num;
    }

    public boolean forward()
    {
        return forward;
    }


    // ################################### This could be a subclass, but to save class file space it's like this #########

    // Represents a *performance* of one move on the cube. Holds displayable information about it, and can also execute the move, with animation and all.

    boolean animate;
    double  spintime;
    long    timestamp;
    /**
     *
     * @param side  This should be the raw side number
     */
    public SingleMove(int side, int slices, boolean forward, int num, boolean animate, double spintime, Caesar _mainApp)
    {
        jc = _mainApp;

        timestamp = System.currentTimeMillis();
        this.logicalSide = jc.cube.logicalSideAt(side);
        this.slices = slices;
        this.forward = forward;
        this.num = num;
        this.animate = animate;
        this.spintime = spintime;
    }

    public void execute() {
        jc.cube.spin(jc.cube.mapSide(logicalSide), slices, forward, num, animate, spintime);
    }

    /**
     * reverses. without animation for now
     */
    public void unExecute() {
        jc.cube.spin(jc.cube.mapSide(logicalSide), slices, !forward, num, false, 0);
    }
}
