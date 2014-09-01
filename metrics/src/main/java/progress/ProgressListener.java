// Fraunhofer Institute for Computer Graphics Research (IGD)
// Competence Center for Information Visualization and Visual Analytics
//
// Copyright (c) 2011-2012 Fraunhofer IGD. All rights reserved.
//
// This source code is property of the Fraunhofer IGD and underlies
// copyright restrictions. It may only be used with explicit
// permission from the respective owner.

package progress;


/**
 * An interface for progress updates
 * @author Martin Steiger
 */
public interface ProgressListener
{
	public void start(int max);

	public void step();

	public void finish();

	public boolean isCancelled();
}
