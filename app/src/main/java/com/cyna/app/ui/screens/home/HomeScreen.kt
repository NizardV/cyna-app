package com.cyna.app.ui.screens.home

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.cyna.app.ui.core.Destination
import com.cyna.app.ui.core.components.input.PrimaryButton
import com.cyna.app.ui.core.components.layout.*
import com.cyna.app.ui.core.navigate

@Composable
fun HomeScreen(navController: NavController) {
    MainScaffold(navController = navController) { innerPadding ->
        CenteredBox(
            horizontalPadding = 16.dp
        ) {
            CenteredColumn(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Bienvenue dans Cyna App!",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium
                )

                ExtraLargeSpacer()

                MediumSpacer()

                PrimaryButton(
                    onClick = {
                        navController.navigate(Destination.Splash)
                    },
                    text = "Redemarrer l'application"
                )
            }
        }
    }
}