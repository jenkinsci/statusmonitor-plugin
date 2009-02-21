package hudson.plugins.statusmonitor;

import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.Descriptor;
import hudson.model.Hudson;
import hudson.tasks.Publisher;

import java.io.IOException;
import java.util.List;



public class MonitorPublisher extends Publisher {

	public Descriptor<Publisher> getDescriptor() {
		return PluginImpl.MONITOR_PUBLISHER_DESCRIPTOR;
	}


	@Override
	public boolean needsToRunAfterFinalized() {
		return false;
	}


	@Override
	public Action getProjectAction(AbstractProject<?, ?> project) {
		return null;
	}


	@Override
	public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
		installMonitor();
		return true;
	}


	/**
	 * Installs MonitorAction onto the front page. If it is already  installed, nothing happens.
	 */
	private void installMonitor() {
		boolean isInstalled = false;
		List<Action> installedActions = Hudson.getInstance().getActions();
		for (Action installedAction: installedActions) {
			if (installedAction instanceof MonitorAction) {
				isInstalled = true;
				break;
			}
		}
		if (!isInstalled) {
			MonitorAction action = new MonitorAction();
			Hudson.getInstance().getActions().add(action);
		}
	}

}
