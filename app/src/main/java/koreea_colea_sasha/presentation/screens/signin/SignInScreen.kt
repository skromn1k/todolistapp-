package koreea_colea_sasha.presentation.screens.signin

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.koreea_colea_sasha.R
import koreea_colea_sasha.ui.theme.*
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import koreea_colea_sasha.ui.theme.DarkGray
import koreea_colea_sasha.ui.theme.MainBackground
import koreea_colea_sasha.ui.theme.archivoLight
import koreea_colea_sasha.ui.theme.interBold
import koreea_colea_sasha.ui.theme.interLight
import koreea_colea_sasha.ui.theme.interMedium

@Composable
fun SignInScreen(navController: NavController) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MainBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .padding(top = 100.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            AppLogo()
            AppTitle()
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            VectorArt()
            AppDescription()
        }

        SignInWithGoogleButton(navController)
    }
}

@Composable
fun AppDescription() {
    Text(
        modifier = Modifier.width(260.dp),
        textAlign = TextAlign.Center,
        fontFamily = archivoLight,
        fontSize = 14.sp,
        text = stringResource(R.string.app_description)
    )
}

@Composable
fun AppLogo() {
    Box(
        modifier = Modifier
            .shadow(10.dp, RoundedCornerShape(20.dp), true)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .size(120.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.signin_check_icon),
            contentDescription = "",
            modifier = Modifier.size(90.dp),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun AppTitle() {
    Text(
        modifier = Modifier.padding(start = 20.dp, top = 20.dp),
        text = buildAnnotatedString {
            append("Welcome to ")
            withStyle(
                style = SpanStyle(
                    fontWeight = FontWeight.Bold,
                    fontFamily = interBold,
                    fontSize = 28.sp
                )
            ) {
                append("\nTodo List")
            }
        },
        fontWeight = FontWeight.Light,
        fontSize = 18.sp,
        fontFamily = interLight,
        color = DarkGray
    )
}

@Composable
fun SignInWithGoogleButton(navController: NavController) {
    val context = LocalContext.current
    val oneTapClient = remember { Identity.getSignInClient(context) }

    val signInRequest = remember {
        BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(context.getString(R.string.default_web_client_id)) // из google-services.json
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        try {
            val credential = oneTapClient.getSignInCredentialFromIntent(result.data)
            val idToken = credential.googleIdToken

            if (idToken != null) {
                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                Firebase.auth.signInWithCredential(firebaseCredential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(context, "Google Sign-In Successful", Toast.LENGTH_SHORT).show()
                            navController.navigate("home_screen")
                        } else {
                            Toast.makeText(context, "Sign-In Failed", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(context, "No ID token!", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Sign-In Error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom
    ) {
        Button(
            onClick = {
                oneTapClient.beginSignIn(signInRequest)
                    .addOnSuccessListener { result ->
                        val intentSenderRequest = IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                        launcher.launch(intentSenderRequest)
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "One Tap Sign-In Failed: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            },
            modifier = Modifier
                .shadow(12.dp, RoundedCornerShape(10.dp), true)
                .clip(RoundedCornerShape(10.dp))
                .width(300.dp)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.White,
                contentColor = DarkGray
            )
        ) {
            Text(text = "Continue with Google", fontFamily = interMedium)
        }

        Spacer(modifier = Modifier.height(50.dp))
    }
}
@Composable
fun VectorArt() {
    Image(
        painter = painterResource(id = R.drawable.signin_vector_art),
        contentDescription = "",
        modifier = Modifier
            .width(280.dp)
            .height(230.dp)
    )
}
