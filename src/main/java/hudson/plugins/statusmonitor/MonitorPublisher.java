package hudson.plugins.statusmonitor;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.model.Hudson;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;

import java.io.IOException;
import java.util.List;



public class MonitorPublisher extends Notifier {

	@Extension
	public static final MonitorDescriptor DESCRIPTOR = new MonitorDescriptor();

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

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.STEP;
    }
}
