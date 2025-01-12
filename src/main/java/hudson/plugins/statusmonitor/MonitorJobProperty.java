package hudson.plugins.statusmonitor;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import hudson.model.Job;
import jenkins.model.OptionalJobProperty;
import org.kohsuke.stapler.DataBoundConstructor;

public class MonitorJobProperty extends OptionalJobProperty<Job<?, ?>> {

    @DataBoundConstructor
    public MonitorJobProperty() {
    }

    @Extension
    public static class DescriptorImpl extends OptionalJobPropertyDescriptor {

        @Override
        public boolean isApplicable(Class<? extends Job> jobType) {
            return true;
        }

        @NonNull
        @Override
        public String getDisplayName() {
            return "Show in Status Monitor";
        }
    }
}
