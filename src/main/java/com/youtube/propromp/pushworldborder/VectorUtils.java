package com.youtube.propromp.pushworldborder;

import com.github.yannicklamprecht.worldborder.api.Position;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

public class VectorUtils {
    public static Vector toVector(Position position) {
        return new Vector(position.getX(), 0, position.getZ());
    }

    public static Position toPosition(Vector position) {
        return new Position(position.getX(), position.getZ());
    }

    public static int compareRange(double x, double min, double max) {
        if (x < min)
            return -1;
        if (x > max)
            return 1;
        return 0;
    }

    private static double clamp(double x, double min, double max) {
        return Math.max(min, Math.min(x, max));
    }

    public static Vector getClosestPoint(BoundingBox box, Vector point) {
        return new Vector(
                clamp(point.getX(), box.getMinX(), box.getMaxX()),
                clamp(point.getY(), box.getMinY(), box.getMaxY()),
                clamp(point.getZ(), box.getMinZ(), box.getMaxZ())
        );
    }
}
