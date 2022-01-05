package fr.asvadia.achat.utils.File;

public enum Files {
    Config("config"),
    Players("players");

    private final String name;

    Files(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
