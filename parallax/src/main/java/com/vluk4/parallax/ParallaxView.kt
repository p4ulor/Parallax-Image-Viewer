package com.vluk4.parallax

import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.vluk4.example.parallax.model.ContentSettings
import com.vluk4.example.parallax.model.ParallaxOrientation
import com.vluk4.example.parallax.model.SensorData
import com.vluk4.example.parallax.sensor.SensorDataManager
import com.vluk4.parallax.model.Content
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

private const val INITIAL_VERTICAL_OFFSET = 0.5f

@Composable
fun ParallaxView(
    modifier: Modifier = Modifier,
    backgroundContent: Content = Content(),
    middleContent: Content = Content(),
    foregroundContent: Content = Content(),
    movementIntensityMultiplier: Int = 20,
    verticalOffsetLimit: Float = 0.5f,
    horizontalOffsetLimit: Float = 0.5f,
    orientation: ParallaxOrientation = ParallaxOrientation.Full
) {
    val configuration = LocalConfiguration.current
    val context = LocalContext.current

    val data = getSensorData(context, configuration)

    ParallaxViewContent(
        data = data,
        backgroundContent = backgroundContent,
        middleContent = middleContent,
        foregroundContent = foregroundContent,
        modifier = modifier,
        depthMultiplier = movementIntensityMultiplier,
        orientation = orientation,
        horizontalLimit = horizontalOffsetLimit,
        verticalLimit = verticalOffsetLimit,
    )
}

@Composable
private fun ParallaxViewContent(
    data: SensorData,
    modifier: Modifier,
    backgroundContent: Content = Content(),
    middleContent: Content = Content(),
    foregroundContent: Content = Content(),
    depthMultiplier: Int,
    horizontalLimit: Float,
    verticalLimit: Float,
    orientation: ParallaxOrientation,
) {
    val backgroundAlignment: Alignment = backgroundContent.settings.alignment
    val middleAlignment: Alignment = middleContent.settings.alignment
    val foregroundAlignment: Alignment = foregroundContent.settings.alignment
    val backgroundScaleMultiplier: Float = backgroundContent.settings.scale
    val middleScaleMultiplier: Float = middleContent.settings.scale
    val foregroundScaleMultiplier: Float = foregroundContent.settings.scale

    var roll = data.roll.coerceIn(getRange(horizontalLimit)).times(depthMultiplier)
    var pitch = data.pitch.coerceIn(getRange(verticalLimit)).plus(INITIAL_VERTICAL_OFFSET)
        .times(depthMultiplier)

    when (orientation) {
        ParallaxOrientation.Horizontal -> pitch = 0f
        ParallaxOrientation.Vertical -> roll = 0f
        else -> Unit
    }

    backgroundContent?.let { background ->
        Box(modifier = modifier.clipToBounds()) {
            Box(
                modifier = Modifier
                    .scale(backgroundScaleMultiplier)
                    .offset {
                        IntOffset(
                            x = ((1.5 * -roll.dp.roundToPx()).toInt()),
                            y = (1.5 * (pitch.dp.roundToPx())).toInt()
                        )
                    }
                    .align(backgroundAlignment),
                content = { background.composableContentOrNothing() }
            )

            middleContent?.let { middle ->
                Box(
                    modifier = Modifier
                        .scale(middleScaleMultiplier)
                        .align(middleAlignment),
                    content = { middle.composableContentOrNothing() }
                )
            }

            foregroundContent?.let { foreground ->
                Box(
                    modifier = Modifier
                        .scale(foregroundScaleMultiplier)
                        .offset {
                            IntOffset(
                                x = roll.dp.roundToPx(),
                                y = -pitch.dp.roundToPx()
                            )
                        }
                        .align(foregroundAlignment),
                    content = { foreground.composableContentOrNothing() }
                )
            }
        }
    }
}

private fun getRange(value: Float): ClosedFloatingPointRange<Float> {
    val transformedValue = value * 1.5f
    return -transformedValue..transformedValue
}

@Composable
private fun getSensorData(context: Context, configuration: Configuration) : SensorData {
    val scope = rememberCoroutineScope()
    var data by remember { mutableStateOf(SensorData()) }
    val deviceOrientation = configuration.orientation

    DisposableEffect(deviceOrientation) {
        val dataManager = SensorDataManager(context)
        dataManager.init(deviceOrientation)

        val job = scope.launch {
            dataManager.data
                .receiveAsFlow()
                .onEach {
                    if(it!=data){ //avoids flickering
                        data = it
                    }
                }
                .collect()
        }

        onDispose {
            dataManager.cancel()
            job.cancel()
        }
    }
    return data
}