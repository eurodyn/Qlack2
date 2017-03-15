/*
* Copyright 2014 EUROPEAN DYNAMICS SA <info@eurodyn.com>
*
* Licensed under the EUPL, Version 1.1 only (the "License").
* You may not use this work except in compliance with the Licence.
* You may obtain a copy of the Licence at:
* https://joinup.ec.europa.eu/software/page/eupl/licence-eupl
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the Licence is distributed on an "AS IS" basis,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the Licence for the specific language governing permissions and
* limitations under the Licence.
*/
package com.eurodyn.qlack2.common.util.util;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.beanutils.BeanUtils;

public class ListMapper {
	private static final Logger LOGGER = Logger.getLogger(ListMapper.class.getName());

	@SuppressWarnings("unchecked")
	public static <S, D> List<D> map(final List<S> source,	final Class<D> destType) {
		List<D> retVal = null;

		if (source != null) {
			retVal = new ArrayList<D>();
			if (source.size() > 0) {
				for (S element : source) {
					try {
						retVal.add((D)BeanUtils.cloneBean(element));
					} catch (IllegalAccessException | InstantiationException
							| InvocationTargetException | NoSuchMethodException e) {
						LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
					}
				}
			}
		}

		return retVal;
	}

	@SuppressWarnings("unchecked")
	public static <S, D> D map(final S source, final Class<D> destType) {
		D retVal = null;
		if (source != null) {
			try {
				retVal = (D)BeanUtils.cloneBean(source);
			} catch (IllegalAccessException | InstantiationException
					| InvocationTargetException | NoSuchMethodException e) {
				LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
			}
		}

		return retVal;
	}


}
