package de.fhg.igd.iva.colorspaces;

import java.awt.Color;

public class HSL {
	
	private HSL() {	}
	
	/** 
	 * Adapted from w3c pseudocode
	 * http://www.w3.org/TR/css3-color/#hsl-color
	 * HOW TO RETURN hue.to.rgb(m1, m2, h): 
	       IF h<0: PUT h+1 IN h
	       IF h>1: PUT h-1 IN h
	       IF h*6<1: RETURN m1+(m2-m1)*h*6
	       IF h*2<1: RETURN m2
	       IF h*3<2: RETURN m1+(m2-m1)*(2/3-h)*6
	   	   RETURN m1
	 */
	private static float hueToRGB(float m1, float m2, float h) {
		if (h < 0)
			h+= 1;
		if (h > 1)
			h -= 1;
		
		if (h * 6 < 1)
			return m1 + (m2-m1) * h * 6f;
		if(h*2f < 1)
			return m2;
		if (h*3f < 2)
			return m1+(m2-m1)*(4f-h*6f);
		return m1;
	}

	/** 
	 * w3c pseudocode
	 * HOW TO RETURN hsl.to.rgb(h, s, l): 
	       SELECT: 
		  l<=0.5: PUT l*(s+1) IN m2
		  ELSE: PUT l+s-l*s IN m2
	       PUT l*2-m2 IN m1
	       PUT hue.to.rgb(m1, m2, h+1/3) IN r
	       PUT hue.to.rgb(m1, m2, h    ) IN g
	       PUT hue.to.rgb(m1, m2, h-1/3) IN b
	       RETURN (r, g, b)
	 *
	 */
	public static Color HSLtoRGB(float h, float s, float l) {
		float m2;
		if (l <= 0.5)
			m2 = l * (s+1f);
		else
			m2 = l+s-l*s;
		float m1 = l*2f-m2;
		float r = hueToRGB(m1, m2, h+1f/3f);
		float g = hueToRGB(m1, m2, h);
		float b = hueToRGB(m1, m2, h-1f/3f);

		return new Color(r, g, b);
	}

}
