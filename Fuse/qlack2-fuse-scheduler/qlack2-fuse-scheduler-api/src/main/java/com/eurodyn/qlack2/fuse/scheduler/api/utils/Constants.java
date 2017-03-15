/*
 * Copyright 2014 EUROPEAN DYNAMICS SA <info@eurodyn.com>
 *
 * Licensed under the EUPL, Version 1.1 only (the "License").
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */
package com.eurodyn.qlack2.fuse.scheduler.api.utils;

public class Constants {

	public static final String QSCH_JOB_QUALIFIER = "qlack2.job.qualifier";

	/**
	 * Enumeration of the week days.
	 */
	public static enum TRIGGER_DAYS {
		MON, TUE, WED, THU, FRI, SAT, SUN
	};

	/**
	 * Enumeration of the month period.
	 */
	public static enum TRIGGER_MONTH_PERIOD {
		FIRST, LAST
	};

	/**
	 * Enumeration of the trigger fire types.
	 */
	public static enum TRIGGERS {
		ASAP, Daily, Weekly, Monthly, Cron
	}

	/**
	 * Enumeration of the trigger mis-fire policies.
	 */
	public static enum TRIGGER_MISFIRE {
		/**
		 * Instructs the Scheduler that upon a mis-fire situation, the
		 * CronTrigger wants to have it's next-fire-time updated to the next
		 * time in the schedule after the current time (taking into account any
		 * associated Calendar, but it does not want to be fired now.
		 */
		MISFIRE_INSTRUCTION_DO_NOTHING,

		/**
		 * Instructs the Scheduler that upon a mis-fire situation, the
		 * CronTrigger wants to be fired now by Scheduler.
		 */
		MISFIRE_INSTRUCTION_FIRE_ONCE_NOW,

		/**
		 * Instructs the Scheduler that the Trigger will never be evaluated for
		 * a misfire situation, and that the scheduler will simply try to fire
		 * it as soon as it can, and then update the Trigger as if it had fired
		 * at the proper time. NOTE: if a trigger uses this instruction, and it
		 * has missed several of its scheduled firings, then several rapid
		 * firings may occur as the trigger attempt to catch back up to where it
		 * would have been. For example, a SimpleTrigger that fires every 15
		 * seconds which has misfired for 5 minutes will fire 20 times once it
		 * gets the chance to fire.
		 */
		MISFIRE_INSTRUCTION_IGNORE_MISFIRE_POLICY
	}

}
