package iryrwarosh;

import java.awt.Color;

public class Common {
	public static Color guiForeground = hsv(120, 5, 66);
	public static Color guiBackground = hsv(120, 5,  5);

	public static Color playScreenForeground = hsv(20, 33, 66);
	public static Color playScreenBackground = hsv(20, 30, 15);

	
	public static Color hsv(int h, int s, int v){
		return Color.getHSBColor(h / 360f, s / 100f, v / 100f);
	}

}
