package io.github.gcjojo.liblib.math;

import io.github.gcjojo.liblib.utils.MathUtils;
import net.minecraft.util.FastColor;

public class Color {
    public static Color WHITE = new Color(255, 255, 255, 255);
    public int red;
    public int green;
    public int blue;
    public int alpha;

    public Color(int r, int g, int b, int a) {
        this.red = MathUtils.clamp(r, 0, 255);
        this.green = MathUtils.clamp(g, 0, 255);
        this.blue = MathUtils.clamp(b, 0, 255);
        this.alpha = MathUtils.clamp(a, 0, 255);
    }

    public Color(int color) {
        int r = FastColor.ARGB32.red(color);
        int g = FastColor.ARGB32.green(color);
        int b = FastColor.ARGB32.blue(color);
        int a = FastColor.ARGB32.alpha(color);

        this.red = MathUtils.clamp(r, 0, 255);
        this.green = MathUtils.clamp(g, 0, 255);
        this.blue = MathUtils.clamp(b, 0, 255);
        this.alpha = MathUtils.clamp(a, 0, 255);
    }

    /*public Color(String hexValue) {

    }*/

    public int getColorInt() {
        return FastColor.ARGB32.color(alpha, red, green, blue);
    }
}
