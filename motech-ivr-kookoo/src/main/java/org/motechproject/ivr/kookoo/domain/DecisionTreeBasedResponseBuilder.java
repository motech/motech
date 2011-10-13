package org.motechproject.ivr.kookoo.domain;

import org.motechproject.decisiontree.model.*;
import org.motechproject.ivr.kookoo.KookooIVRResponseBuilder;

import java.util.List;

public class DecisionTreeBasedResponseBuilder {
    public KookooIVRResponseBuilder ivrResponse(Node node, Object customData, KookooIVRResponseBuilder ivrResponseBuilder, boolean retryOnIncorrectUserAction) {
        List<Prompt> prompts = node.getPrompts();
        for (Prompt prompt : prompts) {
            if (retryOnIncorrectUserAction && !(prompt instanceof MenuAudioPrompt) && prompt instanceof AudioPrompt) continue;
            ITreeCommand command = prompt.getCommand();
            boolean isAudioPrompt = prompt instanceof AudioPrompt;
            if (command == null) {
                buildPrompts(ivrResponseBuilder, prompt.getName(), isAudioPrompt);
            } else {
                String[] promptsFromCommand = command.execute(customData);
                for (String promptFromCommand : promptsFromCommand) {
                    buildPrompts(ivrResponseBuilder, promptFromCommand, isAudioPrompt);
                }
            }
        }
        if (node.hasTransitions()) {
            ivrResponseBuilder.collectDtmfLength(maxLenOfTransitionOptions(node));
        }
        return ivrResponseBuilder;
    }

    private int maxLenOfTransitionOptions(Node node) {
        int maxLen = 0;
        for (String key : node.getTransitions().keySet()) {
            if (maxLen < key.length()) maxLen = key.length();
        }
        return maxLen;
    }

    private void buildPrompts(KookooIVRResponseBuilder ivrResponseBuilder, String promptName, boolean isAudioPrompt) {
        if (isAudioPrompt) ivrResponseBuilder.withPlayAudios(promptName);
        else ivrResponseBuilder.withPlayTexts(promptName);
    }
}
