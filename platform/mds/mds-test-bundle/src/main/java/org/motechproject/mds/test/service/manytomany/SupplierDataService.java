package org.motechproject.mds.test.service.manytomany;

import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.test.domain.manytomany.Supplier;


public interface SupplierDataService extends MotechDataService<Supplier> {

    @Lookup
    Supplier findBySupplierName(@LookupField(name = "supplierName") String supplierName);
}
