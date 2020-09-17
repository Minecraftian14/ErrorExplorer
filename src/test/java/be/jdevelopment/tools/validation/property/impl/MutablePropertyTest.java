package be.jdevelopment.tools.validation.property.impl;

import be.jdevelopment.tools.validation.error.FailureBuilder;
import be.jdevelopment.tools.validation.property.MonadOfProperties;
import be.jdevelopment.tools.validation.property.Property;

import org.junit.Test;

import java.util.Stack;

import static org.junit.Assert.*;

public class MutablePropertyTest {
	
	static class Util {
		Stack<String> errors = new Stack<>();
		FailureBuilder builder = errors::add;
		MonadStructure struct = new MonadStructure(builder);
	}
	
	@Test
	public void fail_returnsFailUncut() {
		var util = new Util();
		var mut = new MutableProperty<String>("Hello", util.struct);
		
		assertEquals(AbstractProperty.STATE.SUCCESS, mut.state);
	}
	
}