package org.shadow.skriva;

public class Configuration {
    private Action action;
    private String input;
    private String output = null;
    private boolean verbose;
    private String modernAlertCssPath;
    private String modernYesNoCssPath;
    private String modernSuccessCssPath;

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public String getModernAlertCssPath() {
        return modernAlertCssPath;
    }

    public void setModernAlertCssPath(String modernAlertCssPath) {
        this.modernAlertCssPath = modernAlertCssPath;
    }

    public void print() {
        System.out.println("Action:         " + Action.enumToName(action));
        System.out.println("Input file:     " + input);
        System.out.println("Output file:    " + output);
        System.out.println("CSS dir path:   " + modernAlertCssPath);
    }

    public String getModernYesNoCssPath() {
        return modernYesNoCssPath;
    }

    public void setModernYesNoCssPath(String modernYesNoCssPath) {
        this.modernYesNoCssPath = modernYesNoCssPath;
    }

    public String getModernSuccessCssPath() {
        return modernSuccessCssPath;
    }

    public void setModernSuccessCssPath(String modernSuccessCssPath) {
        this.modernSuccessCssPath = modernSuccessCssPath;
    }
}