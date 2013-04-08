/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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
package com.liferay.faces.demos.list;

import com.liferay.portal.kernel.util.HtmlUtil;
import com.liferay.portal.kernel.util.HttpUtil;
import com.liferay.portal.kernel.util.StringPool;

import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.model.DLFileEntryWrapper;


/**
 * @author  Neil Griffin
 */
public class DocLibFileEntry extends DLFileEntryWrapper {

	// serialVersionUID
	private static final long serialVersionUID = 1890897391510276691L;

	// Private Data Members
	private String kilobytes;
	private String url;
	private String userName;

	public DocLibFileEntry(DLFileEntry dlFileEntry, String portalURL, String pathContext, long scopeGroupId) {
		super(dlFileEntry);
		this.kilobytes = Long.toString(dlFileEntry.getSize() / 1024L) + " KB";
		this.url = portalURL + pathContext + "/documents/" + Long.toString(scopeGroupId) + StringPool.SLASH +
			dlFileEntry.getFolderId() + StringPool.SLASH +
			HttpUtil.encodeURL(HtmlUtil.unescape(dlFileEntry.getTitle()));
		this.userName = dlFileEntry.getUserName();

		// this.url = portalURL + pathContext + "/documents/" + Long.toString(scopeGroupId) + StringPool.SLASH +
		// dlFileEntry.getUuid();
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj)
			return true;

		if (obj == null)
			return false;

		if (getClass() != obj.getClass())
			return false;

		DocLibFileEntry other = (DocLibFileEntry) obj;
		DLFileEntry dlFileEntry = getWrappedModel();

		if (dlFileEntry == null) {

			if (other.getWrappedModel() != null)
				return false;
		}
		else if (!dlFileEntry.equals(other.getWrappedModel())) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		DLFileEntry dlFileEntry = getWrappedModel();
		final int prime = 31;
		int result = 1;
		result = (prime * result) + ((dlFileEntry == null) ? 0 : dlFileEntry.hashCode());

		return result;
	}

	public String getKilobytes() {
		return kilobytes;
	}

	public String getUrl() {
		return url;
	}

	@Override
	public String getUserName() {
		return userName;
	}
}
