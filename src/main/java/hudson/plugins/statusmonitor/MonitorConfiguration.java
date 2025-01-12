package hudson.plugins.statusmonitor;

import edu.umd.cs.findbugs.annotations.NonNull;
import hudson.Extension;
import jenkins.appearance.AppearanceCategory;
import jenkins.model.GlobalConfiguration;
import jenkins.model.GlobalConfigurationCategory;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.DataBoundSetter;

@Extension
public class MonitorConfiguration extends GlobalConfiguration {

    private int columns = 2;

    @DataBoundConstructor
    public MonitorConfiguration() {
        load();
    }

    @NonNull
    @Override
    public GlobalConfigurationCategory getCategory() {
        return GlobalConfigurationCategory.get(AppearanceCategory.class);
    }


    public int getColumns() {
        return columns;
    }

    @DataBoundSetter
    public void setColumns(int columns) {
        if (columns < 1) {
            columns = 1;
        }
        if (columns > 5) {
            //columns = 5;
        }
        this.columns = columns;
        save();
    }
}
