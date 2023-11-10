package com.dokar.pinchzoomgrid

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateCentroid
import androidx.compose.foundation.gestures.calculateCentroidSize
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChanged
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.util.fastForEach
import kotlin.math.abs

internal fun Modifier.handlePinchGesture(state: PinchZoomLazyGridState): Modifier {
    return this.pointerInput(state) {
        // Based on detectTransformGestures()
        awaitEachGesture {
            var zoom = 1f
            var pastTouchSlop = false
            val touchSlop = viewConfiguration.touchSlop / 4

            awaitFirstDown(requireUnconsumed = false)

            var pinchStarted = false

            do {
                val event = awaitPointerEvent()
                val canceled = event.changes.fastAny { it.isConsumed }
                if (!canceled) {
                    val zoomChange = event.calculateZoom()

                    if (!pastTouchSlop) {
                        zoom *= zoomChange
                        val centroidSize = event.calculateCentroidSize(useCurrent = false)
                        val zoomMotion = abs(1 - zoom) * centroidSize

                        if (zoomMotion > touchSlop) {
                            pastTouchSlop = true
                        }
                    }

                    if (pastTouchSlop) {
                        if (zoomChange != 1f) {
                            if (!pinchStarted) {
                                val centroid = event.calculateCentroid(useCurrent = false)
                                state.onZoomStart(centroid, zoom)
                                pinchStarted = true
                            }
                            state.onZoom(zoomChange)
                        }
                        event.changes.fastForEach {
                            if (it.positionChanged()) {
                                it.consume()
                            }
                        }
                    }
                }
            } while (!canceled && event.changes.fastAny { it.pressed })
            if (pinchStarted) {
                state.onZoomStopped()
            }
        }
    }
}

internal fun Modifier.handleOverZooming(state: PinchZoomLazyGridState): Modifier {
    return this.graphicsLayer {
        val isOverZooming = state.isZooming && state.nextCells == null
        if (!isOverZooming) return@graphicsLayer
        val scale = when (state.zoomType) {
            ZoomType.ZoomIn -> 1f + state.progress * 0.1f
            ZoomType.ZoomOut -> 1f - state.progress * 0.1f
        }
        scaleX = scale
        scaleY = scale
    }
}
