package org.shadow.skriva;

public enum Action {
    Encrypt, Decrypt, Undefined;

    public static Action nameToEnum(String name) {
        return switch (name.toLowerCase()) {
            case "encrypt" -> Encrypt;
            case "decrypt" -> Decrypt;
            default -> Undefined;
        };
    }

    public static String enumToName(Action action) {
        return switch (action) {
            case Encrypt -> "encrypt";
            case Decrypt -> "decrypt";
            default -> "undefined";
        };
    }

    public static boolean isValidName(String name) {
        return nameToEnum(name) != Undefined;
    }
}
