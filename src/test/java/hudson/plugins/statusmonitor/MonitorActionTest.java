package hudson.plugins.statusmonitor;

import hudson.matrix.MatrixProject;
import hudson.maven.MavenModuleSet;
import hudson.model.AbstractProject;
import hudson.model.FreeStyleProject;
import org.jvnet.hudson.test.HudsonTestCase;

import java.io.IOException;

public class MonitorActionTest
        extends HudsonTestCase
{
    private MonitorAction monitorAction;

    @Override
    protected void setUp() throws Exception
    {
        super.setUp();

        monitorAction = new MonitorAction();
    }

    public void testGetProjectsArray_shouldGetProjectFreeStyleProjectWithAMonitorPublisher()
            throws Exception
    {
        FreeStyleProject freeStyleProject = createFreeStyleProject();
        addMonitorPublisher(freeStyleProject);

        AbstractProject[][] projects = monitorAction.getProjectsArray();

        assertTrue("Free-style project should be in array since one of its publishers is a MonitorPublisher.",
                isInArray(projects, freeStyleProject));
    }

    public void testGetProjectsArray_shouldGetMavenProjectProjectWithAMonitorPublisher()
            throws Exception
    {
        MavenModuleSet mavenProject = createMavenProject();
        addMonitorPublisher(mavenProject);

        AbstractProject[][] projects = monitorAction.getProjectsArray();

        assertTrue("Maven project should be in array since one of its publishers is a MonitorPublisher.",
                isInArray(projects, mavenProject));
    }

    public void testGetProjectsArray_shouldGetMatrixProjectWithAMonitorPublisher()
            throws Exception
    {
        MatrixProject matrixProject = createMatrixProject();
        addMonitorPublisher(matrixProject);

        AbstractProject[][] projects = monitorAction.getProjectsArray();

        assertTrue("Matrix project should be in array since one of its publishers is a MonitorPublisher.",
                isInArray(projects, matrixProject));
    }

    public void testGetProjectsArray_shouldNotGetProjectsWithoutAMonitorPublisher()
            throws Exception
    {
        FreeStyleProject freeStyleProject = createFreeStyleProject();

        AbstractProject[][] projects = monitorAction.getProjectsArray();
        
        assertFalse("There should not be any projects in the array since none of the projects have a MonitorPublisher.",
                isInArray(projects, freeStyleProject));
    }

    public void testGetProjectsArray_shouldGetMultipleProjectsWhenMultipleProjectsWithMonitorPublishersFound()
            throws Exception
    {
        FreeStyleProject freeStyleProject = createFreeStyleProject();
        addMonitorPublisher(freeStyleProject);
        MatrixProject matrixProject = createMatrixProject();
        addMonitorPublisher(matrixProject);

        AbstractProject[][] projects = monitorAction.getProjectsArray();

        assertTrue("freeStyleProject should be in array since one of its publishers is a MonitorPublisher.",
                isInArray(projects, freeStyleProject));
        assertTrue("matrixProject should be in array since one of its publishers is a MonitorPublisher.",
                isInArray(projects, matrixProject));
    }

    public void testGetProjectsArray_shouldBeEmptyArrayWhenNoPojrectsWithMonitorPublishersFound()
    {
        AbstractProject[][] projects = monitorAction.getProjectsArray();

        assertEquals(0, projects.length);
    }

    public void testGetProjectsArray_shouldBe1Column3RowsWhenThereAreLessThan3ProjectsWithMonitorPublishersFound()
            throws Exception
    {
        addMonitorPublisher(createFreeStyleProject());
        addMonitorPublisher(createFreeStyleProject());
        addMonitorPublisher(createFreeStyleProject());

        AbstractProject[][] projects = monitorAction.getProjectsArray();

        assertEquals("Should have 3 rows of projects with MonitorPublisher publishers.", 3, projects.length);
        assertEquals("Row should have one project.", 1, projects[0].length);
        assertEquals("Row should have one project.", 1, projects[1].length);
        assertEquals("Row should have one project.", 1, projects[2].length);
    }

    public void testGetProjectsArray_shouldHave3RowsAnd2ColumnsOfProjectsWhenThereAreMoreThan3ProjectsWithMonitorPublishersFound()
            throws Exception
    {
        // 7 projects
        addMonitorPublisher(createFreeStyleProject());
        addMonitorPublisher(createFreeStyleProject());
        addMonitorPublisher(createFreeStyleProject());
        addMonitorPublisher(createFreeStyleProject());
        addMonitorPublisher(createFreeStyleProject());
        addMonitorPublisher(createFreeStyleProject());
        addMonitorPublisher(createFreeStyleProject());

        AbstractProject[][] projects = monitorAction.getProjectsArray();

        assertEquals("Should have 3 rows of projects with MonitorPublisher publishers.", 4, projects.length);
        assertEquals("Row should have two projects.", 2, projects[0].length);
        assertEquals("Row should have two projects.", 2, projects[1].length);
        assertEquals("Row should have two projects.", 2, projects[2].length);
        assertEquals("Row should have one project by itself.", 1, projects[3].length);
    }

    public void testGetProjectsArray_shouldHaveLastProjectByItselfInARowWhenMoreThan3ProjectsWithMonitorPublishersFound()
            throws Exception
    {
        // 6 projects
        addMonitorPublisher(createFreeStyleProject());
        addMonitorPublisher(createFreeStyleProject());
        addMonitorPublisher(createFreeStyleProject());
        addMonitorPublisher(createFreeStyleProject());
        addMonitorPublisher(createFreeStyleProject());
        addMonitorPublisher(createFreeStyleProject());

        AbstractProject[][] projects = monitorAction.getProjectsArray();

        assertEquals("Should have 3 rows of projects with MonitorPublisher publishers.", 3, projects.length);
        assertEquals("Row should have one project.", 2, projects[0].length);
        assertEquals("Row should have one project.", 2, projects[1].length);
        assertEquals("Row should have one project.", 2, projects[2].length);
    }

    private void addMonitorPublisher(AbstractProject project)
            throws IOException
    {
        //noinspection unchecked
        project.getPublishersList().add(new MonitorPublisher());
    }

    public boolean isInArray(AbstractProject[][] projects, AbstractProject expectedProject)
    {
        for (AbstractProject[] rowOfProjects : projects)
        {
            for (AbstractProject project : rowOfProjects)
            {
                if (expectedProject == project)
                {
                    return true;
                }
            }
        }
        return false;
    }
}
