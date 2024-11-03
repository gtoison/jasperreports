/*
 * JasperReports - Free Java Reporting Library.
 * Copyright (C) 2001 - 2023 Cloud Software Group, Inc. All rights reserved.
 * http://www.jaspersoft.com
 *
 * Unless you have purchased a commercial license agreement from Jaspersoft,
 * the following license terms apply:
 *
 * This program is part of JasperReports.
 *
 * JasperReports is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JasperReports is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JasperReports. If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.jasperreports.interactivity.crosstabs;

import java.util.LinkedHashMap;
import java.util.Map;

import net.sf.jasperreports.crosstabs.CrosstabConstants;
import net.sf.jasperreports.engine.JRGenericPrintElement;
import net.sf.jasperreports.engine.JRPropertiesUtil;
import net.sf.jasperreports.engine.ReportContext;
import net.sf.jasperreports.engine.fill.JRFillCrosstab;
import net.sf.jasperreports.jackson.util.JacksonUtil;
import net.sf.jasperreports.json.export.GenericElementJsonHandler;
import net.sf.jasperreports.json.export.JsonExporterContext;

/**
 * @author Lucian Chirita (lucianc@users.sourceforge.net)
 */
public class CrosstabInteractiveJsonHandler implements GenericElementJsonHandler
{
	private static final CrosstabInteractiveJsonHandler INSTANCE = new CrosstabInteractiveJsonHandler();
	
	public static CrosstabInteractiveJsonHandler getInstance()
	{
		return INSTANCE;
	}
	
	private CrosstabInteractiveJsonHandler()
	{
	}

	@Override
	public boolean toExport(JRGenericPrintElement element)
	{
		return true;
	}

	@Override
	public String getJsonFragment(JsonExporterContext exporterContext, JRGenericPrintElement element)
	{
		ReportContext reportContext = exporterContext.getExporterRef().getReportContext();
		String jsonFragment = null;
		boolean interactive = JRPropertiesUtil.getInstance(exporterContext.getJasperReportsContext()).getBooleanProperty(
				JRFillCrosstab.PROPERTY_INTERACTIVE, true);
		if (reportContext != null && interactive)
		{
			Map<String, Object> elementInfo = new LinkedHashMap<>();
			
			String crosstabId = (String) element.getParameterValue(CrosstabConstants.ELEMENT_PARAMETER_CROSSTAB_ID);
			String crosstabFragmentId = (String) element.getParameterValue(CrosstabConstants.ELEMENT_PARAMETER_CROSSTAB_FRAGMENT_ID);
			if (crosstabFragmentId == null)
			{
				// this can happen for JasperPrints generated by 5.5.0
				crosstabFragmentId = crosstabId;
			}
			
			elementInfo.put("type", "crosstab");
			elementInfo.put("module", "jive.crosstab");
			elementInfo.put("uimodule", "jive.crosstab.interactive");
			elementInfo.put("id", crosstabFragmentId);
			elementInfo.put("fragmentId", crosstabFragmentId);
			elementInfo.put("crosstabId", crosstabId);
			elementInfo.put("startColumnIndex", element.getParameterValue(CrosstabConstants.ELEMENT_PARAMETER_START_COLUMN_INDEX));			
			elementInfo.put("hasFloatingHeaders", element.getParameterValue(CrosstabConstants.ELEMENT_PARAMETER_FLOATING_HEADERS));
			elementInfo.put("rowGroups", element.getParameterValue(CrosstabConstants.ELEMENT_PARAMETER_ROW_GROUPS));
			elementInfo.put("dataColumns", element.getParameterValue(CrosstabConstants.ELEMENT_PARAMETER_DATA_COLUMNS));
			
			String elementInfoJson = JacksonUtil.getInstance(exporterContext.getJasperReportsContext()).getJsonString(elementInfo);
			// assuming the Id doesn't need escaping
			jsonFragment = "\"" + crosstabFragmentId + "\":" + elementInfoJson;
		}
		return jsonFragment;
	}

}
