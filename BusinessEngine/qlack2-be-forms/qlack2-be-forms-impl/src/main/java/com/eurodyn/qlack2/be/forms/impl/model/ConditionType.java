package com.eurodyn.qlack2.be.forms.impl.model;

public enum ConditionType {
	/**
	 * Important: Do not modify the order of enum values below since
	 * EnumType.ORDINAL is used in the Condition entity and therefore a
	 * change in the order will render existing DB data inconsistent.
	 * New values should always be added to the end of the list.
	 */
	PRECONDITION,
	POSTCONDITION,
	DEFAULT_VALIDATION,
	VALIDATION
}
