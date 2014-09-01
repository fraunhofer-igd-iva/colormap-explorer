// Fraunhofer Institute for Computer Graphics Research (IGD)
// Department Information Visualization and Visual Analytics
//
// Copyright (c) Fraunhofer IGD. All rights reserved.
//
// This source code is property of the Fraunhofer IGD and underlies
// copyright restrictions. It may only be used with explicit
// permission from the respective owner.
package progress;

import org.slf4j.Logger;

/**
 * @author Martin Steiger
 *
 */
public class LoggingProgressListener extends AbstractProgressListener
{
	private int logRate;
	private Logger logger;
	private String message;

	/**
	 * @param logger the logger to use
	 * @param message the message
	 */
	public LoggingProgressListener(Logger logger, String message)
	{
		this.logger = logger;
		this.message = message;
	}

	@Override
	public void start(int max)
	{
		super.start(max);
		this.logRate = max / 12;
	}

	@Override
	public void step()
	{
		super.step();

		if (getProgress() % logRate == 0)
		{
			logger.debug(String.format("%s: %3.0f%%", message, 100d * getProgress() / getMaximum()));
		}
	}

	@Override
	public void finish()
	{
		logger.debug(String.format("%s finished", message));
	}

	@Override
	public boolean isCancelled()
	{
		return false;
	}
}
