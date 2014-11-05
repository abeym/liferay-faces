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
package com.liferay.faces.alloy.component.inputdate.internal;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.UIComponent;
import javax.faces.component.behavior.ClientBehavior;
import javax.faces.component.behavior.ClientBehaviorContext;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.render.FacesRenderer;

import com.liferay.faces.alloy.component.inputdate.DateSelectEvent;
import com.liferay.faces.alloy.component.inputdate.InputDate;
import com.liferay.faces.alloy.component.inputdatetime.InputDateTimeUtil;
import com.liferay.faces.alloy.component.inputdatetime.internal.InputDateTimeResponseWriter;
import com.liferay.faces.alloy.component.inputtext.InputText;
import com.liferay.faces.util.client.BrowserSniffer;
import com.liferay.faces.util.client.BrowserSnifferFactory;
import com.liferay.faces.util.component.ComponentUtil;
import com.liferay.faces.util.factory.FactoryExtensionFinder;
import com.liferay.faces.util.js.JavaScriptFragment;
import com.liferay.faces.util.lang.StringPool;
import com.liferay.faces.util.render.internal.RendererUtil;


/**
 * @author  Kyle Stiemann
 */
//J-
@FacesRenderer(componentFamily = InputDate.COMPONENT_FAMILY, rendererType = InputDate.RENDERER_TYPE)
@ResourceDependencies(
	{
		@ResourceDependency(library = "liferay-faces-alloy", name = "alloy.css"),
		@ResourceDependency(library = "liferay-faces-reslib", name = "build/aui-css/css/bootstrap.min.css"),
		@ResourceDependency(library = "liferay-faces-reslib", name = "build/aui/aui-min.js"),
		@ResourceDependency(library = "liferay-faces-reslib", name = "liferay.js")
	}
)
//J+
public class InputDateRenderer extends InputDateRendererBase {

	// This is a javascript function that sets the value of the input to the new date. It is used when
	// showOn="button".
	private static final String BUTTON_ON_DATE_CLICK_TEMPLATE = "var input=A.one('#{0}');" +
		"input.set('value',A.Date.format(event.date,{format:'{1}'}));";

	// Private Constants used in getMaskFromDatePattern()
	private static final String REGEX_TOKEN = "\\{0\\}";

	protected static String getMaskFromDatePattern(String datePattern) {

		String mask = datePattern;

		mask = mask.replaceAll("%", "%%");
		mask = mask.replaceAll(StringPool.NEW_LINE, "%n");
		mask = mask.replaceAll(StringPool.TAB, "%t");

		mask = mask.replaceAll("yyyy", "%Y");
		mask = mask.replaceAll("yy", "%y");
		mask = mask.replaceAll("MMMMM", "%B");
		mask = mask.replaceAll("MMM", "%b");
		mask = mask.replaceAll("MM", "%m");
		mask = mask.replaceAll("M", "%m");

		// Replace "dd" with a token (which doesn't contain "d"), so "%d" won't be replaced when we call
		// replaceAll("d", "%e").
		mask = mask.replaceAll("dd", REGEX_TOKEN);
		mask = mask.replaceAll("d", "%e");
		mask = mask.replaceAll(REGEX_TOKEN, "%d");
		mask = mask.replaceAll("DDD", "%j");
		mask = mask.replaceAll("FF", "%u");
		mask = mask.replaceAll("EEEE", "%A");
		mask = mask.replaceAll("EEE", "%a");

		return mask;
	}

	@Override
	public void encodeJavaScriptCustom(FacesContext facesContext, UIComponent uiComponent) throws IOException {

		BrowserSnifferFactory browserSnifferFactory = (BrowserSnifferFactory) FactoryExtensionFinder.getFactory(
				BrowserSnifferFactory.class);
		BrowserSniffer browserSniffer = browserSnifferFactory.getBrowserSniffer(facesContext.getExternalContext());
		InputDate inputDate = (InputDate) uiComponent;

		if (browserSniffer.isMobile() && inputDate.isResponsive()) {

			String clientVarName = ComponentUtil.getClientVarName(facesContext, inputDate);
			String clientKey = inputDate.getClientKey();

			if (clientKey == null) {
				clientKey = clientVarName;
			}

			JavaScriptFragment liferayComponent = new JavaScriptFragment("Liferay.component('" + clientKey + "')");
			String clientId = uiComponent.getClientId(facesContext);
			String inputClientId = clientId.concat(INPUT_SUFFIX);

			Object maxDateObject = inputDate.getMaxDate();
			Object minDateObject = inputDate.getMinDate();
			String maxDateString = null;
			String minDateString = null;

			if ((maxDateObject != null) || (minDateObject != null)) {

				String datePattern = inputDate.getDatePattern();
				String timeZoneString = inputDate.getTimeZone();
				TimeZone timeZone = TimeZone.getTimeZone(timeZoneString);
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(InputDate.DEFAULT_HTML5_DATE_PATTERN,
						Locale.ENGLISH);

				if (maxDateObject != null) {
					Date maxDate = InputDateTimeUtil.getObjectAsDate(maxDateObject, datePattern, timeZone);
					maxDateString = simpleDateFormat.format(maxDate);
				}

				if (maxDateObject != null) {
					Date minDate = InputDateTimeUtil.getObjectAsDate(minDateObject, datePattern, timeZone);
					minDateString = simpleDateFormat.format(minDate);
				}
			}

			ResponseWriter responseWriter = facesContext.getResponseWriter();
			RendererUtil.encodeFunctionCall(responseWriter, "LFAI.initDateTimePickerMobile", liferayComponent,
				inputClientId, maxDateString, minDateString);
		}
	}

	protected void encodeCalendar(FacesContext facesContext, ResponseWriter responseWriter, InputDate inputDate,
		boolean first) throws IOException {

		// The calendar attribute value provides the opportunity to specify dateClick events, minDate, maxDate as
		// key:value pairs via JSON syntax. For example: "calendar: {minDate: new Date(2015,1,1,0,0,0,0)}"
		boolean calendarFirst = true;

		encodeNonEscapedObject(responseWriter, "calendar", StringPool.BLANK, first);
		responseWriter.write(StringPool.OPEN_CURLY_BRACE);

		Object maxDateObject = inputDate.getMaxDate();

		if (maxDateObject != null) {

			encodeDate(responseWriter, inputDate, "maximumDate", maxDateObject, calendarFirst);
			calendarFirst = false;
		}

		Object minDateObject = inputDate.getMinDate();

		if (minDateObject != null) {

			encodeDate(responseWriter, inputDate, "minimumDate", minDateObject, calendarFirst);
			calendarFirst = false;
		}

		String dateSelectClientBehaviorScript = null;
		Map<String, List<ClientBehavior>> clientBehaviorMap = inputDate.getClientBehaviors();
		Collection<String> eventNames = inputDate.getEventNames();

		for (String eventName : eventNames) {

			if (DateSelectEvent.DATE_SELECT.equals(eventName)) {

				List<ClientBehavior> clientBehaviorsForEvent = clientBehaviorMap.get(eventName);

				if (clientBehaviorsForEvent != null) {

					for (ClientBehavior clientBehavior : clientBehaviorsForEvent) {

						String clientId = inputDate.getClientId(facesContext);
						ClientBehaviorContext clientBehaviorContext = ClientBehaviorContext.createClientBehaviorContext(
								facesContext, inputDate, eventName, clientId, null);
						dateSelectClientBehaviorScript = clientBehavior.getScript(clientBehaviorContext);
					}
				}

				break;
			}
		}

		String showOn = inputDate.getShowOn();

		if ("button".equals(showOn) || (dateSelectClientBehaviorScript != null)) {

			encodeNonEscapedObject(responseWriter, "on", StringPool.BLANK, calendarFirst);
			responseWriter.write(StringPool.OPEN_CURLY_BRACE);

			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("function(event){if(this._canBeSelected(event.date)){");

			if ("button".equals(showOn)) {

				// Each time a date is clicked, the input must be updated via javascript because the datePicker is not
				// attached to the input.
				String clientId = inputDate.getClientId(facesContext);
				String escapedInputClientId = RendererUtil.escapeClientId(clientId.concat(INPUT_SUFFIX));
				String buttonOnDateClick = BUTTON_ON_DATE_CLICK_TEMPLATE.replace("{0}", escapedInputClientId);
				String datePattern = inputDate.getDatePattern();
				String mask = getMaskFromDatePattern(datePattern);
				String onDateClick = buttonOnDateClick.replace("{1}", mask);
				stringBuilder.append(onDateClick);
			}

			if (dateSelectClientBehaviorScript != null) {
				stringBuilder.append(dateSelectClientBehaviorScript);
			}

			stringBuilder.append("}}");
			encodeNonEscapedObject(responseWriter, "dateClick", stringBuilder.toString(), true);
			responseWriter.write(StringPool.CLOSE_CURLY_BRACE);
			calendarFirst = false;
		}

		responseWriter.write(StringPool.CLOSE_CURLY_BRACE);
	}

	protected void encodeDate(ResponseWriter responseWriter, InputDate inputDate, String attributeName,
		Object dateObject, boolean first) throws IOException {

		String datePattern = inputDate.getDatePattern();
		String timeZoneString = inputDate.getTimeZone();
		TimeZone timeZone = TimeZone.getTimeZone(timeZoneString);
		Date date = InputDateTimeUtil.getObjectAsDate(dateObject, datePattern, timeZone);

		// Note: The JavaScript date object expects zero-based month numbers, so it is necessary to offset the month
		// by 1.
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("'new Date'(yyyy,MM-1,dd,0,0,0,0)");
		simpleDateFormat.setTimeZone(timeZone);

		String dateString = simpleDateFormat.format(date);
		encodeNonEscapedObject(responseWriter, attributeName, dateString, first);
	}

	@Override
	protected void encodeHiddenAttributes(FacesContext facesContext, ResponseWriter responseWriter, InputDate inputDate,
		boolean first) throws IOException {

		BrowserSnifferFactory browserSnifferFactory = (BrowserSnifferFactory) FactoryExtensionFinder.getFactory(
				BrowserSnifferFactory.class);
		BrowserSniffer browserSniffer = browserSnifferFactory.getBrowserSniffer(facesContext.getExternalContext());

		if (!(browserSniffer.isMobile() && inputDate.isResponsive())) {

			encodeCalendar(facesContext, responseWriter, inputDate, first);
			first = false;

			encodeHiddenAttributesInputDateTime(facesContext, responseWriter, inputDate, first);
			first = false;
		}
	}

	@Override
	protected void encodeMask(ResponseWriter responseWriter, InputDate inputDate, String datePattern, boolean first)
		throws IOException {

		String datePatternMask = getMaskFromDatePattern(datePattern);

		super.encodeMask(responseWriter, inputDate, datePatternMask, first);
	}

	@Override
	public String getAlloyClassName(FacesContext facesContext, UIComponent uiComponent) {

		String alloyClassName = super.getAlloyClassName(facesContext, uiComponent);
		BrowserSnifferFactory browserSnifferFactory = (BrowserSnifferFactory) FactoryExtensionFinder.getFactory(
				BrowserSnifferFactory.class);
		BrowserSniffer browserSniffer = browserSnifferFactory.getBrowserSniffer(facesContext.getExternalContext());
		InputDate inputDate = (InputDate) uiComponent;

		if (browserSniffer.isMobile() && inputDate.isResponsive()) {
			alloyClassName = alloyClassName.concat("Native");
		}

		return alloyClassName;
	}

	@Override
	public String getButtonIconName() {
		return "calendar";
	}

	@Override
	public String getDelegateComponentFamily() {
		return InputText.DELEGATE_COMPONENT_FAMILY;
	}

	@Override
	public String getDelegateRendererType() {
		return InputText.DELEGATE_RENDERER_TYPE;
	}

	@Override
	protected InputDateTimeResponseWriter getInputDateTimeResponseWriter(ResponseWriter responseWriter,
		String inputClientId, boolean mobile, boolean responsive) {
		return new InputDateResponseWriter(responseWriter, StringPool.INPUT, inputClientId, mobile, responsive);
	}

	@Override
	protected String[] getModules(FacesContext facesContext, UIComponent uiComponent) {

		String[] modules = super.getModules(facesContext, uiComponent);
		BrowserSnifferFactory browserSnifferFactory = (BrowserSnifferFactory) FactoryExtensionFinder.getFactory(
				BrowserSnifferFactory.class);
		BrowserSniffer browserSniffer = browserSnifferFactory.getBrowserSniffer(facesContext.getExternalContext());
		InputDate inputDate = (InputDate) uiComponent;
		boolean responsive = inputDate.isResponsive();

		if (browserSniffer.isMobile() && responsive) {
			String nativeAlloyModuleName = modules[0].concat("-native");
			modules = new String[] { nativeAlloyModuleName };
		}

		return modules;
	}
}
