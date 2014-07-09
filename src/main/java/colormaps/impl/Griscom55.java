package colormaps.impl;


/**
 * This is modeled after the poster "Cross-Modal Sound to Sight Associations
 * with Musical Timbre in Non-Synesthetes" - William S. Griscom & Stephen E. Palmer - 2013.
 * 
 * It remins unclear whether the scale given is intended as a 2D map, however.
 * 
 * It's also not clear which space is in effect; might be L*u*v* or homegrown. It
 * exhibits substantial lightness variation, suggesting an unmentioned lightness function
 * or RGB magic.
 * @author Simon Thum
 */
public class Griscom55 extends GuoLabStyle {
	
	public Griscom55() {
		super(constant1f(0.55f), linearBa(-4, 40, -10, -25));
	}

	@Override
	public String getName() {
		return "Griscom and Palmer (not that close)";
	}

	@Override
	public String getDescription() {
		return "Griscom and Palmer 2013 - not really a 2D color map, maybe not Lab.";
	}
	
}
