package org.latency4j;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Method-level annotation which indicates that the method falls under a
 * {@link LatencyRequirement#getWorkCategory() monitored category} as defined in
 * a {@link LatencyRequirement latency requirement}. This annotation has a
 * single text parameter, which is used to specify the
 * {@link LatencyRequirement#getWorkCategory() category} to which the method
 * belongs.
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface MonitoredByLatency4J {
	/**
	 * <p>
	 * Returns the {@link LatencyRequirement#getWorkCategory() category} of the
	 * monitored method, as specified in the annotation instance.
	 * </p>
	 * 
	 * @return The {@link LatencyRequirement#getWorkCategory() category} under
	 *         which the method is to be monitored.
	 */
	String value() default "";
}