// Fraunhofer Institute for Computer Graphics Research (IGD)
// Department Information Visualization and Visual Analytics
//
// Copyright (c) Fraunhofer IGD. All rights reserved.
//
// This source code is property of the Fraunhofer IGD and underlies
// copyright restrictions. It may only be used with explicit
// permission from the respective owner.
package progress;


/**
 * @author Martin Steiger
 *
 */
public abstract class AbstractProgressListener implements ProgressListener
{
	private int progress;
	private int maximum;

	@Override
	public void start(int max)
	{
		this.progress = 0;
		this.maximum = max;
	}

	@Override
	public void step()
	{
		progress++;
	}

	@Override
	public void finish()
	{
		progress = maximum;
	}

	/**
	 * @return the progress
	 */
	public int getProgress()
	{
		return progress;
	}

	/**
	 * @return the maximum
	 */
	public int getMaximum()
	{
		return maximum;
	}
}
