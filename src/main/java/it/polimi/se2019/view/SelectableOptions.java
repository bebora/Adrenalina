package it.polimi.se2019.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility class representing selectable options.
 * It supports:
 * <li>A number of selectable options</li>
 * <li>A list of selectable options</li>
 * @param <T> the type of the option
 */
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

    /**
     * Get the minimum and the maximum number of options selectable, in a printable String.
     * @return a String to show to the client, showing costraints
     */
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

    /**
     * @param options list of options that can be chosen
     * @param maxSelectables max number of option to choose (if 0, it gets converted to the list size)
     * @param minSelectables min number of option to choose
     * @param prompt String to show to the client, indicating the purpose of the Options
     */
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

    /**
     * Check if {@code options} are acceptable
     * @param options chosen by the client
     * @return whether the options are correct or not
     */
    public boolean checkForCoherency(List<T> options) {
        if (options.size() < minSelectables || options.size() > maxSelectables)
            return false;
        for (T option : options)
            if (Collections.frequency(options, option) > Collections.frequency(this.options, option))
                return false;
        return true;

    }

}

