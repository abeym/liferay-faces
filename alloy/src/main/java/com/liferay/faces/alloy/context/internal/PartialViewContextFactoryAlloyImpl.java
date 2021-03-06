/**
 * Copyright (c) 2000-2015 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package com.liferay.faces.alloy.context.internal;

import javax.faces.context.FacesContext;
import javax.faces.context.PartialViewContext;
import javax.faces.context.PartialViewContextFactory;

import com.liferay.faces.util.product.ProductConstants;
import com.liferay.faces.util.product.ProductMap;


/**
 * @author  Neil Griffin
 */
public class PartialViewContextFactoryAlloyImpl extends PartialViewContextFactory {

	// Private Constants
	private static final boolean LIFERAY_PORTAL_DETECTED = ProductMap.getInstance().get(ProductConstants.LIFERAY_PORTAL)
		.isDetected();
	private static final boolean LIFERAY_FACES_BRIDGE_DETECTED = ProductMap.getInstance().get(
			ProductConstants.LIFERAY_FACES_BRIDGE).isDetected();

	// Private Data Members
	private PartialViewContextFactory wrappedPartialViewContextFactory;

	public PartialViewContextFactoryAlloyImpl(PartialViewContextFactory partialViewContextFactory) {
		this.wrappedPartialViewContextFactory = partialViewContextFactory;
	}

	@Override
	public PartialViewContext getPartialViewContext(FacesContext facesContext) {

		PartialViewContext partialViewContext = wrappedPartialViewContextFactory.getPartialViewContext(facesContext);

		if (LIFERAY_PORTAL_DETECTED && LIFERAY_FACES_BRIDGE_DETECTED) {
			// Use the Bridge's PartialViewContext.
		}
		else {
			partialViewContext = new PartialViewContextAlloyImpl(partialViewContext);
		}

		return partialViewContext;
	}
}
