package edu.uwi.soscai.algorithm;

import java.util.*;

import edu.uwi.soscai.model.Rect;
import javafx.animation.Timeline;
import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;

public abstract class BinPacker {
    protected Canvas drawer;
    protected Bounds container;
    protected Timeline timeline;

    public BinPacker(Canvas drawer, Bounds container) {
        this.drawer = drawer;
        this.container = container;
    }

    public abstract Map<String, Number> pack(List<Rect> boxes);
}
