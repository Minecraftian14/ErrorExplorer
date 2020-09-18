package be.jdevelopment.tools.validation.property.impl;

import be.jdevelopment.tools.validation.error.FailureBuilder;
import be.jdevelopment.tools.validation.property.MonadOfProperties;
import be.jdevelopment.tools.validation.property.Property;

import org.junit.Test;

import java.util.Stack;
import java.util.function.Function;

import static org.junit.Assert.*;

public class MutablePropertyTest {
	
	static class Util {
		Stack<String> errors = new Stack<>();
		FailureBuilder builder = errors::add;
		MonadStructure struct = new MonadStructure(builder);
		
		<T> MutableProperty<T> map(String value, Function<String, T> f) {
			return (MutableProperty<T>) new MutableProperty<String>(value, struct).map(f::apply);
		}
	}
	
	/* Map test */
	
	@Test
	public void map_convertsValue_givenSuccess() {
		var util = new Util();
		var mut = util.<Boolean> map("Hello", String::isEmpty);
		
		assertEquals(AbstractProperty.STATE.SUCCESS, mut.state);
		assertFalse((Boolean) mut.value);
	}
	
	@Test
	public void map_convertsValue_givenNullSuccess() {
		var util = new Util();
		var mut = util.<String> map(null, $ -> "Hello");
		
		assertEquals(AbstractProperty.STATE.SUCCESS, mut.state);
		assertEquals("Hello", mut.value);
	}
	
	@Test
	public void map_convertsToNullValue_givenSuccess() {
		var util = new Util();
		var mut = util.<String> map("Hello", $ -> null);
		
		assertEquals(AbstractProperty.STATE.SUCCESS, mut.state);
		assertNull(mut.value);
	}
	
	@Test
	public void map_doesNotAct_givenFailed() {
		var util = new Util();
		
		var failUncut = new MutableProperty(null, util.struct).switchTo(AbstractProperty.STATE.FAIL_UNCUT);
		assertEquals(AbstractProperty.STATE.FAIL_UNCUT, failUncut.state);
		failUncut.map($ -> { throw new AssertionError("Should not throw from failed property"); });
		assertEquals(AbstractProperty.STATE.FAIL_UNCUT, failUncut.state);
		
		var failCut = new MutableProperty(null, util.struct).switchTo(AbstractProperty.STATE.FAIL_CUT);
		assertEquals(AbstractProperty.STATE.FAIL_CUT, failCut.state);
		failCut.map($ -> { throw new AssertionError("Should not throw from failed property"); });
		assertEquals(AbstractProperty.STATE.FAIL_CUT, failCut.state);
	}
	
	@Test
	public void map_switchedToFailUncut_givenThrowingMethod() {
		var util = new Util();
		var mut = new MutableProperty<Object>(null, util.struct);
		
		var exception = new RuntimeException();
		
		try {
			mut.map($ -> { throw exception; });
		} catch(RuntimeException e) {
			assertEquals(exception, e);
			assertEquals(AbstractProperty.STATE.FAIL_UNCUT, mut.state);
		}
	}
	
}