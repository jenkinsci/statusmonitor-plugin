package hudson.plugins.statusmonitor;

import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.StaplerRequest;


public class MonitorDescriptor extends BuildStepDescriptor<Publisher> {

	public static final String ACTION_LOGO_LARGE = "/plugin/statusmonitor/icons/monitor-32x32.png";
	public static final String ACTION_LOGO_MEDIUM = "/plugin/statusmonitor/icons/monitor-22x22.png";


	protected MonitorDescriptor() {
		super(MonitorPublisher.class);
	}


	@Override
	public String getDisplayName() {
		return "Status Monitor";
	}


	@Override
	public boolean isApplicable(Class<? extends AbstractProject> jobType) {
        	return true;
	}


	@Override
	public Publisher newInstance(StaplerRequest req, JSONObject formData) throws FormException {
		return new MonitorPublisher();
	}

}
