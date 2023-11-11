package com.dokar.pinchzoomgrid

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.InspectorInfo
import androidx.compose.ui.unit.toSize

/**
 * The scope to create a [LazyVerticalGrid] or [LazyHorizontalGrid]. Both [gridState],
 * [gridCells], and [pinchItem] should be used to create the grid.
 */
interface PinchZoomGridScope {
    /**
     * The lazy grid state used to pass to [LazyVerticalGrid] or [LazyHorizontalGrid].
     */
    val gridState: LazyGridState

    /**
     * The grid cells used to pass to [LazyVerticalGrid] or [LazyHorizontalGrid].
     */
    val gridCells: GridCells

    /**
     * Mark the item to be animatable. The [key] should be the same as the key passed
     * to [LazyGridScope.item] or [LazyGridScope.items].
     */
    fun Modifier.pinchItem(
        key: Any,
        transitions: PinchItemTransitions = PinchItemTransitions.All,
    ): Modifier
}

internal class CurrPinchZoomGridScope(
    private val state: PinchZoomGridState,
    override val gridState: LazyGridState,
) : PinchZoomGridScope {
    override val gridCells: GridCells get() = state.currentCells

    override fun Modifier.pinchItem(
        key: Any,
        transitions: PinchItemTransitions
    ): Modifier {
        val shouldScale = transitions.has(PinchItemTransitions.Scale)
        val shouldTranslate = transitions.has(PinchItemTransitions.Translate)
        return this
            .onGloballyPositioned { state.itemsBounds[key] = it.bounds() }
            .onDetach { state.itemsBounds.remove(key) }
            .drawWithContent {
                if (state.isCurrItemsVisible) {
                    drawContent()
                }
            }
            .graphicsLayer {
                if (!state.isCurrItemsVisible || !state.isZooming || state.nextCells == null) {
                    return@graphicsLayer
                }

                val progress = state.progress
                val currBounds = state.itemsBounds[key]
                val nextBounds = state.nextItemsBounds[key]
                if (nextBounds == null) {
                    if (state.isNextItemsBoundsReady) {
                        // Items are not in the next cells now
                        alpha = 1f - progress
                    }
                    return@graphicsLayer
                }

                if (currBounds != null && (shouldScale || shouldTranslate)) {
                    if (!state.animatingKeys.contains(key)) {
                        // Notify the next grid renders non-animating items
                        state.animatingKeys.add(key)
                        state.animatingKeysSignal++
                    }
                }

                if (shouldScale) {
                    // Scale
                    transformOrigin = TransformOrigin(0f, 0f)
                    val targetScaleX = nextBounds.size.width / size.width
                    val targetScaleY = nextBounds.size.height / size.height
                    scaleX = 1f + (targetScaleX - 1f) * progress
                    scaleY = 1f + (targetScaleY - 1f) * progress
                }

                if (shouldTranslate && currBounds != null) {
                    // Translate
                    val targetTranX = nextBounds.left - currBounds.left
                    val targetTranY = nextBounds.top - currBounds.top
                    translationX = targetTranX * progress
                    translationY = targetTranY * progress
                }
            }
    }
}

internal class NextPinchZoomGridScope(
    private val state: PinchZoomGridState,
    override val gridState: LazyGridState,
    override val gridCells: GridCells,
) : PinchZoomGridScope {
    override fun Modifier.pinchItem(
        key: Any,
        transitions: PinchItemTransitions
    ): Modifier {
        return this
            .onGloballyPositioned { state.nextItemsBounds[key] = it.bounds() }
            .drawWithContent {
                val needToDraw = state.isZooming &&
                        state.animatingKeysSignal > 0 &&
                        !state.animatingKeys.contains(key)
                if (state.forceShowNextItems || needToDraw) {
                    drawContent()
                }
            }
            .graphicsLayer {
                if (state.isZooming) {
                    alpha = state.progress
                }
            }
    }
}

private fun LayoutCoordinates.bounds(): Rect {
    return Rect(
        offset = positionInRoot(),
        size = size.toSize(),
    )
}

private fun Modifier.onDetach(block: () -> Unit): Modifier =
    this.then(OnDetachModifierElement(block))

private class OnDetachModifierElement(
    private var onDetach: () -> Unit,
) : ModifierNodeElement<OnDetachModifierNode>() {
    override fun InspectorInfo.inspectableProperties() {
        name = "onDetach"
        properties["onDetach"] = onDetach
    }

    override fun create(): OnDetachModifierNode {
        return OnDetachModifierNode(onDetach)
    }

    override fun update(node: OnDetachModifierNode) {
        node.onDetach = onDetach
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as OnDetachModifierElement

        return onDetach == other.onDetach
    }

    override fun hashCode(): Int {
        return onDetach.hashCode()
    }
}

private class OnDetachModifierNode(
    var onDetach: () -> Unit,
) : Modifier.Node() {
    override fun onDetach() {
        super.onDetach()
        this.onDetach.invoke()
    }
}