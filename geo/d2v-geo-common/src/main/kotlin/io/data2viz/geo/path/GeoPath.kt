package io.data2viz.geo.path

import io.data2viz.geo.GeoJSON
import io.data2viz.geo.projection.Projection
import io.data2viz.geo.stream
import io.data2viz.path.PathAdapter

fun geoPath(projection: Projection, context: PathAdapter) = GeoPath(projection, context)

class GeoPath(val projection: Projection, val context: PathAdapter) {

    private val contextStream: PathContext = PathContext(context)

    fun path(geo: GeoJSON): PathAdapter {
        stream(geo, projection.stream(contextStream))
        return context
    }
}