/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2010-11 The Trustees of Columbia University in the City of
 * New York and Grameen Foundation USA.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. Neither the name of Grameen Foundation USA, Columbia University, or
 * their respective contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY GRAMEEN FOUNDATION USA, COLUMBIA UNIVERSITY
 * AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL GRAMEEN FOUNDATION
 * USA, COLUMBIA UNIVERSITY OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.motechproject.openmrs.advice;

import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.dao.PatientDao;
import org.motechproject.model.Rule;
import org.openmrs.Patient;
import org.openmrs.PersonAttribute;
import org.openmrs.api.context.Context;
import org.springframework.aop.AfterReturningAdvice;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * An OpenMRS AOP interceptor that enables us to perform various tasks upon a
 * patient being saved, whether that operation knows about it or not.
 */
public class PatientAdvice implements AfterReturningAdvice {

	private static Log log = LogFactory.getLog(PatientAdvice.class);

	/**
	 * @see org.springframework.aop.AfterReturningAdvice#afterReturning(java.lang.Object,
	 *      java.lang.reflect.Method, java.lang.Object[], java.lang.Object)
	 */
	public void afterReturning(Object returnValue, Method method,
			Object[] args, Object target) throws Throwable {

		String methodName = method.getName();

		if (methodName.equals("savePatient")) {

			log.debug("intercepting method invocation");

			Patient patient = (Patient) returnValue;
			
			PatientDao motechPatientDao = Context.getService(PatientDao.class);
			
			if (motechPatientDao != null) {
				
				org.motechproject.model.Patient mPatient = null;
				String id = patient.getUuid();
		        if (motechPatientDao.contains(id)) {
		        	mPatient = motechPatientDao.get(id);
		        } else {
		        	mPatient = new org.motechproject.model.Patient();
		        	mPatient.setId(id);
		        }
				
				PersonAttribute phoneAttribute = patient.getAttribute("Phone Number");
				if (phoneAttribute != null) {
					mPatient.setPhoneNumber(phoneAttribute.getValue());
				}
				//TODO: what else do we need to copy?
				
				if (mPatient.isNew()) {
					motechPatientDao.add(mPatient);
				} else {
					motechPatientDao.update(mPatient);
				}
				
				//TODO: transaction handling?
			}

			
		}
	}

}
