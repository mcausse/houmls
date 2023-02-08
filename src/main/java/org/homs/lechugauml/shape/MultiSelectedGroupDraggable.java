package org.homs.lechugauml.shape;

import org.homs.lechugauml.Diagram;

import java.awt.*;
import java.util.List;

public class MultiSelectedGroupDraggable implements Draggable {

    final List<Shape> multiSelectedShapesToDrag;

    public MultiSelectedGroupDraggable(List<Shape> multiSelectedShapesToDrag) {
        this.multiSelectedShapesToDrag = multiSelectedShapesToDrag;
    }

    @Override
    public Cursor getTranslationCursor() {
        return Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
    }

    @Override
    public Rectangle getRectangle() {
        return null;
    }

    @Override
    public void translate(Diagram diagram, double dx, double dy) {
        this.multiSelectedShapesToDrag.forEach(shape -> shape.translate(diagram, dx, dy));
    }

    @Override
    public void dragHasFinished(Diagram diagram) {
        this.multiSelectedShapesToDrag.forEach(shape -> shape.dragHasFinished(diagram));
    }

}
