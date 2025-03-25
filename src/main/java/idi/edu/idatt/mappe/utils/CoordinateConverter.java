package idi.edu.idatt.mappe.utils;

/**
 * Utility class for converting between different coordinate systems
 *
 * Based on the formulas:
 * (x, y) = ((xmax/cmax) * c, ymax - (ymax/rmax) * r)
 * (r, c) = (rmax - (rmax/ymax) * y, (cmax/xmax) * x)
 */
public class CoordinateConverter {

    /**
     * Converts board coordinates (r,c) to screen coordinates (x,y)
     * Board coordinates: origin at bottom-left, r increases upward, c increases rightward
     * Screen coordinates: origin at top-left, x increases rightward, y increases downward
     *
     * @param r The row on the board (increasing upward from bottom)
     * @param c The column on the board (increasing rightward)
     * @param rmax The maximum row index on the board
     * @param cmax The maximum column index on the board
     * @param xmax The maximum x-coordinate on the screen
     * @param ymax The maximum y-coordinate on the screen
     * @return An array containing the screen coordinates [x, y]
     */
    public static double[] boardToScreen(int r, int c, int rmax, int cmax, double xmax, double ymax) {
        double x = (xmax / cmax) * c;
        double y = ymax - (ymax / rmax) * r;
        return new double[]{x, y};
    }

    /**
     * Converts screen coordinates (x,y) to board coordinates (r,c)
     * Screen coordinates: origin at top-left, x increases rightward, y increases downward
     * Board coordinates: origin at bottom-left, r increases upward, c increases rightward
     *
     * @param x The x-coordinate on the screen
     * @param y The y-coordinate on the screen
     * @param rmax The maximum row index on the board
     * @param cmax The maximum column index on the board
     * @param xmax The maximum x-coordinate on the screen
     * @param ymax The maximum y-coordinate on the screen
     * @return An array containing the board coordinates [r, c]
     */
    public static int[] screenToBoard(double x, double y, int rmax, int cmax, double xmax, double ymax) {
        int c = (int)((cmax / xmax) * x);
        int r = (int)(rmax - ((rmax / ymax) * y));

        r = Math.max(0, Math.min(rmax, r));
        c = Math.max(0, Math.min(cmax, c));

        return new int[]{r, c};
    }
}