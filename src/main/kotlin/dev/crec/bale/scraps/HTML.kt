package dev.crec.bale.scraps

import kotlinx.html.*


@HtmlTagMarker
inline fun FlowContent.path(classes: String? = null, crossinline block: PATH.() -> Unit = {}): Unit = PATH(
    attributesMapOf("class", classes), consumer
).visit(block)

open class PATH(initialAttributes: Map<String, String>, override val consumer: TagConsumer<*>) :
    HTMLTag("path", consumer, initialAttributes, null, false, false),
    HtmlBlockTag {}
