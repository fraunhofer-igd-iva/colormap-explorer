package colormaps.impl;

public class Guo30Gauss extends GuoLabStyle {
	
	public Guo30Gauss() {
		lightness_function = pseudo_gaussian(1.4f);
		chroma_function = linear_ab(0, 80, 0, -80);
	}

	@Override
	public String getName() {
		return "Guo 2005 (Gaussian)";
	}

	@Override
	public String getDescription() {
		return "Guo 2005 (L*a*b*; Gaussian lightness; xy mapped to ab)";
	}

	@Override
	public colormaps.ColorSpace getColorSpace() {
		return colormaps.ColorSpace.GUO_2005_GAUSS;
	}
	
}