package org.faulty.wpreplace.context;

import org.jline.utils.AttributedString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.stereotype.Component;

@Component
public class WpPromptProvider implements PromptProvider {

    @Autowired
    private MissionMizService missionContext;

    @Autowired
    private RouteContext routeContext;

    @Override
    public AttributedString getPrompt() {
        String prompt;
        if (missionContext.isMissionLoaded()) {
            if (routeContext.isRouteLoaded()) {
                prompt = "|" + missionContext.getMizFilePath() + "|R" + routeContext.getGroupId() + "| > ";
            } else {
                prompt = "|" + missionContext.getMizFilePath() + "| > ";
            }
        } else {
            prompt = "|NO MISSION LOADED| > "; 
        }  
        return new AttributedString(prompt);
    }

}