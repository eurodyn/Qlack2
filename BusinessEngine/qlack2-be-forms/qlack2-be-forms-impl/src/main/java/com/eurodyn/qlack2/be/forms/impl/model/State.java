package com.eurodyn.qlack2.be.forms.impl.model;

public enum State {
	/**
	 * Important: Do not modify the order of enum values below since
	 * EnumType.ORDINAL is used in the FormVersion entity and therefore a
	 * change in the order will render existing DB data inconsistent.
	 * New values should always be added to the end of the list.
	 */
	DRAFT,
	FINAL,
	TESTING
}