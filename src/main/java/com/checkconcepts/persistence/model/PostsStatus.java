package com.checkconcepts.persistence.model;

import java.util.ArrayList;
import java.util.List;

public enum PostsStatus {

	CREATED,

	READY_FOR_PUBLISH,
	
	IMPROVEMENT_REQUIRED,

	PUBLISHED;

	/** used for conversion from int to text representation */
	public static final List<String> valuesAsString;

	static {
		valuesAsString = new ArrayList<String>();
		for (PostsStatus ts : PostsStatus.values()) {
			valuesAsString.add(ts.name());
		}
	}

	/**
	 *
	 * @return list with the string representation of enum values
	 */
	public static List<String> getValuesAsString() {
		return valuesAsString;
	}

	public String getDisplayName() {
		String string = name();
		char ch = ' ';
		// Replace _ with space
		string = string.replace('_', ch);
		return string;
	}

	public String getName() {
		return name();
	}

}
