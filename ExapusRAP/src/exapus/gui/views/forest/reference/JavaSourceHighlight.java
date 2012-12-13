package exapus.gui.views.forest.reference;

import java.util.Random;

import de.java2html.util.RGB;

public class JavaSourceHighlight {
	public static RGB generateRandomPastelColor() {
		return generateRandomColor(new RGB(255,255,255));
	}
	
	public static RGB generateRandomColor(RGB mix) {
		//http://stackoverflow.com/questions/43044/algorithm-to-randomly-generate-an-aesthetically-pleasing-color-palette
		
	    Random random = new Random();
	    int red = random.nextInt(256);
	    int green = random.nextInt(256);
	    int blue = random.nextInt(256);

	    if (mix != null) {
	        red = (red + mix.getRed()) / 2;
	        green = (green + mix.getGreen()) / 2;
	        blue = (blue + mix.getBlue()) / 2;
	    }

	    RGB color = new RGB(red, green, blue);
	    return color;
	}

}
