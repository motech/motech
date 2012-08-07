package org.motechproject.decisiontree.it;

import org.motechproject.decisiontree.model.ITreeCommand;

class TestCommand implements ITreeCommand {
    @Override
    public String[] execute(Object obj) {
        final String[] result = new String[1];
        result[0] = "ok";
        return result;
    }

}
