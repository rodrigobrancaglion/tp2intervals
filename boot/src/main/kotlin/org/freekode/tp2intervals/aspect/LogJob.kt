package org.freekode.tp2intervals.aspect

/**
 * Custom annotation to enable AOP-based logging for specific methods.
 */
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention(AnnotationRetention.RUNTIME)
annotation class LogJob