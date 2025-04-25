package idi.edu.idatt.mappe.views;

import idi.edu.idatt.mappe.models.Tile;
import idi.edu.idatt.mappe.utils.CoordinateConverter;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.effect.DropShadow;

public class ConnectionView extends Group {

    public enum Type {
        LADDER, SNAKE
    }

    private static final double TILE_PADDING = 10;

    public ConnectionView(Tile from, Tile to, Type type, int rows, int cols, double width, double height) {
        // Calculate tile size
        double tileWidth = width / cols;
        double tileHeight = height / rows;

        // Calculate center positions of both tiles
        double[] start = CoordinateConverter.boardToScreen(from.getY(), from.getX(), rows, cols, width, height);
        double[] end = CoordinateConverter.boardToScreen(to.getY(), to.getX(), rows, cols, width, height);

        double startX = start[0] + tileWidth / 2;
        double startY = start[1] + tileHeight / 2;
        double endX = end[0] + tileWidth / 2;
        double endY = end[1] + tileHeight / 2;

        if (type == Type.LADDER) {
            drawLadder(startX, startY, endX, endY, tileWidth);
        } else {
            drawSnake(startX, startY, endX, endY, tileWidth, tileHeight);
        }
    }

    private void drawLadder(double x1, double y1, double x2, double y2, double tileWidth) {
        // Calculate ladder width based on tile size
        double ladderWidth = tileWidth * 0.4;

        // Calculate the direction vector and perpendicular vector
        double dirX = x2 - x1;
        double dirY = y2 - y1;
        double length = Math.sqrt(dirX * dirX + dirY * dirY);

        // Normalize direction vector
        dirX /= length;
        dirY /= length;

        // Calculate perpendicular vector (90 degrees counter-clockwise)
        double perpX = -dirY;
        double perpY = dirX;

        // Calculate the points for the ladder sides
        double side1X1 = x1 + perpX * ladderWidth/2;
        double side1Y1 = y1 + perpY * ladderWidth/2;
        double side1X2 = x2 + perpX * ladderWidth/2;
        double side1Y2 = y2 + perpY * ladderWidth/2;

        double side2X1 = x1 - perpX * ladderWidth/2;
        double side2Y1 = y1 - perpY * ladderWidth/2;
        double side2X2 = x2 - perpX * ladderWidth/2;
        double side2Y2 = y2 - perpY * ladderWidth/2;

        // Create side lines with gradient color and thicker width
        Line side1 = new Line(side1X1, side1Y1, side1X2, side1Y2);
        Line side2 = new Line(side2X1, side2Y1, side2X2, side2Y2);

        LinearGradient ladderGradient = new LinearGradient(
                0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.SADDLEBROWN),
                new Stop(1, Color.SIENNA)
        );

        side1.setStroke(ladderGradient);
        side2.setStroke(ladderGradient);
        side1.setStrokeWidth(5);
        side2.setStrokeWidth(5);

        // Add shadow effect
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(3.0);
        dropShadow.setOffsetX(2.0);
        dropShadow.setOffsetY(2.0);
        dropShadow.setColor(Color.color(0, 0, 0, 0.3));

        side1.setEffect(dropShadow);
        side2.setEffect(dropShadow);

        getChildren().addAll(side1, side2);

        // Add rungs (steps)
        int steps = (int)(length / 20) + 2;  // Adjust number of steps based on ladder length
        steps = Math.max(3, Math.min(12, steps));  // At least 3 steps, at most 12

        for (int i = 0; i <= steps; i++) {
            double t = (double) i / steps;
            double px1 = side1X1 + (side1X2 - side1X1) * t;
            double py1 = side1Y1 + (side1Y2 - side1Y1) * t;
            double px2 = side2X1 + (side2X2 - side2X1) * t;
            double py2 = side2Y1 + (side2Y2 - side2Y1) * t;

            Line rung = new Line(px1, py1, px2, py2);
            rung.setStroke(Color.GOLDENROD);
            rung.setStrokeWidth(3);
            rung.setEffect(dropShadow);
            getChildren().add(rung);
        }
    }

    private void drawSnake(double x1, double y1, double x2, double y2, double tileWidth, double tileHeight) {
        // Calculate control points for a cubic curve
        double ctrlX1 = x1 + (x2 - x1) * 0.25 + (Math.random() - 0.5) * tileWidth * 1.5;
        double ctrlY1 = y1 + (y2 - y1) * 0.25 + (Math.random() - 0.5) * tileHeight * 1.5;
        double ctrlX2 = x1 + (x2 - x1) * 0.75 + (Math.random() - 0.5) * tileWidth * 1.5;
        double ctrlY2 = y1 + (y2 - y1) * 0.75 + (Math.random() - 0.5) * tileHeight * 1.5;

        // Create a cubic curve for snake body
        CubicCurve snakeBody = new CubicCurve(
                x1, y1,
                ctrlX1, ctrlY1,
                ctrlX2, ctrlY2,
                x2, y2
        );

        // Create gradient for snake
        LinearGradient snakeGradient = new LinearGradient(
                0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.DARKGREEN),
                new Stop(0.5, Color.INDIANRED),
                new Stop(1, Color.MEDIUMSEAGREEN)
        );

        snakeBody.setStroke(snakeGradient);
        snakeBody.setStrokeWidth(tileWidth * 0.25);
        snakeBody.setFill(null);

        // Add shadow
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(5.0);
        dropShadow.setOffsetX(3.0);
        dropShadow.setOffsetY(3.0);
        dropShadow.setColor(Color.color(0, 0, 0, 0.5));
        snakeBody.setEffect(dropShadow);

        // Create snake head
        double headRadius = tileWidth * 0.2;
        Circle head = new Circle(x1, y1, headRadius, Color.DARKGREEN);
        head.setEffect(dropShadow);

        // Create snake eyes
        // Calculate direction from first control point to head
        double dirX = x1 - ctrlX1;
        double dirY = y1 - ctrlY1;
        double dir = Math.atan2(dirY, dirX);

        double eyeOffset = headRadius * 0.5;
        double eyeRadius = headRadius * 0.25;

        // Left eye
        Circle leftEye = new Circle(
                x1 + Math.cos(dir - 0.5) * eyeOffset,
                y1 + Math.sin(dir - 0.5) * eyeOffset,
                eyeRadius, Color.WHITE
        );

        // Right eye
        Circle rightEye = new Circle(
                x1 + Math.cos(dir + 0.5) * eyeOffset,
                y1 + Math.sin(dir + 0.5) * eyeOffset,
                eyeRadius, Color.WHITE
        );

        // Eye pupils
        Circle leftPupil = new Circle(
                x1 + Math.cos(dir - 0.5) * eyeOffset,
                y1 + Math.sin(dir - 0.5) * eyeOffset,
                eyeRadius * 0.5, Color.BLACK
        );

        Circle rightPupil = new Circle(
                x1 + Math.cos(dir + 0.5) * eyeOffset,
                y1 + Math.sin(dir + 0.5) * eyeOffset,
                eyeRadius * 0.5, Color.BLACK
        );

        // Create forked tongue
        double tongueLength = headRadius * 1.5;
        double tongueWidth = headRadius * 0.3;

        Polygon tongue = new Polygon(
                x1 + Math.cos(dir) * headRadius, y1 + Math.sin(dir) * headRadius,
                x1 + Math.cos(dir) * (headRadius + tongueLength), y1 + Math.sin(dir) * (headRadius + tongueLength) - tongueWidth,
                x1 + Math.cos(dir) * (headRadius + tongueLength * 0.7), y1 + Math.sin(dir) * (headRadius + tongueLength * 0.7),
                x1 + Math.cos(dir) * (headRadius + tongueLength), y1 + Math.sin(dir) * (headRadius + tongueLength) + tongueWidth
        );
        tongue.setFill(Color.RED);

        // Add all parts to the group
        getChildren().addAll(snakeBody, head, leftEye, rightEye, leftPupil, rightPupil, tongue);
    }
}