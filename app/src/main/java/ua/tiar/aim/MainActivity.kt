package ua.tiar.aim

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.room.Room
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.coroutines.delay
import ua.tiar.aim.db.AppDatabase
import ua.tiar.aim.network.HttpClientProvider
import ua.tiar.aim.repository.ApiRepository
import ua.tiar.aim.repository.DbRepository
import ua.tiar.aim.settings.AppSettings
import ua.tiar.aim.ui.screens.ComposeApp
import ua.tiar.aim.viewmodel.AppViewModel
import ua.tiar.aim.viewmodel.AppViewModelFactory

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalCoilApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
//        window.decorView.setBackgroundColor(ContextCompat.getColor(this, R.color.bg))
//        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = true
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "aicat-database"
        ).build()
        val settings = AppSettings(this)
        val apiRepository: ApiRepository = if (settings.curSource == AppConstants.PERCHANCE.lowercase()) {
            ua.tiar.aim.network.perchance.ApiService(HttpClientProvider.provideHttpClient())
        } else {
            ua.tiar.aim.network.artbreeder.ApiService(HttpClientProvider.provideHttpClient())
        }
        val appViewModel: AppViewModel by viewModels {
            AppViewModelFactory(
                DbRepository(db),
                apiRepository,
                settings
            )
        }

//        installSplashScreen().setKeepOnScreenCondition{
//            appViewModel.isSplashShow.value
//        }
        setContent {
            val isSplashShow by appViewModel.isSplashShow.collectAsState()
//            val imageLoader = ImageLoader(LocalContext.current)
//            imageLoader.memoryCache?.clear()
//            imageLoader.diskCache?.clear()
            val curSource by appViewModel.settingsRepository!!.curSourceFlow.collectAsState()
            LaunchedEffect(curSource) {
                appViewModel.changeSource()
            }

            val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.logo_ai_muse))
            var r by remember { mutableStateOf(false) }
            if (r) LottieAnimation(
                modifier = Modifier.padding(64.dp),
                composition = composition,
                contentScale = ContentScale.Inside,
            )
            LaunchedEffect(Unit) {
                delay(100)
                r = true
            }
            AnimatedVisibility(
                visible = !isSplashShow,
                modifier = Modifier.fillMaxSize(),
                enter = fadeIn(tween(800)),
                exit = fadeOut(tween(800))
            ) {
                ComposeApp(appViewModel)
            }
        }
    }

    @OptIn(ExperimentalCoilApi::class)
    override fun finish() {
        val imageLoader = ImageLoader(this)
        imageLoader.memoryCache?.clear()
        imageLoader.diskCache?.clear()
//        finishAndRemoveTask()
        super.finish()
    }
}