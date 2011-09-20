package org.motechproject.ivr.action;

import org.motechproject.decisiontree.model.Tree;
import org.motechproject.server.service.ivr.IVRContext;

public interface TreeChooser {
    Tree getTree(IVRContext ivrContext);
}
