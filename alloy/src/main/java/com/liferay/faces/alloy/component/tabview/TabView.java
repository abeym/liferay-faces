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
package com.liferay.faces.alloy.component.tabview;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.faces.component.FacesComponent;
import javax.faces.component.behavior.AjaxBehavior;
import javax.faces.component.behavior.Behavior;
import javax.faces.component.behavior.ClientBehavior;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.event.FacesEvent;

import com.liferay.faces.alloy.component.tab.Tab;
import com.liferay.faces.alloy.component.tab.TabSelectEvent;
import com.liferay.faces.alloy.component.tab.TabUtil;
import com.liferay.faces.util.component.ComponentUtil;
import com.liferay.faces.util.helper.IntegerHelper;


/**
 * @author  Neil Griffin
 */
@FacesComponent(value = TabView.COMPONENT_TYPE)
public class TabView extends TabViewBase implements ClientBehaviorHolder {

	// Public Constants
	public static final String COMPONENT_TYPE = "com.liferay.faces.alloy.component.tabview.TabView";
	public static final String RENDERER_TYPE = "com.liferay.faces.alloy.component.tabview.internal.TabViewRenderer";
	public static final String STYLE_CLASS_NAME = "alloy-tab-view";
	public static final String SELECTED_INDEX = "selectedIndex";

	// Private Constants
	private static final Collection<String> EVENT_NAMES = Collections.unmodifiableCollection(Arrays.asList(
				TabSelectEvent.TAB_SELECT));

	public TabView() {
		super();
		setRendererType(RENDERER_TYPE);
	}

	@Override
	public void addClientBehavior(String eventName, ClientBehavior clientBehavior) {

		// If the specified client behavior is an Ajax behavior, then the alloy:tabView component tag has an f:ajax
		// child tag. Register a listener that can respond to the Ajax behavior by invoking the tabSelectListener that
		// may have been specified.
		if (clientBehavior instanceof AjaxBehavior) {
			AjaxBehavior ajaxBehavior = (AjaxBehavior) clientBehavior;
			ajaxBehavior.addAjaxBehaviorListener(new TabViewBehaviorListener());
		}

		super.addClientBehavior(eventName, clientBehavior);
	}

	@Override
	public void broadcast(FacesEvent event) throws AbortProcessingException {
		super.broadcast(event);
	}

	@Override
	public void queueEvent(FacesEvent facesEvent) {

		// This method is called by the AjaxBehavior renderer's decode() method. If the specified event is an ajax
		// behavior event that indicates a tab being collapsed/expanded, then
		if (facesEvent instanceof AjaxBehaviorEvent) {

			// Determine the client-side state of the selected tab index.
			FacesContext facesContext = FacesContext.getCurrentInstance();
			Map<String, String> requestParameterMap = facesContext.getExternalContext().getRequestParameterMap();
			String clientId = getClientId(facesContext);
			int selectedIndex = IntegerHelper.toInteger(requestParameterMap.get(clientId + SELECTED_INDEX));

			// If iterating over a data model, then determine the row data and tab associated with the data model
			// iteration.
			Object rowData = null;
			Tab tab = null;
			String var = getVar();

			if (var != null) {
				setRowIndex(selectedIndex);
				rowData = getRowData();
				tab = TabUtil.getFirstChildTab(this);
				setRowIndex(-1);
			}

			// Otherwise, determine the tab associated with the client-side state of the selected tab index.
			else {
				List<Tab> childTabs = TabUtil.getChildTabs(this);

				if (childTabs.size() >= (selectedIndex + 1)) {
					tab = childTabs.get(selectedIndex);
				}
			}

			// Queue an tabView tab event rather than the specified faces event.
			AjaxBehaviorEvent behaviorEvent = (AjaxBehaviorEvent) facesEvent;
			Behavior behavior = behaviorEvent.getBehavior();
			TabSelectEvent tabEvent = new TabSelectEvent(this, behavior, tab, rowData);
			super.queueEvent(tabEvent);
		}

		// Otherwise, queue the specified faces event.
		else {
			super.queueEvent(facesEvent);
		}
	}

	@Override
	public String getDefaultEventName() {
		return TabSelectEvent.TAB_SELECT;
	}

	@Override
	public Collection<String> getEventNames() {
		return EVENT_NAMES;
	}

	@Override
	public String getStyleClass() {

		// getStateHelper().eval(PropertyKeys.styleClass, null) is called because super.getStyleClass() may return the
		// STYLE_CLASS_NAME of the super class.
		String styleClass = (String) getStateHelper().eval(TabViewPropertyKeys.styleClass, null);

		return ComponentUtil.concatCssClasses(styleClass, STYLE_CLASS_NAME);
	}
}
