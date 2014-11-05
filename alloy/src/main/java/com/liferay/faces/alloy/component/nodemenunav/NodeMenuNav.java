/**
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.alloy.component.nodemenunav;

import com.liferay.faces.util.component.ClientComponent;


/**
 * @author  Vernon Singleton
 */
public interface NodeMenuNav extends ClientComponent {

	// Public Constants
	public static final String BUTTON_PREFIX = "btn-";
	public static final String DEFAULT_BTN = "btn";
	public static final String DEFAULT_BUTTON = "btn-default";

	public String getClientKey();

	public void setClientKey(String clientKey);

	public boolean isDisabled();

	public String getFamily();

	public String getRendererType();

	public String getStyleClass();

	public Object getValue();
}
