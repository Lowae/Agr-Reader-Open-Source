package com.lowae.agrreader.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.lowae.agrreader.R
import com.lowae.agrreader.ui.component.base.DynamicSVGImage
import com.lowae.agrreader.ui.component.base.LogoText
import com.lowae.agrreader.ui.page.common.CheckProDialogRouter
import com.lowae.agrreader.ui.page.common.RouteName
import com.lowae.agrreader.ui.svg.illustrations.AgrPro
import com.lowae.agrreader.ui.svg.illustrations.Illustrations

fun showProCheckDialog(
    navHostController: NavHostController,
    description: String? = null
) {
    CheckProDialogRouter.navigate(navHostController, description)
}

@Composable
fun CheckProDialogContent(navController: NavHostController, description: String) {
    Box(
        modifier = Modifier
            .sizeIn(minWidth = 280.dp, maxWidth = 560.dp),
        propagateMinConstraints = true
    ) {
        Surface(
            modifier = Modifier,
            shape = AlertDialogDefaults.shape,
            color = AlertDialogDefaults.containerColor,
            tonalElevation = AlertDialogDefaults.TonalElevation,
        ) {
            Column(
                modifier = Modifier.padding(PaddingValues(all = 24.dp))
            ) {
                Box(
                    Modifier
                        .padding(bottom = 16.dp)
                        .align(Alignment.CenterHorizontally)
                ) {
                    DynamicSVGImage(
                        modifier = Modifier.size(42.dp),
                        svgImageString = Illustrations.AgrPro
                    )
                }
                Box(
                    // Align the title to the center when an icon is present.
                    Modifier
                        .padding(bottom = 16.dp)
                        .align(
                            Alignment.CenterHorizontally
                        )
                ) {
                    LogoText(
                        text = stringResource(R.string.agr_reader_pro),
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
                Box(
                    Modifier
                        .weight(weight = 1f, fill = false)
                        .padding(bottom = 24.dp)
                        .align(Alignment.Start)
                ) {
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                Box(modifier = Modifier.align(Alignment.End)) {
                    Row {
                        TextButton(onClick = { navController.popBackStack() }) {
                            Text(text = stringResource(R.string.cancel))
                        }
                        TextButton(onClick = {
                            navController.popBackStack()
                            navController.navigate(RouteName.PRO_PAY)
                        }) {
                            Text(text = stringResource(R.string.confirm))
                        }
                    }
                }
            }
        }
    }
}