package com.dokar.pinchzoomgrid.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.dokar.pinchzoomgrid.PinchItemTransitions
import com.dokar.pinchzoomgrid.PinchZoomGridLayout
import com.dokar.pinchzoomgrid.PinchZoomLazyGridScope
import com.dokar.pinchzoomgrid.demo.ui.theme.ComposeSampleTheme
import com.dokar.pinchzoomgrid.rememberPinchZoomLazyGridState
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ComposeSampleTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PinchZoomGridDemo()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PinchZoomGridDemo(modifier: Modifier = Modifier) {
    var showOptionsSheet by remember { mutableStateOf(false) }

    var showText by remember { mutableStateOf(false) }
    var horizontalGrid by remember { mutableStateOf(false) }

    val (cellsList, cellsNames) = remember {
        listOf(
            GridCells.Fixed(4),
            GridCells.Fixed(3),
            GridCells.Fixed(2),
        ) to listOf(
            "4",
            "3",
            "2",
        )
    }

    val state = rememberPinchZoomLazyGridState(
        cellsList = cellsList,
        initialCellsIndex = 1,
    )

    Box(modifier = modifier) {
        PinchZoomGridLayout(state = state) {
            if (horizontalGrid) {
                val insetsPadding = WindowInsets.systemBars.asPaddingValues()
                LazyHorizontalGrid(
                    rows = gridCells,
                    state = gridState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        top = insetsPadding.calculateTopPadding() + 72.dp,
                        bottom = insetsPadding.calculateBottomPadding(),
                    ),
                ) {
                    galleryItems(
                        showText = showText,
                        horizontalGrid = true,
                        pinchGridScope = this@PinchZoomGridLayout,
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = gridCells,
                    state = gridState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = WindowInsets.systemBars.asPaddingValues(),
                ) {
                    item { Spacer(modifier = Modifier.height(64.dp)) }

                    galleryItems(
                        showText = showText,
                        horizontalGrid = false,
                        pinchGridScope = this@PinchZoomGridLayout,
                    )
                }
            }
        }

        TitleBar(
            onConfigsClick = { showOptionsSheet = true },
        )
    }

    if (showOptionsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showOptionsSheet = false },
            windowInsets = WindowInsets(left = 0),
        ) {
            Options(
                cellsList = cellsList,
                cellsNames = cellsNames,
                currentCells = state.currentCells,
                onSelectCells = { state.animateToCells(it) },
                showText = showText,
                horizontalGrid = horizontalGrid,
                onShowTextChange = { showText = it },
                onHorizontalGridChange = { horizontalGrid = it },
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars),
            )
        }
    }
}

fun LazyGridScope.galleryItems(
    showText: Boolean,
    horizontalGrid: Boolean,
    pinchGridScope: PinchZoomLazyGridScope,
) {
    with(pinchGridScope) {
        item(
            key = "header1",
            span = { GridItemSpan(maxLineSpan) },
        ) {
            PhotoHeader(
                text = "Today",
                verticalText = horizontalGrid,
                modifier = Modifier.pinchItem(key = "header1"),
            )
        }

        items(count = 5, key = { index -> index }) {
            ImageItem(
                index = it,
                showText = showText,
                modifier = Modifier,
            )
        }

        item(
            key = "header2",
            span = { GridItemSpan(maxLineSpan) },
        ) {
            PhotoHeader(
                text = "Last week",
                verticalText = horizontalGrid,
                modifier = Modifier.pinchItem(key = "header2"),
            )
        }

        items(count = 25, key = { index -> index + 5 }) {
            ImageItem(
                index = it + 5,
                showText = showText,
                modifier = Modifier,
            )
        }

        item(
            key = "header3",
            span = { GridItemSpan(maxLineSpan) },
        ) {
            PhotoHeader(
                text = "Last month",
                verticalText = horizontalGrid,
                modifier = Modifier.pinchItem(key = "header3"),
            )
        }

        items(count = 45, key = { index -> index + 30 }) {
            ImageItem(
                index = it + 30,
                showText = showText,
                modifier = Modifier,
            )
        }
    }
}

@Composable
fun TitleBar(
    onConfigsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background.copy(alpha = 0.89f))
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "Pinch Zoom Grid",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .weight(1f),
        )

        IconButton(onClick = onConfigsClick) {
            Icon(imageVector = Icons.Outlined.Settings, contentDescription = null)
        }
    }
}

@Composable
fun Options(
    cellsList: List<GridCells>,
    cellsNames: List<String>,
    currentCells: GridCells,
    onSelectCells: (index: Int) -> Unit,
    showText: Boolean,
    horizontalGrid: Boolean,
    onShowTextChange: (Boolean) -> Unit,
    onHorizontalGridChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = "Options",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.align(Alignment.CenterHorizontally),
        )

        Text(
            text = "Cells",
            fontSize = 18.sp,
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 8.dp,
            ),
        )

        fun indexToSliderValue(index: Int): Float = index / (cellsList.size - 1f)

        fun sliderValueToIndex(value: Float): Int = (value * (cellsList.size - 1)).toInt()

        var sliderValue by remember(currentCells, cellsList) {
            val index = cellsList.indexOf(currentCells)
            val value = if (index != -1) {
                indexToSliderValue(index)
            } else {
                0f
            }
            mutableFloatStateOf(value)
        }


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            for (i in cellsNames.indices) {
                Text(
                    text = cellsNames[i],
                    fontSize = 14.sp,
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = MaterialTheme.shapes.small
                        )
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {
                                sliderValue = indexToSliderValue(i)
                                onSelectCells(i)
                            },
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }
        }

        Slider(
            value = sliderValue,
            onValueChange = {
                sliderValue = it
                onSelectCells(sliderValueToIndex(it))
            },
            modifier = Modifier.padding(horizontal = 16.dp),
            steps = (cellsList.size - 2).coerceAtLeast(0),
        )

        SwitchItem(checked = showText, onCheckedChange = onShowTextChange) {
            Text("Show text")
        }
        SwitchItem(checked = horizontalGrid, onCheckedChange = onHorizontalGridChange) {
            Text("Horizontal")
        }
    }
}

@Composable
fun SwitchItem(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CompositionLocalProvider(
            LocalTextStyle provides LocalTextStyle.current.copy(fontSize = 18.sp),
        ) {
            title()
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

@Composable
fun PhotoHeader(
    text: String,
    verticalText: Boolean,
    modifier: Modifier = Modifier,
) {
    val displayText = remember(text, verticalText) {
        if (verticalText) {
            text.toCharArray().joinToString(separator = "\n")
        } else {
            text
        }
    }
    Text(
        text = displayText,
        modifier = modifier.padding(8.dp),
        fontSize = 18.sp,
        color = MaterialTheme.colorScheme.primary,
    )
}

@Composable
fun PinchZoomLazyGridScope.ImageItem(
    index: Int,
    showText: Boolean,
    modifier: Modifier = Modifier,
) {
    val placeholder = remember(index) {
        val random = Random(index)
        Color(
            random.nextInt(100, 255),
            random.nextInt(100, 255),
            random.nextInt(100, 255),
        )
    }
    Box(modifier = modifier) {
        AsyncImage(
            model = "https://picsum.photos/seed/${index + 1000}/500/500",
            contentDescription = null,
            modifier = Modifier
                .pinchItem(key = index)
                .fillMaxWidth()
                .aspectRatio(1f)
                .background(placeholder),
        )
        if (showText) {
            Text(
                text = "$index",
                modifier = Modifier
                    .pinchItem(
                        key = "$index-text",
                        transitions = PinchItemTransitions.Translate,
                    )
                    .background(
                        color = Color.Black.copy(alpha = 0.8f),
                        shape = RoundedCornerShape(bottomEnd = 8.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                fontSize = 18.sp,

                )
        }
    }
}
