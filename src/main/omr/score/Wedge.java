//----------------------------------------------------------------------------//
//                                                                            //
//                                 W e d g e                                  //
//                                                                            //
//  Copyright (C) Herve Bitteur 2000-2006. All rights reserved.               //
//  This software is released under the terms of the GNU General Public       //
//  License. Please contact the author at herve.bitteur@laposte.net           //
//  to report bugs & suggestions.                                             //
//----------------------------------------------------------------------------//
//
package omr.score;

import omr.glyph.Glyph;
import omr.glyph.Shape;

import omr.score.visitor.ScoreVisitor;

import omr.sheet.PixelPoint;
import omr.sheet.PixelRectangle;

import omr.util.Logger;

/**
 * Class <code>Wedge</code> represents a crescendo or decrescendo (diminuendo)
 *
 * @author Herv&eacute Bitteur
 * @version $Id$
 */
public class Wedge
    extends Direction
{
    //~ Static fields/initializers ---------------------------------------------

    /** Usual logger utility */
    private static final Logger logger = Logger.getLogger(Wedge.class);

    //~ Instance fields --------------------------------------------------------

    /** Vertical spread in units */
    private final int spread;

    //~ Constructors -----------------------------------------------------------

    //-------//
    // Wedge //
    //-------//
    /**
     * Creates a new instance of Wedge edge (there must be one for the wedge start, and one for the wedge stop).
     *
     * @param start indicate a wedge start
     * @param measure measure that contains this wedge edge
     * @param point middle point on wedge edge
     * @param glyph the underlying glyph
     */
    public Wedge (boolean     start,
                  Measure     measure,
                  SystemPoint point,
                  Chord       chord,
                  Glyph       glyph)
    {
        super(start, measure, point, chord);
        addGlyph(glyph);

        // Spread
        if ((start && (getShape() == Shape.DECRESCENDO)) ||
            (!start && (getShape() == Shape.CRESCENDO))) {
            spread = getSystem()
                         .getScale()
                         .pixelsToUnits(glyph.getContourBox().height);
        } else {
            spread = 0;
        }
    }

    //~ Methods ----------------------------------------------------------------

    //-----------//
    // getSpread //
    //-----------//
    /**
     * Report the vertical spread of the wedge
     *
     * @return vertical spread in units
     */
    public int getSpread ()
    {
        return spread;
    }

    //--------//
    // accept //
    //--------//
    @Override
    public boolean accept (ScoreVisitor visitor)
    {
        return visitor.visit(this);
    }

    //----------//
    // toString //
    //----------//
    @Override
    public String toString ()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("{Wedge ");
        sb.append(super.toString());
        sb.append("}");

        return sb.toString();
    }

    //----------//
    // populate //
    //----------//
    /**
     * Used by ScoreBuilder to allocate the wedges
     *
     * @param glyph underlying glyph
     * @param startingMeasure measure where left side is located
     * @param startingPoint location for left point
     */
    static void populate (Glyph       glyph,
                          Measure     startingMeasure,
                          SystemPoint startingPoint)
    {
        System         system = startingMeasure.getSystem();
        SystemPart     part = startingMeasure.getPart();
        PixelRectangle box = glyph.getContourBox();

        // Start
        new Wedge(
            true,
            startingMeasure,
            startingPoint,
            findChord(startingMeasure, startingPoint),
            glyph);

        // Stop
        SystemPoint endingPoint = system.toSystemPoint(
            new PixelPoint(box.x + box.width, box.y + (box.height / 2)));
        Measure     endingMeasure = part.getMeasureAt(endingPoint);
        new Wedge(
            false,
            endingMeasure,
            endingPoint,
            findChord(endingMeasure, endingPoint),
            glyph);
    }
}