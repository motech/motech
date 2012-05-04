package org.motechproject.decisiontree.model;

/**
 * Contract for a tree command.
 */
public interface ITreeCommand {
    String[] execute(Object obj);
}
