package com.lar5;

import java.awt.*;

/**
 * CubeView draws the cube on the screen. And the buttons and whateverneeds to be drawn.
 */
public class CubeView {

    // NOTE: statics are tricky since they're shared by EVERY APPLET ON A PAGE. Only true constant read-only data can be static. Or possibly page wide settings.
    public final static double sideVec[]={0,0,1, 0,0,-1, 0,-1,0, 1,0,0, 0,1,0, -1,0,0}; // Normal (perpendicular) vectors to each side
    final static double corners[] ={-1,-1,-1, +1,-1,-1, +1,+1,-1, -1,+1,-1,
                                    -1,-1,+1, +1,-1,+1, +1,+1,+1, -1,+1,+1}; // Vertex co-ordinates (3d coordinate for each corner?)

    // "Blocks" explanation.
    // the blocks arrays holds information on which "sub cubies" are part of a 'whole or partial cube' (WOPC). The three
    // different cases are 3x3x3, 2x3x3 and 1x3x3. For twistingSide = N, this WOPC consists of rows blocks[N*4+2] -> blocks[N*4+3],
    // and each row has sticker blocks[N*4] -> blocks[N*4+1]
    double topCorners[]= new double[24],botCorners[] =  new double[24]; // Vertex co-ordinate storage for sub-cubes during twist
    final static int mainBlocks[]={0,3,0,3, 0,3,0,3, 0,3,0,3, 0,3,0,3, 0,3,0,3, 0,3,0,3};
    int topBlocks[],botBlocks[];

     // viewpoint coordinates. eye is the point of view. eX is a point at a right angle to it that defines what's
     // up/down in the picture, and eY is at a right angle to both the others, completing the coordinate system
    double  eye[] = new double[3], eX[] = new double[3], eY[] = new double[3];
    double Teye[] = new double[3],TeX[] = new double[3],TeY[] = new double[3];

    // 'Lefthanded' point of view:
//    private final double Leye[]= { 0.8162, -0.4126, -0.4044 }; // old applet perspective
//    private final double LeX[]=  { 0.3564, -0.1914,  0.9145 };

    private final double Leye[] = { 0.7970338043186325, -0.42045174114844175, -0.43354059572156745 }; // Initial observer co-ordinate axes (view)
    private final double LeX[]  = { 0.38265280721076306, -0.20380551662501864, 0.9011326986226457 }; // (sideways)

    // 'Righthanded' point of view: TODO: Should mirror lefthanded view
    private final double Reye[] = {0.4024, -0.4024, -0.8223}; // Initial observer co-ordinate axes (view)
    private final double ReX[]  = {0.9153, 0.1622, 0.3685};	// (sideways)

    // centered point of view:
    private final double Ceye[] = {0.5771, -0.5705, -0.5843};
    private final double CeX[]  = {0.5138, -0.3025, 0.8028};

    double cornerCoordX[] = new double[8], cornerCoordY[] = new double[8]; // Projected corner co-ordinates (on screen). Produced by projectCorners().
    protected boolean sideVisible[] = new boolean[6]; // Which sides are visible and hidden

    Image offImage;
    Graphics offGraphics; // shortcut to offImage.getGraphics()
    Font font10, font7, font18;

    ColorModel colorModel;

    boolean animating = false; // Is there a move animation in progress? Not sure if user manipulation counts.
    int twistSide = -1; // For animation in paint(), Raw side that is twisting
    int cdepth = 1;     // For animation in paint(), Number of layers to animate
    double phi; // Angle of moving layer

    // for the EditableCube
    protected static final int NO_STICKER = -7;
    protected int selectedStickerID = NO_STICKER;

    boolean showAlgorithm;
    boolean peeking;   // the 'look around the corner' thing
    double peekfactor; // how many cubie lengths out the peeked stickers hover
    boolean fullpeek;  // peeking to the max
    boolean leftHandedView;
    int imageWidth, imageCenter;
    double cubeSizeFactor; // how big to draw the cube. Set as big as possible while fitting in the area

    boolean redrawTextarea = false;
    String textAreaText = "";

    Caesar jc;
    boolean debug;

    // Arrow stuff...
    // arrow polygon mathematical coordinates. Measure unit is "sticker width".
    final static double m05  = -0.5,  _12 = 1.2, _18 = 1.8;
    final static double m05m = 1 + (-1*m05),  _12m = 1 + (-1*_12), _18m = 1+ (-1*_18);
    final static double _03  = 0.3,  _04 = 0.43,  _05 = 0.5, _06 = 0.57, _07 = 0.7;
    final static double[] arrowCooY = {_04, _06, _06, _07, _05, _03, _04};
    final static double[] arrowCooX = {m05, m05, _12, _12, _18, _12, _12};
    final static double[] arrowCooXm = {m05m, m05m, _12m, _12m, _18m, _12m, _12m };

    int arrowX[] = new int[7], arrowY[] = new int[7]; // arrow polygon screen coordinates
    boolean arrowsEnabled = false; // Whether arrow function is enabled at all
    boolean showArrow = false;     // Whether to ahow an arrow right now
    int[] arrowStickers = new int[4];    // Stickers to draw arrows on, if visible.
    int[] arrowCorrections = new int[4]; // Turn arrow this many quarter turns to make it look right (clockwise)
    boolean arrowForward;                // clockwise turn?
    Color arrowBorderColW = new Color(0, 0, 0, 110);
    static Color arrowColor = new Color(255, 255, 255, 160); // transparent white.
    static Color arrowBorderCol;
    private static int[] arr1 = {1, 5, 7, 3};  // Arrow related data. Sticker offset within a side
    private static int[] arr2 = {0, 3, 2 ,1};  // Arrow related data. Direction adjustment for drawing


    public CubeView(boolean closepeek, boolean fullpeek, boolean useShading, boolean showAlgorithm, boolean arrowsEnabled, Color bgColor, Color txtColor, Color txtBgColor, String sideColors, boolean leftHandedView, boolean debug, Caesar julius) {
        this.jc = julius;
        colorModel = new ColorModel(useShading, sideColors, jc.isEditable);

        colorModel.setColors(bgColor, txtColor, txtBgColor);

        this.peeking  = closepeek;
        this.fullpeek = fullpeek;
        this.leftHandedView = leftHandedView;
        this.showAlgorithm = showAlgorithm;
        this.debug = debug;
        this.arrowsEnabled = arrowsEnabled;

        imageWidth = jc.getBounds().width;
        imageCenter = imageWidth/2;
        if (jc.getBounds().height >= imageWidth + 35) { // 15 for text, 20 for buttons
            setText(""); // Paint text area, if there is one
        }

        if (fullpeek) {
            cubeSizeFactor = 0.176 * imageWidth;
            peekfactor = 1.50;
            peeking = true;
        } else if (peeking) {
            cubeSizeFactor = 0.276 * imageWidth;
            peekfactor = 0.25;
        } else {
            cubeSizeFactor = 0.288 * imageWidth;
            peekfactor = 0;
        }

        offImage = jc.createImage(jc.getBounds().width, jc.getBounds().height);       // This is where we draw the picture, in memory, before displaying it.
        offGraphics = offImage.getGraphics();
        Font font = offGraphics.getFont();
        font7  = new Font(font.getName(), font.getStyle(), 7);  // For tiny debug text
        font10 = new Font(font.getName(), font.getStyle(), 10);	// For the "17/41" text
        font18 = new Font(font.getName(), font.getStyle(), 18);	// For BT number in corner
        offGraphics.setFont(font10);	// For the 17/41 text

        topBlocks = new int[24];
        botBlocks = new int[24];

        resetCamera();
    }

    /**
     * Draws the entire picture.
     * @param graphics
     * @param state   Backtrack/Solve/Regular needs different things on screen
     */
    public void paint(Graphics graphics, int state)
    {
        synchronized (offImage) { // Gotta paint the whole picture without the things we depict changing
            offGraphics.setColor(colorModel.bgcolor);
            offGraphics.fillRect(0, 0, imageWidth, imageWidth);

            // Draw buttons
            jc.getButtonRow().drawButtons(offGraphics);

            // Draw state specific stuff
            switch (state) {
                case Caesar.REGULAR_MODE:
                    if (!jc.getMoveSeq().isEmpty())
                        writeInCorner(4, String.valueOf(jc.getMoveSeq().curRealMove - 1) + "/" + String.valueOf(jc.getMoveSeq().numRealMoves));
                    if (jc.getMoveSeq().userHasMoved) {
                        String umov = "" + jc.getPlayer().userMovesCursor;
                        if (jc.getPlayer().userMovesCursor != jc.getPlayer().userMoves.size())
                            umov += "/" + jc.getPlayer().userMoves.size();
                        writeInCorner(3, umov);
                    }
                    break;
                case Caesar.SOLVE_MODE:
                    offGraphics.setFont(font18);
                    writeInCorner(3, jc.getPlayer().getTimerSeconds());
                    int moves = jc.getPlayer().userMovesCursor;
                    if (moves > 0)
                        writeInCorner(4, "" + moves);
                    offGraphics.setFont(font10);
                    break;
                case Caesar.BACKTRACK_MODE :
                    offGraphics.setFont(font18);
                    writeInCorner(3, Integer.toString(jc.btMoves));
                    offGraphics.setFont(font10);
                    writeInCorner(4, jc.getPlayer().userMovesCursor + "/" + jc.btMoves + ":  " + jc.btSolves + " of " + jc.btTries);
                    break;
            }

            jc.drawEditableHelp(offGraphics);

            // Draw text area
            if (redrawTextarea) {
                offGraphics.setColor(colorModel.txtbgcolor);
                offGraphics.fillRect(0, imageWidth, imageWidth, 15); // Assumes 15 pixel high text field for now

                offGraphics.setColor(Color.lightGray); // divider line
                offGraphics.fillRect(0, imageWidth, imageWidth, 1);

                offGraphics.setColor(colorModel.txtcolor);
                offGraphics.drawString(textAreaText, 3, imageWidth+12);
                redrawTextarea = false;
            }

            // Draw cube itself
            if (animating) {
                VecMath.copy(eye, 0, Teye); // In twisted state? Compute top observer
                VecMath.copy(eX, 0, TeX);
                double cosPhi = Math.cos(phi);
                double sinPhi = Math.sin(phi);
                if (twistSide == 1 || twistSide == 2 || twistSide == 5)
                    sinPhi = -sinPhi;

                int n = (CubeState.isLR[twistSide] ? 1 : 0);
                int m = (CubeState.isFB[twistSide] ? 1 : 2);

                Teye[n] = cosPhi*eye[n] - sinPhi*eye[m];
                TeX[n]  = cosPhi*eX[n]  - sinPhi*eX[m];
                Teye[m] = sinPhi*eye[n] + cosPhi*eye[m];
                TeX[m]  = sinPhi*eX[n]  + cosPhi*eX[m];
                VecMath.vecProd(Teye, TeX, TeY);

                if (cdepth == 3)
                    drawCube(Teye, TeX, TeY, corners, mainBlocks); // When turning whole cube only need to draw one cube / Matt July 2nd 2002
                else {
                    if (VecMath.scalProd(eye, 0, sideVec, twistSide*3) < 0) { // Top facing away? Draw it first
                        drawCube(Teye, TeX, TeY, topCorners, topBlocks);
                        drawCube( eye,  eX,  eY, botCorners, botBlocks);
                    } else {
                        drawCube( eye,  eX,  eY, botCorners, botBlocks);
                        drawCube(Teye, TeX, TeY, topCorners, topBlocks);
                    }
                }
            } else
                drawCube(eye, eX, eY, corners, mainBlocks); // Draw cube
        }

        graphics.drawImage(offImage, 0, 0, jc);
    }

    // Draw cube or sub-cube, with all the individual stickers etc
    private void drawCube(double beye[], double beX[], double beY[],
                            double bcorners[], int bblocks[])
    {
        int rectX[] = new int[4];
        int rectY[] = new int[4];

        projectCorners(beX, beY, beye, bcorners);
        colorModel.prepColors(beye, beX, beY, sideVisible);

        if (debug) offGraphics.setFont(font7);


        if (peeking) { // Draw the peeked stickers first, so they get overwritten by the regular cube.
            for (int peekSide = 0; peekSide < 6; peekSide++) {
                if (!sideVisible[peekSide])
                    drawStickers(peekSide, bblocks, true);
            }
        }

        for (int side = 0; side < 6; side++) {
            if (sideVisible[side]) // Side faces towards us? Draw it.
            {
                for (int j = 0; j < 4; j++) {		// Find corner co-ordinates for this side
                    rectX[j] = (int) cornerCoordX[CubeState.sides[side*4 + j]];
                    rectY[j] = (int) cornerCoordY[CubeState.sides[side*4 + j]];
                }
                offGraphics.setColor((jc.notInverse ? Color.black : Color.darkGray));
                offGraphics.fillPolygon(rectX, rectY, 4); // First draw black background of side

                drawStickers(side, bblocks, false);
            }
        }
        if (debug) offGraphics.setFont(font10);
    }

    /**
     * Draw all stickers on one side. And arrows, side letters etc.
     */
    private void drawStickers(int side, int bblocks[], boolean peekside) {
        double sx, sy, sdxh, sdyh, sdxv, sdyv;
        int sideW,sideH; // pixel measurments
        boolean drawArrow = false;
        int rectX[] = new int[4], rectY[] = new int[4];

        sideW = bblocks[side*4 + 1] - bblocks[side*4];     // Number of layers in block in direction "W"
        sideH = bblocks[side*4 + 3] - bblocks[side*4 + 2]; // Number of layers in block in direction "H"
        if (sideW > 0) {
            sx   = cornerCoordX[CubeState.sides[side*4]]; // screen x coordinate for corner 0, the corner by the lowest sticker number
            sy   = cornerCoordY[CubeState.sides[side*4]]; // screen y coordinate for corner 0
            sdxh = (cornerCoordX[CubeState.sides[side*4 + 1]] - sx)/sideW; // x distance between sticker corners in one direction
            sdyh = (cornerCoordY[CubeState.sides[side*4 + 1]] - sy)/sideW; // y distance between sticker corners in one direction
            sdxv = (cornerCoordX[CubeState.sides[side*4 + 3]] - sx)/sideH; // x distance between sticker corners in other direction
            sdyv = (cornerCoordY[CubeState.sides[side*4 + 3]] - sy)/sideH; // y distance between sticker corners in other direction

            if (peekside) { // draw in the 'floating' position
                double middleStickerFactor = 1.5 - bblocks[side*4];
                double middleRowFactor     = 1.5 - bblocks[side*4 + 2];

                sx  += (sx + middleStickerFactor*sdxh + middleRowFactor*sdxv - imageCenter) * peekfactor;
                sy  += (sy + middleStickerFactor*sdyh + middleRowFactor*sdyv - imageCenter) * peekfactor;
            }

            int row = bblocks[side*4 + 2];
            for (int n = 0; n < sideH; n++) {
                int sticker = bblocks[side*4];
                double _01 = 0.1;
                double _09 = 0.9;
                for (int o = 0; o < sideW; o++) {
                    int stickerID = side*9 + row*3 + sticker;
                    if (stickerID < 0 || stickerID > 53) { // intermittent bug @@@
                        System.out.println("=== " + side + " | " + row + " | " + sticker + " | " + stickerID + " | " + sideH + " | " + sideW + " | " + bblocks[side*4] + " | " + bblocks); //@@@
                    }
                    Color stickerColor = colorModel.getStickerColor(jc.cube.stickerColors[stickerID], side);
                    if (stickerColor != null) {
                        // calculate coordinates for the 4 corners of this sticker
                        rectX[0] = (int) (sx + (o + _01)*sdxh + (n + _01)*sdxv);
                        rectX[1] = (int) (sx + (o + _09)*sdxh + (n + _01)*sdxv);
                        rectX[2] = (int) (sx + (o + _09)*sdxh + (n + _09)*sdxv);
                        rectX[3] = (int) (sx + (o + _01)*sdxh + (n + _09)*sdxv);

                        rectY[0] = (int) (sy + (o + _01)*sdyh + (n + _01)*sdyv);
                        rectY[1] = (int) (sy + (o + _09)*sdyh + (n + _01)*sdyv);
                        rectY[2] = (int) (sy + (o + _09)*sdyh + (n + _09)*sdyv);
                        rectY[3] = (int) (sy + (o + _01)*sdyh + (n + _09)*sdyv);

                        offGraphics.setColor(stickerColor);
                        offGraphics.fillPolygon(rectX, rectY, 4);

                        if (peekside && colorModel.closeToBg(stickerColor)) { // draw an outline so you can see white peeked stickers on white
                            offGraphics.setColor(Color.gray);
                            offGraphics.drawPolygon(rectX, rectY, 4);
                        }

                        if (debug) { // write sticker id on every sticker. Somewhat useful, and looks cool!
                            offGraphics.setColor(Color.black);
                            offGraphics.drawString("" + stickerID,  (rectX[0] + rectX[2])/2 - 4, (rectY[0] + rectY[2])/2 + 4); // looks good with 7 point font on 125 wide cube
                            if (stickerID == selectedStickerID) { // for the editable cube
                                offGraphics.drawLine(rectX[0], rectY[0], rectX[2], rectY[2]);
                                offGraphics.drawLine(rectX[1], rectY[1], rectX[3], rectY[3]);
                            }
                        } else
                            if ((stickerID % 9 == 4)) { // Center sticker
                                if (showAlgorithm) {    // Write side name on center sticker
                                    offGraphics.setColor(Color.black);
                                    offGraphics.drawString("" + CubeState.SIDE_LETTERS.charAt(jc.cube.logicalSideAt(side)),  (rectX[0] + rectX[2])/2 - 4, (rectY[0] + rectY[2])/2 + 4);
                                }
                                if (jc.state == Caesar.SOLVE_MODE  && !peekside && CubeState.isLR[side]) {
                                    offGraphics.setColor(Color.gray);
                                    offGraphics.drawString(jc.getPlayer().getTimerSeconds(), (rectX[0] + rectX[2])/2 - 3, (rectY[0] + rectY[2])/2 + 5);
                                    offGraphics.setColor(Color.black);
                                    offGraphics.drawString(jc.getPlayer().getTimerSeconds(), (rectX[0] + rectX[2])/2 - 4, (rectY[0] + rectY[2])/2 + 4);
                                }
                            }


                        if (showArrow && !peekside) {
                            for (int h=0; h<4; h++) {
                                if (stickerID == arrowStickers[h]) {
                                    boolean rotate = (arrowCorrections[h] == 1 || arrowCorrections[h] == 3);
                                    boolean mirror = (arrowCorrections[h] == 1 || arrowCorrections[h] == 2) ^ (!arrowForward);

                                    double[] cx = (mirror ? arrowCooXm : arrowCooX);
                                    double[] c1 = (rotate ? arrowCooY : cx);
                                    double[] c2 = (rotate ? cx : arrowCooY);
                                    for (int c=0; c<7; c++) {
                                        arrowX[c] = (int) (sx + (o + c1[c])*sdxh + (n + c2[c])*sdxv);
                                        arrowY[c] = (int) (sy + (o + c1[c])*sdyh + (n + c2[c])*sdyv);
                                    }
                                    drawArrow = true;
                                }
                            }
                        }
                    }
                    sticker++;
                }
                row++;
            }
            if (drawArrow) {
                offGraphics.setColor(arrowColor);
                offGraphics.fillPolygon(arrowX, arrowY, 7);
                offGraphics.setColor(arrowBorderCol);
                offGraphics.drawPolygon(arrowX, arrowY, 7);
            }
        }
    }


    public void animateTwist(int rawSide, int slices, boolean forward, int num, double spintime)
    {
        double limphi = num * (Math.PI/2);	            // Target for phi (default 1.57 = pi/2, 90 deg)
        double signlim  = (forward ? +1 : -1);		    // sign of limphi
        double turnTime = (num == 1 ? spintime : spintime*1.5);	// milliseconds to be used for one move

        animating = true;  // Global data for paint()
        cdepth = slices;
        twistSide = rawSide;

        if (slices < 3)   // If rotating whole cube there is no need to cut it up /Matt July 2nd 2002
            splitCube(slices, rawSide);

//        long opSum = 0; //@@@ for measuring drawing speed and stuff TODO: Set sleep/wait time so we draw no more than 25 frames/sec = 40 ms/frame
//        int frames = 0; //@@@
        long startMilli = System.currentTimeMillis(); // real timing of drawing
        double factor = (limphi/turnTime)*signlim;	  // complicated mathematical factor
        for (phi = 0; phi*signlim < limphi; phi = (System.currentTimeMillis() - startMilli)*factor) {
//            long opStart = System.currentTimeMillis(); //@@@
            jc.repaint();
//            opSum += System.currentTimeMillis() - opStart; //@@@
//            frames++; //@@@
            try {
              Thread.sleep(5); // Check for interruptions, so UI is responsive
            }
            catch (InterruptedException e) {
//                e.printStackTrace(); // TODO: Why is this interupted constantly while mixing??
              jc.getMoveSeq().pause(); // Stop any subsequent moves
              break; // break animation loop but finish what we're doing
            }
        }
        phi = 0;
//        System.out.println(" ---- " + frames + " at " + opSum/frames + " ms each"); //@@@

        animating = false;
    }


    /**
     * Produce large (2x3x3) and small (1x3x3) sub-cube for animation purposes
     *
     * @param sliceDepth
     * @param twistingSide
     */
    private void splitCube(int sliceDepth, int twistingSide)
    {
        double temp[]  = new double[3];
        double temp2[] =  new double[3];

        boolean check;
        for (int i = 0; i < 24; i++) { // Copy main coordinate data
            topCorners[i] = corners[i];
            botCorners[i] = corners[i];
        }

        VecMath.copy(sideVec, 3*twistingSide, temp); // temp = coordinate for center of twistingSide (I think...)
        VecMath.copy(temp, 0, temp2);                // temp2 = temp
        VecMath.scalMult(temp, 1.3333);              // Uh, OK...
        VecMath.scalMult(temp2, 0.6667);             // Uh, OK...

        for (int corner = 0; corner < 8; corner++) {
            check = false;
            for (int j = 0; j < 4; j++)
                if (corner == CubeState.sides[twistingSide*4 + j])
                    check = true;  // This corner is located in the animating side

            // set all bot/topCorners
            if (sliceDepth == 1) { // If only turning 1 Slice
                if (check)
                    VecMath.sub(temp2, botCorners, corner*3); // botCorners[corner*3 + <0/1/2> = corners[...] - temp2[0/1/2]
                else
                    VecMath.add(temp, topCorners, corner*3);  // topCorners[corner*3 + <0/1/2> = corners[...] + temp[0/1/2]
            } else { // If turning 2 Slices
                if (check)
                    VecMath.sub(temp, botCorners, corner*3);
                else
                    VecMath.add(temp2, topCorners, corner*3);
            }
        }
        // The sub-cubes need information about which stickers belong to them.
        for (int i = 0; i < 24; i++) { // Fix the sub-cube blockings. First copy data from main
            topBlocks[i] = mainBlocks[i]; // Large sub-cube data
            botBlocks[i] = mainBlocks[i]; // Small sub-cube data - or is it the spinning one rather than the small one, now that there is 2 slice movement?
        }
        for (int i = 0; i < 24; i += 4) {
            if (i/4 == twistingSide) {
                botBlocks[i + 1] = botBlocks[i + 3] = 0; // Large sub-cube is blank on top
            } else {

                switch (CubeState.sideIndex(i/4, twistingSide)) // Twisted side adjacent to...
                {
                    case 0:
                        topBlocks[i + 3] = botBlocks[i + 2] = sliceDepth;
                        break; // Up side?
                    case 1:
                        topBlocks[i]     = botBlocks[i + 1] = 3-sliceDepth;
                        break; // Right side?
                    case 2:
                        topBlocks[i + 2] = botBlocks[i + 3] = 3-sliceDepth;
                        break; // Down side?
                    case 3:
                        topBlocks[i + 1] = botBlocks[i]     = sliceDepth;
                        break; // Left side?
                    case -1: // None
                        topBlocks[i + 1] = topBlocks[i + 3] = 0; // Small sub-cube is blank on bottom
                        break;
                }
            }
        }
    }
    void resetCamera() {
        double[] initViewEye = (leftHandedView ? Leye : Reye);
        double[] initViewEx  = (leftHandedView ? LeX  : ReX);
        if (fullpeek) { // centered view to maximize view
            initViewEye = Ceye;
            initViewEx  = CeX;
        }

        for (int i = 0; i < 3; i++) {
            eye[i]= initViewEye[i];
            eX[i] = initViewEx[i] ;
        }
        VecMath.vecProd(eye, eX, eY); // Fix y axis of observer co-ordinate system
        VecMath.normalize(eY);
    }

    /**
     * Moves the 'camera' point of view
     * @param dx mouse movement pixels
     * @param dy mouse movement pixels
     */
    public void moveCamera(int dx, int dy) {
        VecMath.scalMult(eX, ((double) dx)*0.016); // eX *= dx*0.016
        VecMath.add(eX, eye, 0);                   // eye += eX
        VecMath.vecProd(eY, eye, eX);              // eX = eY*eye
        VecMath.normalize(eX);
        VecMath.normalize(eye);

        VecMath.scalMult(eY, ((double) dy)*0.016); // eY *= dy*0.016
        VecMath.add(eY, eye, 0);                   // eye += eY
        VecMath.vecProd(eye, eX, eY);              // eY = eX*eye
        VecMath.normalize(eY);
        VecMath.normalize(eye);
    }

    public void setText(String textToShow) {
        redrawTextarea = true;
        textAreaText = textToShow;
    }


    /**
     * Needs to be called from paint() to produce visible results.
     *
     * @param corner 1 = UL, 2 = UR, 3 = LL, 4 = LR
     * @param cornerText
     */
    void writeInCorner(int corner, String cornerText) {
        offGraphics.setColor(colorModel.txtcolor);

        int xx = (corner == 2 || corner == 4 ? imageWidth - offGraphics.getFontMetrics().stringWidth(cornerText) : 2);
        int yy = (corner == 3 || corner == 4 ? imageWidth-2 : 2 + offGraphics.getFontMetrics().getHeight());

        offGraphics.drawString(cornerText, xx, yy);
    }

    /**
     * Updates all texts and buttons etc to current status after a cube turn or other change of state.
     * Does not call repaint().
     */
    public void refresh() {
        if (showAlgorithm) {
            if (jc.getMoveSeq().userHasMoved)
                jc.getPlayer().showUserMoves();
            else
                setText(jc.getMoveSeq().toString(true));
        }
        updateArrows();
        jc.getButtonRow().triggerRedraw();
    }

    public void updateArrows() {
        if (!arrowsEnabled)
            return;

        showArrow = (!jc.getMoveSeq().atEnd() && !jc.getMoveSeq().userHasMoved);
        if (showArrow) {
            SingleMove nextMove = jc.getMoveSeq().getCurrentMove();
            int arrowSide = nextMove.rawSide();
            arrowBorderCol = arrowBorderColW;
            if (nextMove.getSlices() == 2) {
                arrowSide = CubeState.oppositeSide[arrowSide];
                arrowBorderCol = Color.black; // Small visual cue for inversed turning
            }
            arrowForward = nextMove.forward();

            boolean center = (nextMove.getSlices() == 3);

            for (int adjSide = 0; adjSide < 4; adjSide++) {
                int adjSideID = CubeState.nextSide[arrowSide*4 + adjSide];

                int si = CubeState.sideIndex(adjSideID, arrowSide);
                arrowStickers[adjSide] = adjSideID*9 + (center ? 4 : arr1[si]);
                arrowCorrections[adjSide] = arr2[si];
            }
            if (nextMove.qTurns() == 1) { // show just one arrow
                boolean useSide[] = (CubeState.isUD[arrowSide] ? CubeState.isFB : CubeState.isUD); // Show arrows on U and D if possible, FB otherwise
                for (int t=0; t<4; t++) {
                    int side = arrowStickers[t] / 9;
                    if (!useSide[side])
                        arrowStickers[t] = -1;
                }
            }
        }
    }

    /**
     * if (x, y) is on a displayed cube side, it is returned. If not, -1.
     * @param x  screen x coordinate
     * @param y  screen x coordinate
     * @return   raw side number
     */
    public int whatSide(int x, int y)
    {
        int rectX[] = new int[4], rectY[] = new int[4];  // screen  & Y coordinates

        projectCorners(eX, eY, eye, corners);

        for (int side = 0; side < 6; side++) { // Check all sides
            if (sideVisible[side]) {	// If it's visible, check if it was clicked
                for (int j = 0; j < 4; j++) {						// Find corner co-ordinates
                    rectX[j] = (int) cornerCoordX[CubeState.sides[side*4 + j]];
                    rectY[j] = (int) cornerCoordY[CubeState.sides[side*4 + j]];
                }
                if (new Polygon(rectX, rectY, 4).contains(x, y)) { // Was this side clicked?
                    return side;
                }
            }
        }
        return -1;
    }

    /**
     * Calculates the 3D corner coordinated to the 2D screen surface.
     * Computes cornerCoordX[], cornerCoordY[] & sideVisible[]
     *
     * This is where the screen size of the cube is determined
     */
    protected void projectCorners(double vX[], double vY[], double vye[], double cornlings[]) {
        for (int i = 0; i < 8; i++) {	// Project 3D co-ordinates into 2D screen ones
            cornerCoordX[i] = (imageCenter + cubeSizeFactor*VecMath.scalProd(cornlings, i*3, vX, 0));
            cornerCoordY[i] = (imageCenter - cubeSizeFactor*VecMath.scalProd(cornlings, i*3, vY, 0));
        }

        for (int side = 0; side < sideVisible.length; side++)
            sideVisible[side] = (VecMath.scalProd(vye, 0, sideVec, 3*side) > 0.001);
    }
}
