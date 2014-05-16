/*
 * Copyright 2014 Fraunhofer IGD
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

/**
 * Holds a singleton instance of the global event bus
 * @author Martin Steiger
 */
public final class MyEventBus
{
	private static final EventBus INSTANCE = new EventBus("Global");

	private static final Logger logger = LoggerFactory.getLogger(MyEventBus.class);
	
	static 
	{
		INSTANCE.register(new Object()
		{
			@Subscribe
			public void onDeadObject(DeadEvent event)
			{
				logger.warn("Dead event: " + event.getEvent());
			}
		});
	}
	
	/** 
	 * Private!
	 */
	private MyEventBus()
	{
		// empty
	}
	
	/**
	 * @return the only instance (thread-safe)
	 */
	public static EventBus getInstance()
	{
		return INSTANCE;
	}
}
