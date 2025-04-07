package hudson.plugins.statusmonitor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.not;

import hudson.matrix.MatrixProject;
import hudson.maven.MavenModuleSet;
import hudson.model.FreeStyleProject;
import hudson.model.Job;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.junit.jupiter.WithJenkins;

@WithJenkins
class MonitorActionTest
{
    private JenkinsRule jenkins;

    private MonitorAction monitorAction;

    @BeforeEach
    void setUp(JenkinsRule rule)
    {
        jenkins = rule;
        monitorAction = new MonitorAction();
    }

    @Test
    void testGetProjectsArray_shouldGetProjectFreeStyleProjectWithAMonitorPublisher()
            throws Exception
    {
        FreeStyleProject freeStyleProject = jenkins.createFreeStyleProject();
        addMonitorJobProperty(freeStyleProject);

        List<Job<?, ?>> projects = monitorAction.getProjects();

        assertThat("Free-style project should be in array since JobProperty is set.",
                projects, contains(freeStyleProject));
    }

    @Test
    void testGetProjectsArray_shouldGetMavenProjectProjectWithAMonitorPublisher()
            throws Exception
    {
        MavenModuleSet mavenProject = jenkins.createProject(MavenModuleSet.class, "p");
        addMonitorJobProperty(mavenProject);

        List<Job<?, ?>> projects = monitorAction.getProjects();

        assertThat("Maven project should be in array since JobProperty is set.",
                projects, contains(mavenProject));
    }

    @Test
    void testGetProjectsArray_shouldGetMatrixProjectWithAMonitorPublisher()
            throws Exception
    {
        MatrixProject matrixProject = jenkins.createProject(MatrixProject.class, "p");
        addMonitorJobProperty(matrixProject);

        List<Job<?, ?>> projects = monitorAction.getProjects();

        assertThat("Matrix project should be in array since JobProperty is set.",
                projects, contains(matrixProject));
    }

    @Test
    void testGetProjectsArray_shouldNotGetProjectsWithoutAMonitorPublisher()
            throws Exception
    {
        FreeStyleProject freeStyleProject = jenkins.createFreeStyleProject();

        List<Job<?, ?>> projects = monitorAction.getProjects();

        assertThat("There should not be any projects in the array since none of the projects have a MonitorPublisher.",
                projects, not(contains(freeStyleProject)));
    }

    @Test
    void testGetProjectsArray_shouldGetMultipleProjectsWhenMultipleProjectsWithMonitorPublishersFound()
            throws Exception
    {
        FreeStyleProject freeStyleProject = jenkins.createFreeStyleProject();
        addMonitorJobProperty(freeStyleProject);
        MatrixProject matrixProject = jenkins.createProject(MatrixProject.class, "p");
        addMonitorJobProperty(matrixProject);

        List<Job<?, ?>> projects = monitorAction.getProjects();

        assertThat("freeStyleProject and matrixProject should be in array since one of its publishers is a MonitorPublisher.",
                projects, containsInAnyOrder(freeStyleProject, matrixProject));
    }

    @Test
    void testGetProjectsArray_shouldBeEmptyArrayWhenNoProjectsWithMonitorJobPropertyFound()
    {
        List<Job<?, ?>> projects = monitorAction.getProjects();

        assertThat("Projects size should be 0", projects, empty());
    }

    private static void addMonitorJobProperty(Job<?, ?> project)
            throws IOException
    {
        project.addProperty(new MonitorJobProperty());
    }

}
