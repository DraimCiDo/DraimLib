package me.draimlib.updater;

public enum VersionType {
    PREALPHA("pre-alpha", 0, false),
    ALPHA("alpha", 1, false),
    BETA("beta", 2, false),
    RC("rc", 3, false),
    STABLE("stable", 4, true),
    RELEASE("release", 5, true),
    SNAPSHOT("snapshot", 6, false);

    String name;
    int level;
    boolean stable;

    VersionType(String name, int level, boolean stable) {
        this.name = name;
        this.level = level;
        this.stable = stable;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public boolean isStable() {
        return stable;
    }
}
