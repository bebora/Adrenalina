package it.polimi.se2019.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SelectableOptions<T> implements Serializable {
    /**
     * Prompt that describes what the options are for
     */
    String prompt;
    /**
     * Options that can be selected
     */
    ArrayList<T> options;
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

    public String getNumericalCostraints(){
        StringBuilder string = new StringBuilder(String.format("You can select from a minimum of %d elements, to a maximum of %d elements.",minSelectables,maxSelectables));
        if (minSelectables == 0) {
            string.append(" Select 0 for empty list.");
        }
        return string.toString();
    }

    public void setOptions(ArrayList<T> options) {
        this.options = options;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public T getOption(int i){
        if(i > 0 && i<=options.size()){
            return options.get(i-1);
        }
        else
            return null;
    }

    public SelectableOptions(){
        this.maxSelectables = 0;
        this.minSelectables = 0;
        this.options = new ArrayList<>();
        this.prompt = null;
    }

    public SelectableOptions(List<T> options, int maxSelectables, int minSelectables, String prompt) {
        this.options = new ArrayList<>(options);
        if (maxSelectables == -1) {
            this.maxSelectables = options.size();
        }
        else {
            this.maxSelectables = maxSelectables;
        }
        this.minSelectables = minSelectables;
        this.prompt = prompt;
    }
    public SelectableOptions(SelectableOptions selectableOptions) {
        this.minSelectables = selectableOptions.getMinSelectables();
        this.maxSelectables = selectableOptions.getMaxSelectables();
        this.prompt = selectableOptions.getPrompt();
    }

    public boolean checkForCoherency(List<T> options) {
        if (options.size() < minSelectables || options.size() > maxSelectables)
            return false;
        for (T option : options)
            if (Collections.frequency(options, option) > Collections.frequency(this.options, option))
                return false;
        return true;

    }

}

