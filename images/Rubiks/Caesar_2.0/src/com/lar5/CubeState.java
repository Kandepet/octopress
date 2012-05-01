package com.lar5;

/**
 * CubeState keeps the state of the cube and has the functions to change it.
 *
 * About raw and Logical sides:
 * By tradition the six cube sides are called U, D, B, F, L and R, for Up Down, Back Front, Left and Right.
 * At start, B is side 0, F is 1, etc. When the whole cube is turned that changes. So what was originally
 * B can be any side from 0 to 5. That doesn't matter for most things, except when we need to either read or
 * write data in that form. The conversion is made by the functions mapSide(logicalSide) and logicalSideAt(rawSide).
 * So if what was at start U (or 2) side is now on side 5, the raw side for that is 5 (or L) and the logical
 * side is U.
 */
public class CubeState {
    // NOTE: statics are tricky since they're shared by EVERY APPLET ON A PAGE. Only true constant read-only data can be static. Or possibly page wide settings.
    final public static int sides[]     ={4,5,6,7, 3,2,1,0, 0,1,5,4, 1,2,6,5, 2,3,7,6, 0,4,7,3}; // Corner IDs for each side
    final public static int nextSide[]  ={2,3,4,5, 4,3,2,5, 1,3,0,5, 1,4,0,2, 1,5,0,3, 2,0,4,1}; // For each side, the 4 adjacent sides, starting with the one next to the "0,1,2" row (except for one!). Clockwise order if colDir[side] == 1, anti clockwise if -1.

    static public final String SIDE_LETTERS = "BFURDL"; // "B" is side 0, "F" side 1, etc
    static public final boolean[] isLR = {false, false, false,  true, false, true}; // is side n either L or R?
    static public final boolean[] isUD = {false, false,  true, false,  true, false};
    static public final boolean[] isFB = { true,  true, false, false, false, false};

    static private final int colDir[] = {-1, -1, 1, -1, 1, -1}; // some kind of 'parity' for the side
    static public  final int circleOrder[] = {0, 1, 2, 5, 8, 7, 6, 3}; // order of the stickers around a center
    static public  final int oppositeSide[] = {1, 0, 4, 5, 2, 3}; // Opposites are 0 <-> 1,  2 <-> 4,  3 <-> 5

    public final int stickerColors[] = new int[54]; // Hold the current color values for the 54 stickers

    Caesar jc; // jc = Julius Caesar
    String initMove;
    String initrevMove;
    String startpos;
    double tt1q;	// Time to turn quarter move. In ms.

    // ------------ side mappings ------------
    // sideMap[n] is the side where the original side n is currently
    private static final int changeF[] = {0, 1, 5, 2, 3, 4}; // Changes from an 'F' move
    private static final int changeD[] = {3, 5, 2, 1, 4, 0}; // Changes from an 'D' move
    private static final int changeR[] = {2, 4, 1, 3, 0, 5}; // Changes from an 'R' move
    private static int[][] changeTable = {changeF, changeF, changeD, changeR, changeD, changeR};
    private final int sideMap[]  = {0, 1, 2, 3, 4, 5}; // sideMap[S] gives the current side number where the original side S now is.


    public CubeState(String startpos, String initMove, String initrevMove, double tt1q, Caesar applet)
    {
        jc = applet;
        this.startpos = startpos;
        this.initMove = initMove;
        this.initrevMove = initrevMove;
        this.tt1q = tt1q;
    }

    /**
     * Performs moves on the cube, animated ot not. Should only be used by AnimationThread (if animating)
     *
     *	@param rawSide 	0-5
     *  @param slices   1-3 layers deep
     *	@param num		1, 2, 3 or 4 quarter turns.
     *	@param animate	Show animation - or not.
     *  @param spintime time in ms for a quarter turn. if == 0, the default spin time is used
     */
    public void spin(int rawSide, int slices, boolean clockwise, int num, boolean animate, double spintime)
    {
        boolean forward = clockwise ^ (colDir[rawSide] > 0);
        double time = (spintime == 0 ? tt1q : spintime);

        if (animate)
            jc.view.animateTwist(rawSide, slices, forward, num, time);

        int quads = (clockwise ? 4 - num : num);
        moveStickers(rawSide, slices, quads);

        if (animate)
            jc.repaint();	// Don't paint if we're ffwd-ing
    }

    /**
     * Moves the colors of the stickers to "implement" a full move.
     *
     * @param sideNumber   raw side number
     * @param quads        Number of quarter (90-degree) turns
     */
    protected void moveStickers(int sideNumber, int slices, int quads) // Shift colored fields
    {
        int j,k;
        int buffer[] = new int[12];
        int newStickerColors[] = new int[54]; // To build new color values when moving

        for (j = 0; j < newStickerColors.length; j++) // JDK 1.2: Use Arrays
            newStickerColors[j] = -99;

        // 1. move the stickers of the first layer
        moveStickersOneLayer(sideNumber, quads, newStickerColors);

        // 2. move the stickers on the middle slice if required / Added by Matt July 2nd 2002
        if (slices >= 2) {
            k = 0;
            for (int adjSide = 0; adjSide < 4; adjSide++) { //  the 4 adjacent sides of the (nominally) turning side
                int adjSideID = nextSide[sideNumber*4 + adjSide];

                for (j = 0; j < 3; j++) { // the three middle stickers on this side
                    switch (sideIndex(adjSideID, sideNumber)) {
                        case 0:
                            buffer[k] = adjSideID*9 + 3 + j;   // 3,4,5
                            break;
                        case 1:
                            buffer[k] = adjSideID*9 + 1 + 3*j; // 1,4,7
                            break;
                        case 2:
                            buffer[k] = adjSideID*9 + 5 - j;   // 5,4,3
                            break;
                        case 3:
                            buffer[k] = adjSideID*9 + 7 - 3*j; // 7,4,1
                            break;
                        default:
                            break;
                    }
                    k++;
                }
            }

            for (int m = 0; m < 12; m++)
                newStickerColors[buffer[ (m + quads*3) % 12 ]] = stickerColors[buffer[m]];

            // centers moved, record this change
            syncSideMap(sideNumber, quads);
            jc.view.updateArrows();
        }

        // 3. move the stickers on the third layer if required, when spinning entire cube / Added by Matt July 2nd 2002
        if (slices == 3)
            moveStickersOneLayer(oppositeSide[sideNumber], 4 - quads, newStickerColors);

        // Copy changed stickers to 'real' cube
        for (j = 0; j < newStickerColors.length; j++) {
            if (newStickerColors[j] != -99)
                stickerColors[j] = newStickerColors[j];
        }
    }

    /**
     * Moves the colors of the stickers in one single (non middle) layer.
     * @param sideNumber   raw side number
     * @param quads        Number of quarter (90-degree) turns
     */
    private void moveStickersOneLayer(int sideNumber, int quads, int newStickerColors[]) // Shift colored fields
    {
        int buffer[] = new int[12];

        // 1. rotate the 'stickers' on the rotating side itself
        int k = quads*2; // quads = number of 90-degree multiples
        for (int i = 0; i < 8; i++) {
            newStickerColors[sideNumber*9 + circleOrder[(i + k)%8]] = stickerColors[sideNumber*9 + circleOrder[i]];
        }

        // 2. move the 'stickers' on the sides of the first slice
        k = 0; // unusual loop counter from 0 to 11.
        for (int adjSide = 0; adjSide < 4; adjSide++) {
            int adjSideID = nextSide[sideNumber*4 + adjSide];

            for (int j = 0; j < 3; j++) { // the three adjacent stickers on this side
                switch (sideIndex(adjSideID, sideNumber)) {
                    case 0:
                        buffer[k] = adjSideID*9 + j; // 0,1,2
                        break;
                    case 1:
                        buffer[k] = adjSideID*9 + 2 + 3*j; // 2,5,8
                        break;
                    case 2:
                        buffer[k] = adjSideID*9 + 8 - j; // 8,7,6
                        break;
                    case 3:
                        buffer[k] = adjSideID*9 + 6 - 3*j; // 6,3,0
                        break;
                }
                k++;
            }
        }

        for (int m = 0; m < 12; m++)
            newStickerColors[buffer[ (m + quads*3)%12 ]] = stickerColors[buffer[m]];
    }

    public void startPosition() // Set (or reset) to start position
    {
        if (startpos != null) {
            for (int i = 0; i < 54; i++) {
                stickerColors[i] = startpos.charAt(i) - 97; // This is 'a'-'h' // 97 = 'a' ?
                if (stickerColors[i] < 0)
                    stickerColors[i] += 40; // This is for 'A'-'F'
            }
        } else {
            for (int i = 0; i < 54; i++)
                stickerColors[i] = i/9;
        }
        resetSideMap();

        // You can have both pos and init moves. One good reason is to move to a specific viewpoint
        if (initMove != null) {
            MoveSequence initMoveSequence = new MoveSequence(initMove, jc);
            initMoveSequence.goLast();
        } else {
            if (initrevMove != null) {
                MoveSequence initrevMoveSequence = new MoveSequence(initrevMove, jc);
                initrevMoveSequence.reverseSequence();
                initrevMoveSequence.goLast();
            }
        }

        jc.view.updateArrows();
        jc.repaint();
    }

    /**
     * Put the cube in the solved position. Doesn't paint.
     */
    public void solvedPosition()
    {
        for (int i = 0; i < 54; i++)
            stickerColors[i] = i/9;
        resetSideMap();
    }

    public boolean isSolved()
    {
        for (int s = 0; s< 54; s += 9) {
            for (int t=1; t<9; t++)
                if (stickerColors[s] != stickerColors[s+t])
                    return false;
        }
        return true;
    }

    /**
     * If otherSide is adjacent to side, returns the offset (0..3) to use for it in topBlocks[] and other arrays. -1 otherwise.
     * The "inverse" of nextSide in a sense.
     */
    static int sideIndex(int side1, int side2)
    {
        int k = -1;
        for (int j = 0; j < 4; j++)
            if (nextSide[side1*4 + j] == side2)
                k = j;

        return k;
    }

    public void printPosition()
    {
        System.out.println();
        System.out.println(positionString());
    }

    /**
     * Returns a string that can be used as a "pos" parameter.
     * @return
     */
    public String  positionString()
    {
        char[] buff = new char[54];
        for (int ii = 0; ii < 54; ii++) {
            if (stickerColors[ii] < 8)
                buff[ii] = (char) (stickerColors[ii] + 97); // [a..h]
            else
                buff[ii] = (char) (stickerColors[ii] + 57); // colored grays [A..F]
        }
        return new String(buff);
    }

    public void resetSideMap()
    {
        for (int i = 0; i < 6; i++)
            sideMap[i] = i;
    }

    public void syncSideMap(int rawSide, int quads)
    {
        int sideMapTMP[] = new int[6]; // work area to build new sideMap[]
        int xquads = quads;

        if (rawSide == 2 || rawSide == 0 || rawSide == 5)
            xquads = 4 - xquads;

        int[] change = changeTable[rawSide];

        for (int q = 0; q < xquads; q++) {
            for (int i=0; i < 6; i++) // 1. build
                sideMapTMP[logicalSideAt(i)] = sideMap[logicalSideAt(change[i])];

            for (int m = 0; m < 6; m++) // 2. copy
                sideMap[m] = sideMapTMP[m];
        }
    }

    /**
     * Returns the actual side where the logical side 'logicalSide' is now located
     * @param logicalSide
     * @return
     */
    public int mapSide(int logicalSide)
    {
        return sideMap[logicalSide];
    }

    /**
     * Returns the logical side that is now located at rawSide
     * @param rawSide
     * @return
     */
    public int logicalSideAt(int rawSide) {
        for (int l = 0; l < 6; l++) {
            if (sideMap[l] == rawSide)
                return l;
        }
        throw new IllegalStateException("" + rawSide);
    }


    // ----------------- "Inverted turning" uses changeX data... -----

    static int[][] edgeStickers = {{14, 28}, {23, 30}, {5, 34}, {32, 39}, {19, 16}, {1, 25}, {7, 43}, {10, 37}, {21, 46}, {3, 50}, {41, 52}, {12, 48}};
    static int[][] cornerStickers = {{17, 20, 27}, {2, 26, 33}, {8, 35, 42}, {11, 29, 36}, {15, 18, 45}, {0, 24, 47}, {9, 38, 51}, {6, 44, 53}};
    void inverseColorTwist(int rawSide, int quads)
    { // TODO: make this a real undoable move
        int xquads = quads;
        int logicalSide = logicalSideAt(rawSide);

        if (logicalSide == 2 || logicalSide == 0 || logicalSide == 5)
            xquads = 4 - xquads;

        int[] change = changeTable[logicalSide];

        for (int e = 0; e < edgeStickers.length; e++) {
            int[] edge = edgeStickers[e];
            if (stickerColors[edge[0]] == logicalSide || stickerColors[edge[1]] == logicalSide) {
                for (int q = 0; q < xquads; q++) {
                    stickerColors[edge[0]] = change[stickerColors[edge[0]]];
                    stickerColors[edge[1]] = change[stickerColors[edge[1]]];
                }
            }
        }

        for (int c = 0; c < cornerStickers.length; c++) {
            int[] corner = cornerStickers[c];
            if (stickerColors[corner[0]] == logicalSide || stickerColors[corner[1]] == logicalSide || stickerColors[corner[2]] == logicalSide) {
                for (int q = 0; q < xquads; q++) {
                    stickerColors[corner[0]] = change[stickerColors[corner[0]]];
                    stickerColors[corner[1]] = change[stickerColors[corner[1]]];
                    stickerColors[corner[2]] = change[stickerColors[corner[2]]];
                }
            }
        }
    }
}
