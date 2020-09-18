package be.jdevelopment.tools.validation.step;

import be.jdevelopment.tools.validation.ObjectProvider;
import be.jdevelopment.tools.validation.PropertyToken;
import be.jdevelopment.tools.validation.property.MonadOfProperties;

import java.util.Stack;

import static java.util.Objects.requireNonNull;

public class ValidationProcessResult implements AutoCloseable {
	
	private Object[] results;
	private String[] properties;
	
	ValidationProcessResult(String[] properties, Object[] results) {
		assert results.length == properties.length : "Results have not same size as properties. This should have been handled by package classes";
		this.results = results;
		this.properties = properties;
	}
	
	@Override
	public void close() {
		for(int i = 0; i < results.length; i++) {
			results[i] = null;
			properties[i] = null;
		}
		results = null;
		properties = null;
	}
	
}