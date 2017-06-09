package io.data2viz.experiments.voronoi

import io.data2viz.color.colors
import io.data2viz.color.colors.black
import io.data2viz.color.colors.darkblue
import io.data2viz.color.colors.lightyellow
import io.data2viz.color.colors.white
import io.data2viz.interpolate.linkedTo
import io.data2viz.interpolate.interpolateRgb
import io.data2viz.experiments.perfs.FpsCalculator
import io.data2viz.interpolate.scale
import io.data2viz.math.Angle
import io.data2viz.math.deg
import io.data2viz.svg.*
import io.data2viz.voronoi.Diagram
import io.data2viz.voronoi.Edge
import io.data2viz.voronoi.Point
import io.data2viz.voronoi.Site
import org.w3c.dom.Element
import org.w3c.dom.Node
import org.w3c.dom.asList
import kotlin.browser.document
import kotlin.browser.window
import kotlin.js.Date
import kotlin.js.Math.random
import kotlin.reflect.KMutableProperty0

fun voronoiSphere() {


    data class SphereConfig(
            var clipping:Boolean = true,
            var showPolygons:Boolean = true,
            var delaunay:Boolean = true,
            var pointCount:Int = 200

    )

    val size = 600

    val commandHeight = 200

    val config = SphereConfig()

    val fpsCalculator = FpsCalculator(document.querySelector("#fps span")!!)

    val randomPoints = (1..config.pointCount).map { GeoPoint((random() * 360).deg, (random() * 360).deg) }.toMutableList()

    val circleRadius = scale.linear.numberToNumber(
            -1 linkedTo 3,
            1 linkedTo 1
    )

    val circleAlpha = scale.linear.numberToNumber(
            -1 linkedTo 1,
            1 linkedTo .2
    )

    val pointToScreen = scale.linear.numberToNumber(
            -1.0 linkedTo -200.0,
            1.0 linkedTo 200.0
    )

    fun List<GeoPoint>.sites() = mapIndexed { index, point -> Site(Point(pointToScreen(point.x), pointToScreen(point.y)), index) }.toTypedArray()

    val it = interpolateRgb(darkblue, lightyellow, 0.7)

    svg {
        width = size
        height = size + commandHeight
        var diagram: Diagram? = null

        val rotationAnimation = RotationAnimation(15.0)
        rotationAnimation { rotation ->
            fpsCalculator.updateFPS()
            randomPoints.forEach { geoPoint -> geoPoint.rotation = rotation }
            if(config.delaunay || config.showPolygons)
                diagram = if (config.clipping) Diagram(randomPoints.sites(), Point(-1000.0, -1000.0), Point(1000.0, 1000.0))
                        else Diagram(randomPoints.sites())
        }

//        val background = rect {
//            width = size
//            height = size
//            fill = colors.black
//        }


        val sphere = g {
            transform {
                translate(size / 2, size / 2)
                rotate((-20).deg)
            }

            randomPoints.forEach { geoPoint ->
                circle {
                    r = circleRadius(geoPoint.z)
                    cx = pointToScreen(geoPoint.x)
                    cy = pointToScreen(geoPoint.y)
                    rotationAnimation { rotation ->
                        fill = it(((geoPoint.x+1)/2).toFloat())
                        r = circleRadius(geoPoint.z)
                        cx = pointToScreen(geoPoint.x)
                    }
                }
            }

            g { //polygons
                rotationAnimation { rotation ->
                    if(config.showPolygons) {
                        selectAll<LineElement, Edge>("line", diagram!!.edges.filterNotNull()) {
                            add = { edge ->
                                if (edge.start != null && edge.end != null)
                                    line {
                                        x1 = edge.start!!.x
                                        y1 = edge.start!!.y
                                        x2 = edge.end!!.x
                                        y2 = edge.end!!.y
                                        stroke = colors.black
                                    }
                            }
                            update = { line, edge ->
                                if (edge.start != null && edge.end != null) {
                                    line.x1 = edge.start!!.x
                                    line.y1 = edge.start!!.y
                                    line.x2 = edge.end!!.x
                                    line.y2 = edge.end!!.y
                                    line. stroke = colors.black
                                } else {
                                    removeChild(line.element)
                                }
                            }
                            remove = { removeChild(it) }
                        }
                    } else {
                        selectAll<LineElement,Edge>("line", emptyList() ) {
                            remove = {removeChild(it)}
                        }
                    }
                }
            }
            g { //delaunay
                rotationAnimation { rotation ->
                    if(config.delaunay) {
                        selectAll<LineElement, Diagram.Link>("line", diagram!!.links().filterNotNull()) {
                            add = { link ->
                                    line {
                                        x1 = link.source.x
                                        y1 = link.source.y
                                        x2 = link.target.x
                                        y2 = link.target.y
                                        stroke = colors.red
                                    }
                            }
                            update = { line, link ->
                                    line.x1 = link.source.x
                                    line.y1 = link.source.y
                                    line.x2 = link.target.x
                                    line.y2 = link.target.y
                                    line. stroke = colors.red
                                }
                            remove = { removeChild(it) }
                        }
                    } else {
                        selectAll<LineElement,Edge>("line", emptyList() ) {
                            remove = {removeChild(it)}
                        }
                    }
                }
            }
        }


        val commandPanel = g {
            transform {
                translate(y=size)
            }
            rect {
                width = size
                height = commandHeight
                fill = white
            }

            val line1 = g {
                transform {
                    translate(y = 30)
                }

                toggleButton("Clipping", config::clipping)
                toggleButton("Polygons", config::showPolygons).apply {
                    transform { translate(150) }
                }
                toggleButton("Delaunay", config::delaunay).apply {
                    transform { translate(300) }
                }
            }
        }

    }

}

private fun GroupElement.toggleButton(name:String, clippingHolder: KMutableProperty0<Boolean>): GroupElement {
    return g {
        setAttribute("style", "cursor:pointer")
        val btBack = rect {
            width = 125
            height = 22
            rx = 3
            ry = 3
            fill = white
            stroke = black
        }
        val label = text {
            x = 10
            y = 16
            text = "$name : ${clippingHolder.get()}"
        }
        on("click") {
            clippingHolder.set(!clippingHolder.get())
            label.text = "$name : ${clippingHolder.get()}"

        }
    }
}

data class GeoPoint(val lat: Angle, val long: Angle) {
    var rotation: Angle = 0.deg
    val y = lat.sin
    val r = lat.cos
    val x: Double
        get() {
            return r * (rotation + long).cos
        }
    val z: Double get() = r * (rotation + long).sin
}


//------------   API test -----------------------

class Selection<E:ElementWrapper,T> {
    var add: ((T) -> Unit) = {}
    var update: ((E,T) -> Unit) = { e, t -> }
    var remove: ((Node) -> Unit) = {}
}

class ElementWrappers {

    val _wrappers = mapOf<Any, (Element) -> ElementWrapper>(
            LineElement::class to {element:Element -> LineElement(element)}
    )

    inline fun <reified E:ElementWrapper> wrap(element: Element): E {
        val c = E::class
        return (_wrappers[c]!!(element) as E)
    }
}

val nodeWrappers = ElementWrappers()

inline fun <reified E:ElementWrapper, T> ParentElement.selectAll(selector: String, datas: List<T>, init: Selection<E,T>.() -> Unit) {
    val selection = Selection<E,T>()
    selection.init()
    val elements = element.querySelectorAll(selector).asList().filterNotNull().map { it as Element }
    if (elements.size > datas.size)  elements.drop(datas.size).forEach { selection.remove(it) }
    if (elements.size < datas.size) datas.drop(elements.size).forEachIndexed { i,t -> selection.add(t)}
    datas.take(elements.size).forEachIndexed { i,t -> selection.update(nodeWrappers.wrap<E>(elements[i]), t) }
}

fun ParentElement.removeChild(child: Node) {
    element.removeChild(child)
}

class RotationAnimation(rotationTimeInSeconds: Double) {

    val startTime = Date().getTime()
    val rotationPerMs = (360.0 / (rotationTimeInSeconds * 1000)).deg

    val blocksOfAnimation = mutableListOf<(Angle) -> Unit>()

    init {
        fun animate() {
            val currentTime = Date().getTime()
            window.requestAnimationFrame {
                val rotation = rotationPerMs * (currentTime - startTime)
                blocksOfAnimation.forEach { it(rotation) }
                animate()
            }
        }
        animate()
    }

    operator fun invoke(animation: (Angle) -> Unit) {
        blocksOfAnimation.add(animation)
    }
}
