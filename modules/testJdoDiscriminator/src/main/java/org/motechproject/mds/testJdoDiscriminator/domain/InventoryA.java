/**********************************************************************
Copyright (c) 2011 Andy Jefferson and others. All rights reserved.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Contributors:
    ...
**********************************************************************/
package org.motechproject.mds.testJdoDiscriminator.domain;

import java.util.HashSet;
import java.util.Set;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.PrimaryKey;

/**
 * Definition of an InventoryA of product1s.
 */
import org.motechproject.mds.annotations.Discriminated;
import org.motechproject.mds.annotations.Entity;
@Entity
public class InventoryA
{
    protected String naame=null;

    public InventoryA(String naame)
    {
        this.naame = naame;
    }

    public String getNaame()
    {
        return naame;
    }


    public String toString()
    {
        return "InventoryA : " + naame;
    }
}