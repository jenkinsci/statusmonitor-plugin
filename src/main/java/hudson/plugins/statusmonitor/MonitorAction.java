package hudson.plugins.statusmonitor;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Job;
import hudson.model.RootAction;
import hudson.model.TopLevelItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import jenkins.model.GlobalConfiguration;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;

/**
 * Status Monitor, shows the configured Jobs in a single screen overview
 */
@Extension
public class MonitorAction implements RootAction {

	public String getDisplayName() {
		// The Name on the Dashboard
		return "Status Monitor";
	}


	public String getIconFileName() {
		return "symbol-pulse-outline plugin-ionicons-api";
	}


	public String getUrlName() {
		// The name of the URL path segment
		return "monitor";
	}


    public int getColumns(List<Job<?, ?>> projects) {
        int columns = GlobalConfiguration.all().get(MonitorConfiguration.class).getColumns();
        return Math.min(projects.size(), columns);
    }

	/**
	 * @return list projects that will be displayed
	 */
	public List<Job<?, ?>> getProjects() {
        List<Job<?, ?>> result = new ArrayList<>();
		List<TopLevelItem> topLevelItems = Jenkins.get().getAllItems(TopLevelItem.class);
        for (TopLevelItem item : topLevelItems) {
            if (item instanceof Job<?, ?>) {
                Job<?, ?> job = (Job<?, ?>) item;
                MonitorJobProperty prop = job.getProperty(MonitorJobProperty.class);
                if (prop != null) {
                    result.add(job);
                } else if (job instanceof AbstractProject<?, ?> && ((AbstractProject<?, ?>) job).getPublishersList().get(MonitorPublisher.DESCRIPTOR) != null) {
                    result.add(job);
                }
            }
        }

		return result;
	}

	public String getResult(Job<?, ?> project) {
		String result;
        if ((project instanceof AbstractProject<?, ?> && ((AbstractProject<?, ?>) project).isDisabled())
                || (project instanceof WorkflowJob && ((WorkflowJob) project).isDisabled())) {
            result = "DISABLED";
        } else {
            if ((project.getLastCompletedBuild() != null) && (project.getLastCompletedBuild().getResult() != null)) {
                result = project.getLastCompletedBuild().getResult().toString();
            } else {
                result = "NOT_BUILT";
            }
        }
		return result.toLowerCase(Locale.ROOT);
	}

    public boolean isLastRow(int size, int index, int columns) {
        int rowCount = size / columns;

        return index == columns * rowCount;
    }
}
