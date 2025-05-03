package koreea_colea_sasha.presentation.userinfo

import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import koreea_colea_sasha.ui.theme.DarkGray
import koreea_colea_sasha.ui.theme.interBold
import koreea_colea_sasha.ui.theme.interLight
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun UserName(viewModel : UserViewModel = viewModel()){

    Text(text = buildAnnotatedString {
        append("Hello, ")
        withStyle(
            style = SpanStyle(
                fontWeight = FontWeight.Bold,
                fontFamily = interBold,
                fontSize = 26.sp
            )
        ) {
            append("\n${viewModel.userName}!")
        }
    },
        fontWeight = FontWeight.Light,
        fontSize = 20.sp,
        fontFamily = interLight,
        color = DarkGray,
    )
}