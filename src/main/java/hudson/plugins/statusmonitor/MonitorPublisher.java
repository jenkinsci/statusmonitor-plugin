package hudson.plugins.statusmonitor;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.BuildListener;
import hudson.tasks.Notifier;
import java.io.IOException;


@Deprecated
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
		return true;
	}
}
