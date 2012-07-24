package org.motechproject.decisiontree.model;

import org.codehaus.jackson.annotate.JsonTypeInfo;

/**
 * Represents a prompt.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public abstract class Prompt {

    private ITreeCommand command;
    private String name;

    public String getName() {
        return name;
    }

    public Prompt setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * Gets the command to be executed as part of prompt
     * @return ITreeCommand instance
     * @see ITreeCommand
     */
    public ITreeCommand getCommand() {
        return command;
    }

    public Prompt setCommand(ITreeCommand command) {
        this.command = command;
        return this;
    }

    @Override
    public String toString() {
        return "Prompt{" +
                "name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Prompt prompt = (Prompt) o;

        if (name != null ? !name.equals(prompt.name) : prompt.name != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
