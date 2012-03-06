package hudson.plugins.statusmonitor;

import hudson.Extension;
import hudson.model.Result;
import hudson.model.RootAction;
import hudson.model.TopLevelItem;
import hudson.model.AbstractProject;
import hudson.model.Hudson;
import hudson.model.Run;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.jsp.jstl.core.LoopTagStatus;

import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

/**
 * Status Monitor, shows the configured Jobs in a single screen overview
 * 
 * @author Daniel Gal�n y Martins
 */
@ExportedBean(defaultVisibility = 999)
@Extension
public class MonitorAction implements RootAction {
	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(MonitorAction.class
			.getName());

	private static final int COLUMNS = 2;

	private static final int REFRESH_PAGE_TIME = 30;

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
				if (abstractProject.getPublishersList().get(
						MonitorPublisher.DESCRIPTOR) != null) {
					result.add(abstractProject);
				}
			}
		}

		return result;
	}

	public String getResult(AbstractProject project) {
		String result;
		if ((project.getLastCompletedBuild() != null)
				&& (project.getLastCompletedBuild().getResult() != null)) {
			if (project.isDisabled()) {
				result = "DISABLED";
			} else {
				result = project.getLastCompletedBuild().getResult().toString();
			}
		} else {
			result = "NOT_BUILD";
		}
		return result;
	}

	public boolean isClaim(AbstractProject project) {
		if ((project.getLastCompletedBuild() != null)
				&& (project.getLastCompletedBuild().getResult() != null)) {
			if (project.isDisabled()) {
				return false;
			}
			try {
				
				Object obj = getActionByClassName(project.getLastCompletedBuild(),"hudson.plugins.claim.ClaimBuildAction");
				
				return obj != null && isClaimed(obj);
			} catch (Exception ex) {
				LOGGER.log(Level.WARNING,"",ex);
				return false;
			}
		}
		return false;
	}

	private Object getActionByClassName(Run lastCompletedBuild, String clazzname) {
		for (Object obj :lastCompletedBuild.getActions()){
			if (obj.getClass().getName().equals(clazzname)){
				return obj;
			}
		}
		return null;
	}

	private boolean isClaimed(Object obj) {
		try {
			Method m = obj.getClass().getMethod("isClaimed", null);
			Object result = m.invoke(obj, null);
			return result != null && ((Boolean) result).booleanValue();
		} catch (Exception e) {
			LOGGER.log(Level.WARNING,"",e);
			return false;
		}
	}

	public String getSoundNotif(AbstractProject project) {
		String result = null;
		if ((project.getLastCompletedBuild() != null)
				&& (project.getLastCompletedBuild().getResult() != null)) {
			if (project.isDisabled()) {
				return null;
			}

			Run lastBuild = project.getLastCompletedBuild();
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.SECOND, -REFRESH_PAGE_TIME);
			Date endTime = new Date(lastBuild.getTime().getTime()
					+ lastBuild.getDuration());

			if (cal.getTime().equals(endTime) || cal.getTime().before(endTime)) {
				Run previousBuild = project.getLastCompletedBuild()
						.getPreviousBuild();

				if (!previousBuild.getResult().equals(lastBuild.getResult())
						&& (lastBuild.getResult().equals(Result.FAILURE) || lastBuild
								.getResult().equals(Result.SUCCESS))) {
					return lastBuild.getResult().toString();
				}
			}
		}
		return result;
	}

	private int getRows() {
		int size = getProjects().size();
		if (size <= 3) {
			return size;
		}
		return ((size % COLUMNS) == 0) ? (size / COLUMNS)
				: ((size + 1) / COLUMNS);
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
				} else {
					// last row and uneven
					if (((i + 1) == rows) && ((projects.size() % 2) != 0)) {
						row = new AbstractProject[1];
						row[0] = projects.get(i * COLUMNS);
					} else {
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
	public int getStyleId(LoopTagStatus varStatus,
			AbstractProject[][] projectsArray) {
		boolean lastLine = varStatus.isLast() && (projectsArray.length > 1)
				&& (projectsArray[projectsArray.length - 1].length == 1);
		boolean oneDimenional = (projectsArray[0].length == 1);
		if (oneDimenional || lastLine) {
			return 1;
		}
		return 2;
	}
}
