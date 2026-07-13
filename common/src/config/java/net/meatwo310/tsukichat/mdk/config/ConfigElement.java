package net.meatwo310.tsukichat.mdk.config;

interface ConfigElement {
    void bindTo(ConfigVisitor visitor);
}
