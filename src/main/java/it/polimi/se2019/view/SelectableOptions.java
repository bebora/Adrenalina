package it.polimi.se2019.view;

import java.util.ArrayList;
import java.util.List;

public class SelectableOptions<T> {
    /**
     * Prompt that describes what the options are for
     */
    String prompt;
    /**
     * Options that can be selected
     */
    List<T> options;
    /**
     * Minimun number of options that must be selected
     */
    private int minSelectables;
    /**
     * Maximum number of options that can be selected
     */
    private int maxSelectables;

    public int getMaxSelectables() {
        return maxSelectables;
    }

    public int getMinSelectables() {
        return minSelectables;
    }

    public List<T> getOptions() {
        return options;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setMaxSelectables(int maxSelectables) {
        this.maxSelectables = maxSelectables;
    }

    public void setMinSelectables(int minSelectables) {
        this.minSelectables = minSelectables;
    }

    public void setOptions(List<T> options) {
        this.options = options;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public SelectableOptions(){
        this.maxSelectables = 0;
        this.minSelectables = 0;
        this.options = new ArrayList<>();
        this.prompt = null;
    }
}
