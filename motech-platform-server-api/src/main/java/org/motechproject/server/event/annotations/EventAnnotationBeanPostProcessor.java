/**
 * MOTECH PLATFORM OPENSOURCE LICENSE AGREEMENT
 *
 * Copyright (c) 2011 Grameen Foundation USA.  All rights reserved.
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
 * 3. Neither the name of Grameen Foundation USA, nor its respective contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY GRAMEEN FOUNDATION USA AND ITS CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL GRAMEEN FOUNDATION USA OR ITS CONTRIBUTORS
 * BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 */
package org.motechproject.server.event.annotations;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.motechproject.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.ReflectionUtils;

/**
 * Responsible for registering handlers based on annotations
 * @author yyonkov
 *
 */
public class EventAnnotationBeanPostProcessor implements BeanPostProcessor {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessBeforeInitialization(java.lang.Object, java.lang.String)
	 */
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		return bean;
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessAfterInitialization(java.lang.Object, java.lang.String)
	 */
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		processAnnotations(bean,beanName);
		return bean;
	}
	private void processAnnotations(final Object bean, final String beanName) {
		ReflectionUtils.doWithMethods(bean.getClass(), new ReflectionUtils.MethodCallback() {
			
			@Override
			public void doWith(Method method) throws IllegalArgumentException,IllegalAccessException {
				MotechListener annotation = method.getAnnotation(MotechListener.class);
				if(annotation!=null) {
					final List<String> subjects = Arrays.asList(annotation.subjects());
					MotechListenerAbstractProxy proxy = null;
					switch (annotation.type()) {
					case ORDERED_PARAMETERS:
						proxy = new MotechListenerOrderedParametersProxy(beanName, bean, method);
						break;
					case MOTECH_EVENT:
						proxy = new MotechListenerEventProxy(beanName, bean, method);
						break;
					case NAMED_PARAMETERS:
						proxy = new MotechListenerNamedParametersProxy(beanName, bean, method);
						break;
					}
					logger.info(String.format("Registering listener type(%20s) bean: %s , method: %s, for subjects: %s",annotation.type().toString(), beanName, method.toGenericString(), subjects));
					Context.getInstance().getEventListenerRegistry().registerListener(proxy, subjects);
				}
			}
		});
	}

	/**
	 * Registers event handlers (hack because we are running spring embedded in an OSGi module)
	 * @param applicationContext
	 */
	public static void registerHandlers(Map<String, Object> beans) {
		EventAnnotationBeanPostProcessor processor = new EventAnnotationBeanPostProcessor();
		for (Map.Entry<String, Object> entry : beans.entrySet()) {
			processor.postProcessAfterInitialization(entry.getValue(), entry.getKey());
		}
	}
}
