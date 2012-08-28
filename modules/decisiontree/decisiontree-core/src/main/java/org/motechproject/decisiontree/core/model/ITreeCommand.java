package org.motechproject.decisiontree.core.model;

import org.codehaus.jackson.annotate.JsonTypeInfo;

/**
 * Contract for tree command.<br/>
 * Known use cases:<br/>
 * Call back from tree node, executed whenever tree node is processed. See also
 * {@link Node#getTreeCommands()}
 * <br/>
 * Dynamic prompts by setting command on prompt, result of command will be used in prompt text. See also
 * {@link AudioPrompt#setCommand(ITreeCommand)},
 * {@link TextToSpeechPrompt#setCommand(ITreeCommand)}
 */
@JsonTypeInfo(include = JsonTypeInfo.As.PROPERTY, use = JsonTypeInfo.Id.CLASS, property = "@type")
public interface ITreeCommand {
    String[] execute(Object obj);
}
