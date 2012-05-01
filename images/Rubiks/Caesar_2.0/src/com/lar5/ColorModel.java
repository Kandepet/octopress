package com.lar5;

import java.awt.*;

/**
 * Created by lars  May 25, 2003  2:31:51 PM
 *
 * Maps abstract sticker color IDs to real colors on screen, with varying sophistication
 */
public class ColorModel
{
    Color colList[];
    double light[]; // Light source coordinates, for shadows
    int lightAngle[]; // 'Shadow factor'
    boolean useShading; //
    boolean[] sideVisible; // local reference
    boolean editMode;

    Color bgcolor, txtcolor, txtbgcolor;


    public ColorModel(boolean _useShading, String sideColors, boolean _editMode)
    {
        editMode = _editMode;
        if (sideColors == null)
            sideColors = "GBRWOY"; // default side color order
        useShading = _useShading;
        if (useShading) {
            colList = new Color[160];
            for (int i = 0; i < 20; i++) {
                colList[i + 20*sideColors.indexOf("G")] = new Color(i*3,       84 + i*9,  i*3);			// Green
                colList[i + 20*sideColors.indexOf("B")] = new Color(i*3,       i*3,       84 + i*9);	// Blue
                colList[i + 20*sideColors.indexOf("R")] = new Color(84 + i*9,  i*3,       i*3);			// Red
                colList[i + 20*sideColors.indexOf("W")] = new Color(103 + i*8, 103 + i*8, 103 + i*8);	// White
                colList[i + 20*sideColors.indexOf("O")] = new Color(84 + i*9,  40 + i*5,  i*3);			// Orange
                colList[i + 20*sideColors.indexOf("Y")] = new Color(84 + i*9,  84 + i*9,  0);		    // Yellow
                colList[i + 120] = new Color(52 + i*4,  52 + i*4,  52 + i*4);		// Gray
                colList[i + 140] = new Color(26 + i*2,  26 + i*2,  26 + i*2);		// Darkgray
            }
            light = new double[3];
            lightAngle = new int[6];
        } else { // plain colors
            colList = new Color [] {
                null, null, null, null, null, null,
                Color.gray, Color.darkGray,
                null, null, null, null, null, null
            };

            for (int i = 0; i < 6; i++) {
                colList[sideColors.indexOf("G")] = Color.green;
                colList[sideColors.indexOf("B")] = Color.blue;
                colList[sideColors.indexOf("R")] = Color.red;
                colList[sideColors.indexOf("W")] = Color.white;
                colList[sideColors.indexOf("O")] = new Color (255, 127, 0); // better orange
                colList[sideColors.indexOf("Y")] = Color.yellow;
            }
            // these 'colored gray' colors only work in the non shadowed mode for now. /Lars May 27 2003
            int grayfactor = 60;
            for (int i = 0; i < 6; i++) {
                Color color = colList[i];

                int grayRed   = (grayfactor*color.gray.getRed()   + (100-grayfactor)*color.getRed())/100;
                int grayGreen = (grayfactor*color.gray.getGreen() + (100-grayfactor)*color.getGreen())/100;
                int grayBlue  = (grayfactor*color.gray.getBlue()  + (100-grayfactor)*color.getBlue())/100;

                colList[i+8] = new Color(grayRed, grayGreen, grayBlue);
            }
        }
    }

    protected void setColors(Color _bgColor, Color _txtcolor, Color _txtbgcolor) {
        bgcolor    = _bgColor;
        txtcolor   = _txtcolor;
        txtbgcolor = _txtbgcolor;
    }

    protected void prepColors(double beye[], double beX[], double beY[], boolean _sideVisible[]) {
        sideVisible = _sideVisible;
        if (useShading) {
            VecMath.copy(beye, 0, light);
            VecMath.scalMult(light, -3);
            VecMath.add(beX, light, 0);
            VecMath.sub(beY, light, 0);
            for (int side = 0; side < 6; side++) {
                if (sideVisible[side])
                    lightAngle[side] = (int) (9.6*(1 - VecMath.cosAng(light, CubeView.sideVec, 3*side)));
                else
                    lightAngle[side] = 17; // for 'peek' colors
            }
        }
    }

    /**
     * Is this color close enough to the background to be hard to differentiate?
     * @param stickerColor
     * @return
     */
    protected boolean closeToBg(Color stickerColor) {
        int diffRed   = stickerColor.getRed()   - bgcolor.getRed();
        int diffBlue  = stickerColor.getBlue()  - bgcolor.getBlue();
        int diffGreen = stickerColor.getGreen() - bgcolor.getGreen();
        int diff = Math.abs(diffRed) + Math.abs(diffBlue) + Math.abs(diffGreen);
        return diff < 24;
    }

    /**
     * @return Color to display for this colorID on this side, right now.
     * null means this sticker should not be drawn.
     */
    protected Color getStickerColor(int colorID, int side) {
        if (useShading) {
            return colList[20*colorID + lightAngle[side]]; // todo if (colorID) >=8 colorID = 6;
        } else {
            if (sideVisible[side] || (colorID < 6) || editMode)
                return colList[colorID];
            else
                return null; // No use in peeking on gray stickers (unless we're editing)
        }
    }

    protected Color getPlainColor(int colorID) {
        return colList[colorID];
    }
}
