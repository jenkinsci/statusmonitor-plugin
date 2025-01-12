package hudson.plugins.statusmonitor;

import static org.hamcrest.MatcherAssert.assertThat;

import hudson.matrix.MatrixProject;
import hudson.maven.MavenModuleSet;
import hudson.model.FreeStyleProject;
import hudson.model.Job;
import java.util.List;
import org.junit.Before;
import org.junit.Rule;

import java.io.IOException;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

public class MonitorActionTest
{
    @Rule
    public JenkinsRule jenkins = new JenkinsRule();

    private MonitorAction monitorAction;

    @Before
    public void setUp() throws Exception
    {
        monitorAction = new MonitorAction();
    }

    @Test
    public void testGetProjectsArray_shouldGetProjectFreeStyleProjectWithAMonitorPublisher()
            throws Exception
    {
        FreeStyleProject freeStyleProject = jenkins.createFreeStyleProject();
        addMonitorJobProperty(freeStyleProject);

        List<Job<?, ?>> projects = monitorAction.getProjects();

        assertThat("Free-style project should be in array since JobProperty is set.",
                isInArray(projects, freeStyleProject));
    }

    @Test
    public void testGetProjectsArray_shouldGetMavenProjectProjectWithAMonitorPublisher()
            throws Exception
    {
        MavenModuleSet mavenProject = jenkins.createProject(MavenModuleSet.class, "p");
        addMonitorJobProperty(mavenProject);

        List<Job<?, ?>> projects = monitorAction.getProjects();

        assertThat("Maven project should be in array since JobProperty is set.",
                isInArray(projects, mavenProject));
    }

    @Test
    public void testGetProjectsArray_shouldGetMatrixProjectWithAMonitorPublisher()
            throws Exception
    {
        MatrixProject matrixProject = jenkins.createProject(MatrixProject.class, "p");
        addMonitorJobProperty(matrixProject);

        List<Job<?, ?>> projects = monitorAction.getProjects();

        assertThat("Matrix project should be in array since JobProperty is set.",
                isInArray(projects, matrixProject));
    }

    @Test
    public void testGetProjectsArray_shouldNotGetProjectsWithoutAMonitorPublisher()
            throws Exception
    {
        FreeStyleProject freeStyleProject = jenkins.createFreeStyleProject();

        List<Job<?, ?>> projects = monitorAction.getProjects();
        
        assertThat("There should not be any projects in the array since none of the projects have a MonitorPublisher.",
                !isInArray(projects, freeStyleProject));
    }

    @Test
    public void testGetProjectsArray_shouldGetMultipleProjectsWhenMultipleProjectsWithMonitorPublishersFound()
            throws Exception
    {
        FreeStyleProject freeStyleProject = jenkins.createFreeStyleProject();
        addMonitorJobProperty(freeStyleProject);
        MatrixProject matrixProject = jenkins.createProject(MatrixProject.class, "p");
        addMonitorJobProperty(matrixProject);

        List<Job<?, ?>> projects = monitorAction.getProjects();

        assertThat("freeStyleProject should be in array since one of its publishers is a MonitorPublisher.",
                isInArray(projects, freeStyleProject));
        assertThat("matrixProject should be in array since one of its publishers is a MonitorPublisher.",
                isInArray(projects, matrixProject));
    }

    @Test
    public void testGetProjectsArray_shouldBeEmptyArrayWhenNoProjectsWithMonitorJobPropertyFound()
    {
        List<Job<?, ?>> projects = monitorAction.getProjects();

        assertThat("Projects size should be 0", projects.isEmpty());
    }

    private void addMonitorJobProperty(Job<?, ?> project)
            throws IOException
    {
        project.addProperty(new MonitorJobProperty());
    }

    public boolean isInArray(List<Job<?, ?>> projects, Job<?, ?> expectedProject)
    {
        for (Job<?, ?> project : projects)
        {
            if (expectedProject == project)
            {
                return true;
            }
        }
        return false;
    }
}
