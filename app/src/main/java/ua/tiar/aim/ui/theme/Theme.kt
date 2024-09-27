package ua.tiar.aim.ui.theme

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.LocalView
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import ua.tiar.aim.R

private val DarkColorScheme = darkColorScheme(
    primaryContainer = Color(0xFF3B485F),
    primary = Color(0xFF19CFCF),//color select
    inversePrimary = Color(0xFF6F55E0),
    onPrimary = Color.White, //buttons text
    background = Color(0xFF212732),
    onBackground = Color.White,
    onSurface = Color.White,
    outline = Color(0xFF6C84AD),
    tertiary = Color.Cyan,//accent
    onTertiary = Color.Cyan,//accent inverse
    tertiaryContainer = Color(0xFF1E8D8D),
    onError = Color(0xFFDAB0B0),
    outlineVariant = Color(0xFF40B3EC)//focused border
)

//private val LightColorScheme = DarkColorScheme //TODO
private val LightColorScheme = lightColorScheme(
    primaryContainer = Color(0xF2FFFFFF),
    primary = Color(0xFFFF5B00),//color select
    inversePrimary = Color(0xFF2394CC),
    onPrimary = Color(0xFF212732), //buttons text
    background = Color(0xFFDEF4FF),
    onBackground = Color(0xFF212732),
    onSurface = Color(0xFFE6EEFD),
    outline = Color(0xFF212732),
    tertiary = Color(0xFFFF5B00),//accent
    onTertiary = Color(0xFFFF5B00),//accent inverse
    tertiaryContainer = Color(0xFF944000),
    onError = Color(0xFFDAB0B0),
    outlineVariant = Color(0xFF40B3EC)//focused border
)

fun dynamicLightColorScheme(): ColorScheme {
    return LightColorScheme
}

fun dynamicDarkColorScheme(): ColorScheme {
    return DarkColorScheme
}


val ColorScheme.colorTopBar: Color
    @Composable
    get() = if (isSystemInDarkTheme()) Color(0xFF2A3241) else Color(0xF2FFFFFF)//Color(0xFFFFFFFF)

val ColorScheme.colorGradientMainCenter: Color
    @Composable
    get() = if (isSystemInDarkTheme()) Color(0xFF3B485F) else Color(0xF2FFFFFF)//Color(0xFFFFFFFF)

val ColorScheme.colorOnView: Color
    @Composable
    get() = if (isSystemInDarkTheme()) Color(0xFF212732) else Color(0xF2E7F6FD)//Color(0xFFFFFFFF)


val ColorScheme.gradientText: Brush
    @Composable
    get() = Brush.linearGradient(
        colors =
            if (isSystemInDarkTheme()) listOf(Color.Cyan, Color(0xFF8F70E6), Color(0xFFFF29AA), Color(0xFF8F70E6))
            else listOf(
                Color(0xFFD50000),
                Color(0xFFFFD600),
                Color(0xFFE930FE),
                Color(0xFFC9005A)
            ),
        start = Offset(0f, Float.POSITIVE_INFINITY),
        end = Offset(Float.POSITIVE_INFINITY, 0f),
        tileMode = TileMode.Repeated
    )



@Composable
fun AIMuseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme() else dynamicLightColorScheme()
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as ComponentActivity).window
            window.statusBarColor = ContextCompat.getColor(view.context, R.color.status)
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}