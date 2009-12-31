package hudson.plugins.statusmonitor;

import hudson.Extension;
import hudson.model.*;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import javax.servlet.jsp.jstl.core.LoopTagStatus;
import java.util.ArrayList;
import java.util.List;

/**
 * Status Monitor, shows the configured Jobs in a single screen overview
 * 
 * @author Daniel Galán y Martins
 */
@ExportedBean (defaultVisibility = 999)
@Extension
public class MonitorAction implements RootAction {
	private static final long serialVersionUID = 1L;

	private static final int COLUMNS = 2;


	public String getDisplayName() {
		// The Name on the Dashboard
		return "Status Monitor";
	}


	public String getIconFileName() {
		return MonitorDescriptor.ACTION_LOGO_MEDIUM;
	}


	public String getUrlName() {
		// The name of the URL path segment
		return "/monitor";
	}


	/**
	 * @return list projects that will be displayed
	 */
	private List<AbstractProject> getProjects() {
        List<AbstractProject> result = new ArrayList<AbstractProject>();
		List<TopLevelItem> topLevelItems = Hudson.getInstance().getItems();
        for (TopLevelItem topLevelItem : topLevelItems) {
            if (topLevelItem instanceof AbstractProject) {
                AbstractProject abstractProject = (AbstractProject) topLevelItem;
                if (abstractProject.getPublishersList().get(MonitorPublisher.DESCRIPTOR) != null) {
                        result.add(abstractProject);
                }
            }
        }

		return result;
	}


	public String getResult(AbstractProject project) {
		String result;
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
	public AbstractProject[][] getProjectsArray() {
		int rows = getRows();
		AbstractProject[][] result = new AbstractProject[rows][];
		List<AbstractProject> projects = getProjects();
		for (int i = 0; i < rows; i++) {
			AbstractProject[] row = result[i];
			if (row == null) {
				if (projects.size() <= 3) {
					row = new AbstractProject[1];
					row[0] = projects.get(i);
				}
				else {
					// last row and uneven
					if (((i + 1) == rows) && ((projects.size() % 2) != 0)) {
						row = new AbstractProject[1];
						row[0] = projects.get(i * COLUMNS);
					}
					else {
						row = new AbstractProject[COLUMNS];
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
	public int getStyleId(LoopTagStatus varStatus, AbstractProject[][] projectsArray) {
		boolean lastLine = varStatus.isLast() && (projectsArray.length > 1) && (projectsArray[projectsArray.length - 1].length == 1);
		boolean oneDimenional = (projectsArray[0].length == 1);
		if (oneDimenional || lastLine) {
			return 1;
		}
		return 2;
	}
}
