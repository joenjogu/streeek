package com.bizilabs.streeek.feature.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.registry.screenModule
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import coil.compose.AsyncImage
import com.bizilabs.streeek.lib.common.navigation.SharedScreen
import com.bizilabs.streeek.lib.design.components.SafiCenteredColumn

val featureProfile = screenModule {
    register<SharedScreen.Profile> { ProfileScreen }
}

object ProfileScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.current
        val screenModel: ProfileScreenModel = getScreenModel()
        val state by screenModel.state.collectAsStateWithLifecycle()
        ProfileScreenContent(state = state) { navigator?.pop() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreenContent(
    state: ProfileScreenState,
    onClickNavigateBackIcon: () -> Unit
) {
    Scaffold(topBar = {
        TopAppBar(
            navigationIcon = {
                IconButton(onClick = onClickNavigateBackIcon) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = null
                    )
                }
            },
            title = {
                Text(
                    text = "Profile"
                )
            },
        )

    }) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            SafiCenteredColumn(modifier = Modifier.fillMaxWidth()) {
                state.account?.let { account ->
                    Card(
                        modifier = Modifier.padding(16.dp),
                        onClick = {},
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground)
                    ) {
                        AsyncImage(
                            modifier = Modifier
                                .size(150.dp)
                                .clip(RoundedCornerShape(50)),
                            model = state.account.avatarUrl,
                            contentDescription = "user avatar url",
                            contentScale = ContentScale.Crop
                        )
                    }
                    account.level?.let { level ->
                        Text(text = "LV.${level.number}")
                        Text(text = level.name)
                    }
                    Text(text = account.username)
                    Text(text = account.email)
                    Text(
                        modifier = Modifier.fillMaxWidth(0.75f),
                        text = account.bio,
                        textAlign = TextAlign.Center
                    )
                    Text(text = buildString {
                        append("Joined : ")
                        append(account.createdAt)
                    })
                }
            }
        }

    }
}