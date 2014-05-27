package colormaps.impl;

public class Guo30Cone extends GuoLabStyle {
	
	public Guo30Cone() {
		lightness_function = cone(60f);
		chroma_function = linear_ab(0, 80, 0, -80);
	}

	@Override
	public String getName() {
		return "Guo 2005 (Cone)";
	}

	@Override
	public String getDescription() {
		return "Guo 2005 (L*a*b*; Cone lightness; chroma linearly mapped to ab)";
	}

	@Override
	public colormaps.ColorSpace getColorSpace() {
		return colormaps.ColorSpace.GUO_2005_CONE;
	}
	
}