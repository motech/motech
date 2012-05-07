package org.motechproject.decisiontree.model;

/**
 * Contract for tree command.<br/>
 * Known use cases:<br/>
 * Call back from tree node, executed whenever tree node is processed. See also
 * {@link Node#setTreeCommands(ITreeCommand...)}
 * <br/>
 * Dynamic prompts by setting command on prompt, result of command will be used in prompt text. See also
 * {@link AudioPrompt#setCommand(ITreeCommand)},
 * {@link TextToSpeechPrompt#setCommand(ITreeCommand)}
 */
public interface ITreeCommand {
    String[] execute(Object obj);
}
