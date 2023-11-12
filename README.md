<div align="center">
<h1>Pinch Zoom Grid</h1>
<img alt="Maven Central" src="https://img.shields.io/maven-central/v/io.github.dokar3/pinchzoomgrid?style=flat-square&color=%232DBEC0">
<img alt="GitHub Workflow Status (with event)" src="https://img.shields.io/github/actions/workflow/status/dokar3/pinch-zoom-grid/android.yaml?style=flat-square">
</div>

<br />

A wrapper for `LazyVerticalGrid()` and `LazyHorizontalGrid()` to switch between multiple grid
cells using pinch gestures and animations.

### Demo

| Vertical                                                     | Horizontal                                                   | Invoke                                                       |
| ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| <video src="https://github.com/dokar3/pinch-zoom-grid/assets/68095777/c887f62f-a107-400d-a33e-0f1fc21bdd2f"/> | <video src="https://github.com/dokar3/pinch-zoom-grid/assets/68095777/246c34d4-0642-4f90-a906-8b995b54e6ba"/> | <video src="https://github.com/dokar3/pinch-zoom-grid/assets/68095777/be1cdd86-8550-4add-8b31-cb3101390c2e"/> |


# Usages

### Dependency

```kotlin
implementation("io.github.dokar3:pinchzoomgrid:LATEST_VERSION")
```

### Basic

```kotlin
val cellsList = remember {
    listOf(
        GridCells.Fixed(4), // Zoom out to switch to this cells
        GridCells.Fixed(3),
        GridCells.Fixed(2), // Zoom in to switch to this cells
    )
}
val state = rememberPinchZoomGridState(
    cellsList = cellsList,
    initialCellsIndex = 1,
)

PinchZoomGridLayout(state = state) { // PinchZoomGridScope
    LazyVerticalGrid(
        // The gridCells is a field of the PinchZoomGridScope
        columns = gridCells,
        // The gridState is a field of the PinchZoomGridScope
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
                // Make sure the pinchItem modifier is applied to the item layout
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

### Item transitions

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

### Offscreen item transitions

It's also possible to customize offscreen item transitions. The default value is `Alpha` + `Translate`.

```kotlin
AsyncImage(
    model = url,
    contentDescription = null,
    modifier = Modifier
        // Enable all transitions
        .pinchItem(key = index, offscreenTransitions = PinchItemTransitions.Translate)
        .fillMaxWidth()
        .aspectRatio(1f)
        .background(placeholder),
)
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
