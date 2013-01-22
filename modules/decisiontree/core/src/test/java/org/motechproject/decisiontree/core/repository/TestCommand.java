package org.motechproject.decisiontree.core.repository;

import org.motechproject.decisiontree.core.model.ITreeCommand;

class TestCommand implements ITreeCommand {
    @Override
    public String[] execute(Object obj) {
        final String[] result = new String[1];
        result[0] = "ok";
        return result;
    }

}
