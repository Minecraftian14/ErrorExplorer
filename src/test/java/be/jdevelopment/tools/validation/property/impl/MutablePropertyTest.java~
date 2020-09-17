package be.jdevelopment.tools.validation.property.impl;

import be.jdevelopment.tools.validation.error.FailureBuilder;
import be.jdevelopment.tools.validation.property.MonadOfProperties;
import be.jdevelopment.tools.validation.property.Property;

import org.junit.Test;

import static org.junit.Assert.*;

public class MonadStructureTest {
	
	@Test
	public void fail_returnsFailUncut() {
		var structure = new MonadStructure(null);
		var property = (MutableProperty<Object>) structure.fail();
		
		assertNotNull(property);
		assertEquals(AbstractProperty.STATE.FAIL_UNCUT, property.state);
		assertNull(property.value);
	}
	
	@Test
	public void of_returnsSuccess_givenNull() {
		var structure = new MonadStructure(null);
		var property = (MutableProperty<Object>) structure.of(null);
		
		assertNotNull(property);
		assertEquals(AbstractProperty.STATE.SUCCESS, property.state);
		assertNull(property.value);
	}
	
	@Test
	public void of_returnsSuccess_givenNonNull() {
		var structure = new MonadStructure(null);
		var property = (MutableProperty<String>) structure.of("Hello World");
		
		assertNotNull(property);
		assertEquals(AbstractProperty.STATE.SUCCESS, property.state);
		assertEquals("Hello World", property.value);
	}
	
}