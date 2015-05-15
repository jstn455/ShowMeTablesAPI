package com.showmetables.service.model;

import java.util.Comparator;

import org.apache.commons.lang3.StringUtils;

public class TableComparator implements Comparator<Table> {

	@Override
	public int compare(Table t1, Table t2) {
		if (StringUtils.isEmpty(t1.getSchemaName()) || StringUtils.isEmpty(t2.getSchemaName()) ||t1.getSchemaName().equals(t2.getSchemaName())) {
			return t1.getName().compareToIgnoreCase(t2.getName());
		} else {
			return t1.getSchemaName().compareToIgnoreCase(t2.getSchemaName());
		}
	}

}
