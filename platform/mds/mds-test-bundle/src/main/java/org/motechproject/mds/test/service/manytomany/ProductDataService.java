package org.motechproject.mds.test.service.manytomany;

import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.test.domain.manytomany.Product;;


public interface ProductDataService extends MotechDataService<Product> {

    @Lookup
    Product findByProductName(@LookupField(name = "productName") String productName);
}
