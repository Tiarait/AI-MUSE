package ua.tiar.aim.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Link
import androidx.compose.material.icons.rounded.MailOutline
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ua.tiar.aim.AppConstants
import ua.tiar.aim.R
import ua.tiar.aim.Utils
import ua.tiar.aim.data.models.DialogAlertModel
import ua.tiar.aim.ui.components.BigButton
import ua.tiar.aim.ui.components.TypewriteText
import ua.tiar.aim.ui.components.pressClickEffect
import ua.tiar.aim.ui.theme.AIMuseTheme
import ua.tiar.aim.ui.theme.gradientText
import ua.tiar.aim.viewmodel.AppViewModel

@Composable
fun AboutScreen(
    isVisible: Boolean = true,
    appViewModel: AppViewModel = viewModel(),
) {

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = AppConstants.topBarHeight),
        ) {
            item(key = "about-card") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                        .height(120.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                    val logoAnimationCard by animateFloatAsState(
                        targetValue = if (isVisible) 1f else 0f,
                        animationSpec = tween(
                            durationMillis = 800,
                            delayMillis = if (isVisible) 200 else 0,
                            easing = LinearEasing
                        ), label = "about-card"
                    )
                    Image(
                        painter = painterResource(id = R.drawable.tiar),
                        contentDescription = "about_logo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(80.dp)
                            .scale(logoAnimationCard)
                            .alpha(logoAnimationCard)
                            .border(
                                BorderStroke(2.dp, MaterialTheme.colorScheme.gradientText),
                                CircleShape
                            )
                            .padding(6.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        val text = "Tiar Apps"
                        TypewriteText(
                            text = text,
                            spec = tween(durationMillis = 800, delayMillis = if (isVisible) 800 else 0, easing = LinearEasing),
                            isVisible = isVisible,
                            style = MaterialTheme.typography.titleMedium.copy(
                                brush = MaterialTheme.colorScheme.gradientText
                            ),
                            outlineWidth = 2f,
                            contentAlignment = Alignment.CenterStart,
                            modifier = Modifier
                        )
                        val textD = stringResource(id = R.string.about_text)
                        TypewriteText(
                            text = textD,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onBackground,
                                lineBreak = LineBreak.Paragraph.copy(strategy = LineBreak.Strategy.Simple)
                            ),
                            spec = tween(durationMillis = textD.length * 20, delayMillis = if (isVisible) 1600 else 0, easing = LinearEasing),
                            isVisible = isVisible,
                            modifier = Modifier
                        )
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val circleAnimate by animateFloatAsState(
                        targetValue = if (isVisible) 1f else 0f,
                        animationSpec = tween(
                            durationMillis = 400,
                            delayMillis = if (isVisible) 1000 else 0,
                            easing = LinearEasing
                        ), label = ""
                    )
                    val ctx = LocalContext.current
                    val link = "tiarait.github.io"
                    val mail = "tiar.develop@gmail.com"
                    BigButton(
                        modifier = Modifier
                            .weight(1f)
                            .alpha(circleAnimate)
                            .scale(circleAnimate)
                            .pressClickEffect(),
                        "Site", link,
                        Icons.Rounded.Link
                    ) {
                        appViewModel.addAlertDialog(
                            DialogAlertModel(
                                title = ctx.getString(R.string.open_link_q),
                                message = ctx.getString(R.string.open_link_d) + "\n\n$link",
                                positive = ctx.getString(R.string.open),
                                onPositiveClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://$link"))
                                    ctx.startActivity(intent)
                                })
                        )
                    }
                    BigButton(
                        modifier = Modifier
                            .weight(1f)
                            .alpha(circleAnimate)
                            .scale(circleAnimate)
                            .pressClickEffect(),
                        "Mail", mail,
                        Icons.Rounded.MailOutline
                    ) {
                        appViewModel.addAlertDialog(DialogAlertModel(
                            title = ctx.getString(R.string.open_link_q),
                            message = ctx.getString(R.string.open_link_d) + "\n\n$mail",
                            positive = ctx.getString(R.string.open),
                            onPositiveClick = {
                                val intent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", mail, null))
                                intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(mail))
                                intent.putExtra(Intent.EXTRA_SUBJECT, "AI MUSE")
                                ctx.startActivity(intent)
                            }))
                    }
                }

            }
            item {
                val textD = "version - " + Utils.getAppVersion(LocalContext.current)
                Box(modifier = Modifier.fillMaxWidth()) {
                    TypewriteText(
                        text = textD,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onBackground,
                            lineBreak = LineBreak.Paragraph.copy(strategy = LineBreak.Strategy.Simple)
                        ),
                        spec = tween(durationMillis = 800, delayMillis = 1200, easing = LinearEasing),
                        isVisible = isVisible,
                        modifier = Modifier
                            .alpha(.5f)
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 32.dp)
                    )
                }
            }
        }
    }
}



@Preview(showBackground = true)
@Composable
fun PreviewThirdScreen() {
    AIMuseTheme {
        AboutScreen()
    }
}