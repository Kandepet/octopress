package com.lar5;

// Rubik's Cube 3D simulator
// Karl Hšrnell, March 11 1996
// Last modified April 17
//
// Scriptability and other changes
// made by Lars Petrus lars@netgate.net
// Last modified November 16 1997.
//
// Futher changes made by Matthew Smith
// Only added ability for applet to print
// the cubes coordinates to console so that
// it was easier to find a better point of view for the regular applet
// Last Modified July 3rd 2002
//
//
// You're free to use this code for non-commercial purposes
// as long as due credit is given to Karl Hšrnell, Lars Petrus and Matthew Smith

import com.lar5.Caesar;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;

/**
 * The purpose of this class is to generate position strings easily. You select
 * facelets by the mouse and set their colors by mouseclicks or keyboard presses. The
 * position (and view point coordinates) is printed to the console when 'P' is pressed
 */

public class EditableCube extends Caesar
{
	/**
	 *	This function provides the editability of surfaces. vafan 0/1/2 provides
	 *	different modes. Common to all is that you click on a face and it changes color.
	 */


    public void init()
    {
        isEditable = true;
        super.init();
//        bgcolor    = findColor(getParameter(pBGCOLOR),  new Color(0xbbbbdd)); // different default bg to make white more visible
    }

    public void mousePressed(MouseEvent event)
    {
        int hitSticker = getSticker(event.getX(), event.getY());

        if (hitSticker != view.NO_STICKER) {
            if (hitSticker == view.selectedStickerID) { // todo a lot more could be done with shift, alt modifiers and different buttons. But this is good enough for now.
                if ( cube.stickerColors[view.selectedStickerID] <= 8)
                    setSelectedSticker((cube.stickerColors[view.selectedStickerID] + 1) % 8);
                else
                    setSelectedSticker((cube.stickerColors[view.selectedStickerID] - 7) % 6  + 8); // grays ranges from 8 to 15
            } else {
                view.selectedStickerID = hitSticker;
            }
            repaint();

            lastX = event.getX();
            lastY = event.getY();
        }

//        if (event.isShiftDown()) spinreq = 1;
//        if (event.isControlDown()) spinreq = 3 - spinreq; // 3 or 2
//                        if (event.isMetaDown())
//                            sequence.getPlayer().queueSpinMove(oppositeSide[side], (event.isAltDown() ? 2 : 1), 4 - spinreq, true, false, 250);
    }

    private void setSelectedSticker(int color)
    {
        if (view.selectedStickerID != view.NO_STICKER)
            cube.stickerColors[view.selectedStickerID] = color;
    }
        public void keyPressed(KeyEvent event)
    {
        switch (event.getKeyCode()) {
            case KeyEvent.VK_NUMPAD1:
            case KeyEvent.VK_1:
                setSelectedSticker(0);
                break;
            case KeyEvent.VK_NUMPAD2:
            case KeyEvent.VK_2:
                setSelectedSticker(1);
                break;
            case KeyEvent.VK_NUMPAD3:
            case KeyEvent.VK_3:
                setSelectedSticker(2);
                break;
            case KeyEvent.VK_NUMPAD4:
            case KeyEvent.VK_4:
                setSelectedSticker(3);
                break;
            case KeyEvent.VK_NUMPAD5:
            case KeyEvent.VK_5:
                setSelectedSticker(4);
                break;
            case KeyEvent.VK_NUMPAD6:
            case KeyEvent.VK_6:
                setSelectedSticker(5);
                break;
            case KeyEvent.VK_NUMPAD7:
            case KeyEvent.VK_7:
                setSelectedSticker(6);
                break;
            case KeyEvent.VK_NUMPAD8:
            case KeyEvent.VK_8:
                setSelectedSticker(7);
                break;

            case KeyEvent.VK_SPACE: // toggle color/colored gray
                if (view.selectedStickerID != view.NO_STICKER) {
                    if (cube.stickerColors[view.selectedStickerID] < 6)
                        cube.stickerColors[view.selectedStickerID] += 8; // make gray
                    else {
                        if ( cube.stickerColors[view.selectedStickerID] > 7)
                            cube.stickerColors[view.selectedStickerID] -= 8; // make ungray
                    }
                }
                break;

            case KeyEvent.VK_LEFT:
                setSelectedSticker((view.selectedStickerID + 53) % 54); // that's "selectedStickerID-1" the ugly way
                break;
            case KeyEvent.VK_RIGHT:
                setSelectedSticker((view.selectedStickerID + 1) % 54);
                break;
            case KeyEvent.VK_UP:
                setSelectedSticker((view.selectedStickerID + 3) % 54);
                break;
            case KeyEvent.VK_DOWN:
                setSelectedSticker((view.selectedStickerID + 51) % 54); // that's "-3"
                break;

            case KeyEvent.VK_G: // Grayify - set the sticker to its native gray color
                setSelectedSticker(8 + (view.selectedStickerID / 9));
                break;

            case KeyEvent.VK_P: // P for Print!
                System.out.println();
                System.out.println("position: " + cube.positionString()); //@@@

                System.out.println();
                // Print current Cube Orientation to console: /Matt July 3rd 2002
                System.out.print("	double eye[] = { ");
                System.out.print(view.eye[0]);
                System.out.print(", ");
                System.out.print(view.eye[1]);
                System.out.print(", ");
                System.out.print(view.eye[2]);
                System.out.println(" };");
                System.out.print("	double eX[]  = { ");
                System.out.print(view.eX[0]);
                System.out.print(", ");
                System.out.print(view.eX[1]);
                System.out.print(", ");
                System.out.print(view.eX[2]);
                System.out.println(" };");
                break;

        }
        repaint();
    }

    protected void drawEditableHelp(Graphics grap) {
        int rectWidth = 4 + view.imageWidth/100;
        int x0 = 3;
        int y0 = view.imageWidth - 3 - rectWidth;
        for (int jx = 0; jx < 8; jx++) {
            view.offGraphics.setColor(view.colorModel.getPlainColor(jx));
            int row = jx / 3;
            int col = jx % 3;
            grap.fillRect(x0 + col*(rectWidth+1), y0 - row*(rectWidth+1), rectWidth, rectWidth);
        }
    }


    private int checkStickers(int side,  boolean peeking, int x, int y) {
        double sx, sy, sdxh, sdyh, sdxv, sdyv;

        sx   = view.cornerCoordX[CubeState.sides[side*4]]; // screen x coordinate for corner 0, the corner by the lowest sticker number
        sy   = view.cornerCoordY[CubeState.sides[side*4]]; // screen y coordinate for corner 0
        sdxh = (view.cornerCoordX[CubeState.sides[side*4 + 1]] - sx)/3; // x distance between sticker corners in one direction
        sdyh = (view.cornerCoordY[CubeState.sides[side*4 + 1]] - sy)/3; // y distance between sticker corners in one direction
        sdxv = (view.cornerCoordX[CubeState.sides[side*4 + 3]] - sx)/3; // x distance between sticker corners in other direction
        sdyv = (view.cornerCoordY[CubeState.sides[side*4 + 3]] - sy)/3; // y distance between sticker corners in other direction

        if (peeking) {
            double middleStickerFactor = 1.5;
            double middleRowFactor     = 1.5;

            sx  += ((sx + (middleStickerFactor)*sdxh + (middleRowFactor)*sdxv) - view.imageCenter) * view.peekfactor;
            sy  += ((sy + (middleStickerFactor)*sdyh + (middleRowFactor)*sdyv) - view.imageCenter) * view.peekfactor;
        }

        for (int row = 0; row < 3; row++) {
            double _01 = 0.1;
            double _09 = 0.9;
            int rectX[] = new int[4], rectY[] = new int[4];
            for (int sticker = 0; sticker < 3; sticker++) {
                    // calculate coordinates for the 4 corners of this sticker
                    rectX[0] = (int) (sx + (sticker + _01)*sdxh + (row + _01)*sdxv);
                    rectX[1] = (int) (sx + (sticker + _09)*sdxh + (row + _01)*sdxv);
                    rectX[2] = (int) (sx + (sticker + _09)*sdxh + (row + _09)*sdxv);
                    rectX[3] = (int) (sx + (sticker + _01)*sdxh + (row + _09)*sdxv);

                    rectY[0] = (int) (sy + (sticker + _01)*sdyh + (row + _01)*sdyv);
                    rectY[1] = (int) (sy + (sticker + _09)*sdyh + (row + _01)*sdyv);
                    rectY[2] = (int) (sy + (sticker + _09)*sdyh + (row + _09)*sdyv);
                    rectY[3] = (int) (sy + (sticker + _01)*sdyh + (row + _09)*sdyv);

                    if (new Polygon(rectX, rectY, 4).contains(x, y))
                        return side*9 + row*3 + sticker; // stickerID
            }
        }
        return view.NO_STICKER; // found nothing
    }

    // What sticker, if any is seen on pixel (x, y) ?
    protected int getSticker(int x, int y)
    {
        view.projectCorners(view.eX, view.eY, view.eye, view.corners);

        for (int side = 0; side < 6; side++) {
            if (view.sideVisible[side]) {
                int hitSticker = checkStickers(side, false, x, y);
                if (hitSticker != view.NO_STICKER)
                    return hitSticker;
            }
        }

        if (view.peeking) { // Check the peeked stickers later, since they get overwritten by the regular cube.
            for (int peekSide = 0; peekSide < 6; peekSide++) {
                if (!view.sideVisible[peekSide]) {
                    int hitSticker = checkStickers(peekSide, true, x, y);
                    if (hitSticker != view.NO_STICKER)
                        return hitSticker;
                }
            }
        }

        return view.NO_STICKER; // no hit...
    }


}
