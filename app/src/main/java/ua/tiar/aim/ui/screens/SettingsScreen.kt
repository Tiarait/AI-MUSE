package ua.tiar.aim.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.EditNote
import androidx.compose.material.icons.rounded.NoAdultContent
import androidx.compose.material.icons.rounded.Source
import androidx.compose.material.icons.rounded.ViewCarousel
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ua.tiar.aim.AppConstants
import ua.tiar.aim.R
import ua.tiar.aim.data.models.DialogAlertModel
import ua.tiar.aim.ui.components.BigButton
import ua.tiar.aim.ui.components.pressClickEffect
import ua.tiar.aim.ui.components.settings.SettingsGroup
import ua.tiar.aim.ui.components.settings.SettingsSwitchComp
import ua.tiar.aim.ui.theme.AIMuseTheme
import ua.tiar.aim.viewmodel.AppViewModel

@Composable
fun SettingsScreen(
    appViewModel: AppViewModel = viewModel(),
) {
    val settings = appViewModel.settingsRepository!!
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(top = 8.dp)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(AppConstants.topBarHeight + 16.dp))
            SettingsGroup {
                SettingsSwitchComp(
                    name = R.string.setting_remember_prompt,
                    icon = Icons.Rounded.EditNote,
                    state = settings.isNeedSaveLastFlow.collectAsState().value
                ) {
                    settings.isNeedSaveLast = !settings.isNeedSaveLast
                }

                val noAvailable = stringResource(id = R.string.not_available)
                val noAvailablePro = stringResource(id = R.string.not_available_pro)
                SettingsSwitchComp(
                    isPro = true,
                    name = R.string.setting_nsfw_filtered,
                    icon = Icons.Rounded.NoAdultContent,
                    state = appViewModel.settingsRepository.filteredNsfw
                ) {
                    appViewModel.addAlertDialog(
                        DialogAlertModel(
                            title = noAvailable,
                            message = noAvailablePro,
                            positive = "Ok",
                            cancelable = false)
                    )
                }
                SelectSource(appViewModel)
            }

        }
    }
    BackHandler {
        appViewModel.hideSettings()
    }
}

@Composable
fun SelectSource(appViewModel: AppViewModel = viewModel()) {
    Surface(
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.ViewCarousel,
                    contentDescription = stringResource(id = R.string.setting_sources),
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = stringResource(id = R.string.setting_sources),
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Start,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            val ctx = LocalContext.current
            val curSource = appViewModel.settingsRepository!!.curSourceFlow.collectAsState().value.lowercase()
            LazyRow(modifier = Modifier) {
                item {
                    BigButton(
                        modifier = Modifier
                            .padding(2.dp)
                            .alpha(if (curSource == AppConstants.ARTBREEDER.lowercase()) 1f else .7f)
                            .weight(1f)
                            .pressClickEffect(),
                        border = if (curSource == AppConstants.ARTBREEDER.lowercase())
                            BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary)
                        else null,
                        title = AppConstants.ARTBREEDER,
                        description = "artbreeder.com",
                        imageVector = Icons.Rounded.Source
                    ) {
                        val sourceDesc = """
                            + Flexible settings.
                            + Several models.
                            + Image to Image.
                            + High resolution.
                            
                            - Difficulty in setup.
                            - Long generation.
                            
                            
                            https://www.artbreeder.com/
                        """.trimIndent()
                        appViewModel.addAlertDialog(
                            DialogAlertModel(
                                title = AppConstants.ARTBREEDER,
                                message = sourceDesc,
                                positive = ctx.getString(R.string.select),
                                onPositiveClick = {
                                    appViewModel.settingsRepository.curSource = AppConstants.ARTBREEDER.lowercase()
                                })
                        )
                    }
                }

                item {
                    BigButton(
                        modifier = Modifier
                            .padding(2.dp)
                            .alpha(if (curSource == AppConstants.PERCHANCE.lowercase()) 1f else .7f)
                            .weight(1f)
                            .pressClickEffect(),
                        border = if (curSource == AppConstants.PERCHANCE.lowercase())
                            BorderStroke(1.dp, MaterialTheme.colorScheme.tertiary)
                        else null,
                        title = AppConstants.PERCHANCE,
                        description = "perchance.org",
                        imageVector = Icons.Rounded.Source
                    ) {
                        val sourceDesc = """
                            + Fast generation.
                            + Ease of use.
                            
                            - Small resolution.
                            
                            
                            https://perchance.org/
                        """.trimIndent()
                        appViewModel.addAlertDialog(
                            DialogAlertModel(
                                title = AppConstants.PERCHANCE,
                                message = sourceDesc,
                                positive = ctx.getString(R.string.select),
                                onPositiveClick = {
                                    appViewModel.settingsRepository.curSource = AppConstants.PERCHANCE.lowercase()
                                })
                        )
                    }
                }
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.tertiary.copy(.4f))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSettingsScreen() {
    AIMuseTheme {
        SettingsGroup {
            SettingsSwitchComp(
                name = R.string.setting_remember_prompt,
                icon = Icons.Rounded.EditNote,
                state = false
            ) {

            }
        }
    }
}