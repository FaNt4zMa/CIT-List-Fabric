package lc.cit.list;

import java.util.List;

public class BundleWrapper {
    public List<String> itemNames;
    public List<String> formatedStringLines;
    public List<String> toRenameTrigger;

    public BundleWrapper(List<String> itemNames, List<String> formatedStringLines, List<String> toRenameTrigger) {
        this.itemNames = itemNames;
        this.formatedStringLines = formatedStringLines;
        this.toRenameTrigger = toRenameTrigger;
    }
}
