package org.motechproject.mds.web.matcher;

import org.motechproject.mds.dto.EntityDto;

public class WIPEntityMatcher extends MdsMatcher<EntityDto> {

    public WIPEntityMatcher() {
        super(EntityDto.class);
    }

    @Override
    protected boolean match(EntityDto obj) {
        return obj.isDraft();
    }

}
