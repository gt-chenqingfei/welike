/**
 * ****************************************************************************
 * Copyright (C) 2005-2016 Pekingese Corporation. All rights reserved
 * File        : 17/7/25
 * <p>
 * Description : description
 * <p>
 * Creation    : 17/7/25
 * Author      : liwenbo0328@163.com
 * History     : Creation, 17/7/25, liwenbo, Create the file
 * ****************************************************************************
 */
package com.pekingese.pagestack.framework.view;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation which allows marking of views to be decoration views when added to a view
 * pager.
 * <p>
 * <p>Views marked with this annotation can be added to the view pager with a layout resource.
 * An example being {@link com.pekingese.pagestack.framework.titlestrip.SubPageTitleStrip}.</p>
 * <p>
 * <p>You can also control whether a view is a decor view but setting
 * {@link BasePageStack.LayoutParams#isDecor} on the child's layout params.</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface DecorView {
}