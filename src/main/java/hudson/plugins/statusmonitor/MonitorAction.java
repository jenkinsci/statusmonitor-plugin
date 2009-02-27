package hudson.plugins.statusmonitor;

import hudson.model.Action;
import hudson.model.Hudson;
import hudson.model.Project;
import hudson.tasks.Publisher;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.jstl.core.LoopTagStatus;

import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;



/**
 * Status Monitor, shows the configured Jobs in a single screen overview
 * 
 * @author Daniel Galán y Martins
 */
@ExportedBean (defaultVisibility = 999)
public class MonitorAction implements Action {

	private static final long serialVersionUID = 1L;

	private static final int COLUMNS = 2;


	public String getDisplayName() {
		// The Name on the Dashboard
		return "Status Monitor";
		//return "Status Monitor</a> (<a href=\"/monitor?all=true\">all</a>)<a href=\"\">";
	}


	public String getIconFileName() {
		return MonitorDescriptor.ACTION_LOGO_MEDIUM;
	}


	public String getUrlName() {
		// The name of the URL path segment
		return "monitor";
	}


	/**
	 * Returns the projects, that will be displayed
	 * 
	 * @return list containing Projects.
	 */
	private List<Project> getProjects() {
		List<Project> result = new ArrayList<Project>();
		List<Project> projects = Hudson.getInstance().getProjects();

		for (Project project: projects) {
			Publisher publisher = project.getPublisher(PluginImpl.MONITOR_PUBLISHER_DESCRIPTOR);
			// Has Option been selected?
			if (publisher != null) {
				result.add(project);
			}
		}
		return result;
	}


	public String getResult(Project project) {
		String result = null;
		if ((project.getLastCompletedBuild() != null) && (project.getLastCompletedBuild().getResult() != null)) {
			if (project.isDisabled()) {
				result = "DISABLED";
			}
			else {
				result = project.getLastCompletedBuild().getResult().toString();
			}
		}
		else {
			result = "NOT_BUILD";
		}
		return result;
	}


	private int getRows() {
		int size = getProjects().size();
		if (size <= 3) {
			return size;
		}
		return ((size % COLUMNS) == 0) ? (size / COLUMNS) : ((size + 1) / COLUMNS);
	}


	@Exported
	public double getRowsHeight() {
		return 100 / new Double(getRows());
	}


	@Exported
	public Project[][] getProjectsArray() {
		int rows = getRows();
		Project[][] result = new Project[rows][];
		List<Project> projects = getProjects();
		for (int i = 0; i < rows; i++) {
			Project[] row = result[i];
			if (row == null) {
				if (projects.size() <= 3) {
					row = new Project[1];
					row[0] = projects.get(i);
				}
				else {
					// last row and uneven
					if (((i + 1) == rows) && ((projects.size() % 2) != 0)) {
						row = new Project[1];
						row[0] = projects.get(i * COLUMNS);
					}
					else {
						row = new Project[COLUMNS];
						for (int j = 0; j < COLUMNS; j++) {
							row[j] = projects.get((i * COLUMNS) + j);
						}
					}
				}
				result[i] = row;
			}
		}
		return result;
	}


	@Exported
	public int getColspan(LoopTagStatus varStatus, Project[][] projectsArray) {
		if (varStatus.isLast() && (projectsArray.length > 1) && (projectsArray[projectsArray.length - 1].length == 1) && (projectsArray[0].length != 1)) {
			return 2;
		}
		return 1;
	}

}
