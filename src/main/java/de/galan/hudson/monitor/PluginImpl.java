package de.galan.hudson.monitor;

import hudson.Plugin;
import hudson.model.Hudson;
import hudson.tasks.BuildStep;



/**
 * Entry point of the Build Monitor
 */
public class PluginImpl extends Plugin {

	public static final MonitorDescriptor MONITOR_PUBLISHER_DESCRIPTOR = new MonitorDescriptor();


	@Override
	public void start() throws Exception {
		BuildStep.PUBLISHERS.addNotifier(MONITOR_PUBLISHER_DESCRIPTOR);
		Hudson.getInstance().getActions().add(new MonitorAction());
	}
}
