# Pinch Zoom Grid

A wrapper for `LazyVerticalGrid()` and `LazyHorizontalGrid()` to animate between multiple grid
cells.

### Demo

| Vertical                                                     | Horizontal                                                   | Invoke                                                       |
| ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| https://github.com/dokar3/pinch-zoom-grid/assets/68095777/c2986093-0631-4ddf-8ded-de073dfe59f6 | https://github.com/dokar3/pinch-zoom-grid/assets/68095777/0f8b087a-a83e-4b96-a0cc-2a93b6bc81a3 | https://github.com/dokar3/pinch-zoom-grid/assets/68095777/018bb468-6b16-43f7-8caa-d2a1bea28fd9 |


# Usages

### Basic

```kotlin
val cellsList = remember {
    listOf(
        GridCells.Fixed(4), // Zoom out to switch to this cells
        GridCells.Fixed(3),
        GridCells.Fixed(2), // Zoom in to switch to this cells
    )
}
val state = rememberPinchZoomLazyGridState(
    cellsList = cellsList,
    defaultCellsIndex = 1,
)

PinchZoomGridLayout(state = state) { // PinchZoomLazyGridScope
    LazyVerticalGrid(
        // The gridCells is a field of the PinchZoomLazyGridScope
        columns = gridCells,
        // The gridState is a field of the PinchZoomLazyGridScope
        state = gridState,
        // Use fillMaxSize() to avoid transition clipping
        modifier = Modifier.fillMaxSize(),
    ) {
        item(
            // key is required
            key = "header",
            span = { GridItemSpan(maxLineSpan) },
        ) {
            PhotoHeader(
                text = "Today",
                // Make sure the pinchItem modifier is applied to the item root
                // and pass the key
                modifier = Modifier.pinchItem(key = "header")
            )
        }

        items(
            count = 10,
            key = { index -> index }
        ) { index ->
            ImageItem(
                index = it,
                modifier = Modifier.pinchItem(key = index),
            )
        }
    }
}
```

### Transitions

It automatically works with non-fixed size layouts, for example:

```kotlin
Box(
    modifier = Modifier
        .pinchItem(key = index)
        .fillMaxWidth()
        .aspectRatio(1f)
)
```

If you have some layouts have fixed size in the item, such as `Text()`, scale transitions need to be disabled:

```kotlin
 Column(modifier = modifier) {
    AsyncImage(
        model = url,
        contentDescription = null,
        modifier = Modifier
            // Enable all transitions
            .pinchItem(key = index)
            .fillMaxWidth()
            .aspectRatio(1f)
            .background(placeholder),
    )
    Text(
        text = "$index",
        modifier = Modifier.pinchItem(
            // Use a different key
            key = "$index-text",
            // Enable only the translate transition
            transitions = PinchItemTransitions.Translate,
        )
    )
}
```

# License
```
Copyright 2023 dokar3

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
