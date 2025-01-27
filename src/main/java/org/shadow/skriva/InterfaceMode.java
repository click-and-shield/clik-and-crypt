package org.shadow.skriva;

public enum InterfaceMode {
    Cli, Gui, Undefined;

    public static InterfaceMode nameToEnum(String name) {
        return switch (name.toLowerCase()) {
            case "gui" -> Gui;
            case "cli" -> Cli;
            default -> Undefined;
        };
    }

    public static String enumToName(InterfaceMode mode) {
        return switch (mode) {
            case Gui -> "gui";
            case Cli -> "cli";
            default -> "undefined";
        };
    }

    public static boolean isValidName(String name) {
        return nameToEnum(name) != Undefined;
    }
}
