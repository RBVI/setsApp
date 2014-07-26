package edu.ucsf.rbvi.setsApp.internal.model;

public enum SetOperations {
	INTERSECT("⋂"), UNION("⋃"), DIFFERENCE("\\");

  final String operator;
  SetOperations(final String operator) {
    this.operator = operator;
  }

  public String operator() {
    return operator;
  }
}
