package com.pekingese.pagestack.framework.page

@Retention
@Target(AnnotationTarget.CLASS)
annotation class PageName(val value: String = "")