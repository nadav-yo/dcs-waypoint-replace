package org.faulty.wpreplace.commands;

import java.util.List;
import java.util.Optional;

import org.springframework.shell.component.MultiItemSelector;
import org.springframework.shell.component.SingleItemSelector;
import org.springframework.shell.component.StringInput;
import org.springframework.shell.component.support.Itemable;
import org.springframework.shell.component.support.SelectorItem;
import org.springframework.shell.standard.AbstractShellComponent;

public abstract class BaseCommand extends AbstractShellComponent {
    private final List<SelectorItem<String>> unitTypes = List.of(
            SelectorItem.of("planes", "plane"),
            SelectorItem.of("helicopters", "helicopter"));
    private final List<SelectorItem<String>> coalitions = List.of(
            SelectorItem.of("blue", "blue"),
            SelectorItem.of("red", "red"));

    String getStringInput(String prompt, String defaultValue) {
        StringInput StringComponent = new StringInput(getTerminal(), prompt, defaultValue);
        StringComponent.setResourceLoader(getResourceLoader());
        StringComponent.setTemplateExecutor(getTemplateExecutor());
        StringInput.StringInputContext context = StringComponent.run(StringInput.StringInputContext.empty());
        return context.getResultValue();
    }

    int getIntInput(String prompt, String defaultValue) {
        try {
            return Integer.parseInt(getStringInput(prompt, defaultValue));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    String getSingleUnitType() {
        SingleItemSelector<String, SelectorItem<String>> component = new SingleItemSelector<>(getTerminal(),
                unitTypes, "Unit Type: ", null);
        component.setResourceLoader(getResourceLoader());
        component.setTemplateExecutor(getTemplateExecutor());
        SingleItemSelector.SingleItemSelectorContext<String, SelectorItem<String>> context = component
                .run(SingleItemSelector.SingleItemSelectorContext.empty());
        return context.getResultItem().flatMap(si -> Optional.ofNullable(si.getItem())).orElse(null);
    }

    String getCoalition() {
        SingleItemSelector<String, SelectorItem<String>> component = new SingleItemSelector<>(getTerminal(),
                coalitions, "Coalition: ", null);
        component.setResourceLoader(getResourceLoader());
        component.setTemplateExecutor(getTemplateExecutor());
        SingleItemSelector.SingleItemSelectorContext<String, SelectorItem<String>> context = component
                .run(SingleItemSelector.SingleItemSelectorContext.empty());
        return context.getResultItem().flatMap(si -> Optional.ofNullable(si.getItem())).orElse(null);
    }

     List<String> selectMultipleUnitTypes() {
        MultiItemSelector<String, SelectorItem<String>> component = new MultiItemSelector<>(getTerminal(),
                unitTypes, "Select types: ", null);
        component.setResourceLoader(getResourceLoader());
        component.setTemplateExecutor(getTemplateExecutor());
        MultiItemSelector.MultiItemSelectorContext<String, SelectorItem<String>> context = component
                .run(MultiItemSelector.MultiItemSelectorContext.empty());
        return context.getResultItems().stream()
                .map(Itemable::getItem)
                .toList();
    }

}
