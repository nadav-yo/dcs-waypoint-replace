package org.faulty.wpreplace.commands;

import org.faulty.wpreplace.context.Error;
import org.faulty.wpreplace.context.MissionMizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.Availability;
import org.springframework.shell.component.PathInput;
import org.springframework.shell.component.PathSearch;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellMethodAvailability;

@ShellComponent
public class MissionCommand extends AbstractShellComponent {

    @Autowired
    private MissionMizService missionContext;

    @ShellMethod(key = "load", value = "Path input", group = "Components")
    public String loadMission() {
        PathSearch.PathSearchConfig config = new PathSearch.PathSearchConfig();
        config.setMaxPathsShow(10);
        config.setMaxPathsSearch(1000);
        config.setSearchForward(true);
        config.setSearchCaseSensitive(false);
        config.setSearchNormalize(false);

        PathSearch component = new PathSearch(getTerminal(), "Enter mission .miz location:", config);
        component.setResourceLoader(getResourceLoader());
        component.setTemplateExecutor(getTemplateExecutor());
        PathSearch.PathSearchContext context = component.run(PathSearch.PathSearchContext.empty());

        Error error = missionContext.loadMission(String.valueOf(context.getResultValue()));
        if (error != null) {
            return error.message();
        }
        return "Mission loaded";
    }

    @ShellMethodAvailability("isMissionLoaded")
    @ShellMethod(key = "save", value = "saves loaded mission to destFile")
    public String saveMission() {
        PathInput component = new PathInput(getTerminal(), "Enter value:");
        component.setResourceLoader(getResourceLoader());
        component.setTemplateExecutor(getTemplateExecutor());
        PathInput.PathInputContext context = component.run(PathInput.PathInputContext.empty());
        Error error = missionContext.saveMission(context.getResultValue().toString());
        if (error != null) {
            return error.message();
        }
        return "Mission saved";
    }

    private Availability isMissionLoaded() {
        return missionContext.isMissionLoaded() ? Availability.available()
                : Availability.unavailable("No mission loaded");
    }
}
