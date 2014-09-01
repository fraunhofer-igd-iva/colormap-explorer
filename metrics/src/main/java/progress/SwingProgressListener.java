// Fraunhofer Institute for Computer Graphics Research (IGD)
// Department Information Visualization and Visual Analytics
//
// Copyright (c) Fraunhofer IGD. All rights reserved.
//
// This source code is property of the Fraunhofer IGD and underlies
// copyright restrictions. It may only be used with explicit
// permission from the respective owner.
package progress;

import java.awt.Component;
import java.lang.reflect.InvocationTargetException;

import javax.swing.ProgressMonitor;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Martin Steiger
 *
 */
public class SwingProgressListener extends AbstractProgressListener
{
	private static final Logger logger = LoggerFactory.getLogger(SwingProgressListener.class);

	private ProgressMonitor task;
	private final Component parent;
	private Object message;

	/**
	 * @param parent the parent component
	 * @param message the message object(!) to set
	 */
	public SwingProgressListener(Component parent, Object message)
	{
		this.parent = parent;
		this.message = message;
	}

	@Override
	public void start(int max)
	{
		super.start(max);
		task = new ProgressMonitor(parent, message, "", 0, max);
		task.setMillisToDecideToPopup(100);
		task.setMillisToPopup(500);
	}

	@Override
	public void step()
	{
		super.step();

		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				String note = String.format("Completed %d%%.\n", getProgress() * 100 / getMaximum());
				task.setNote(note);
				task.setProgress(getProgress());
			}
		});
	}

	@Override
	public void finish()
	{
		try
		{
			SwingUtilities.invokeAndWait(new Runnable()
			{
				@Override
				public void run()
				{
					task.close();
				}
			});
		}
		catch (InvocationTargetException e)
		{
			logger.warn("Caught an exception while waiting for progress dialog to close", e);
		}
		catch (InterruptedException e)
		{
			logger.warn("Timeout while waiting for progress dialog to close", e);
		}
	}

	@Override
	public boolean isCancelled()
	{
		return task.isCanceled();
	}

}
