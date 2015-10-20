/***************************************************
*
* cismet GmbH, Saarbruecken, Germany
*
*              ... and it just works.
*
****************************************************/
package de.cismet.cids.custom.wupp.geocpm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * To not make things more complicated we only use this as a marker and do plain Reflection instead of using a custom
 * annotation processor. It would be much nicer to have a generic property injection mechanism. Maybe some day in the
 * future this will be done.
 *
 * @author   martin.scholl@cismet.de
 * @version  1.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Inject {
    String propertyName() default "";
}
