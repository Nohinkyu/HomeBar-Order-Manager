package com.devik.homebarordermanager.ui.screen.signin

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.navigation.NavController
import com.devik.homebarordermanager.BuildConfig
import com.devik.homebarordermanager.R
import com.devik.homebarordermanager.data.source.database.PreferenceManager
import com.devik.homebarordermanager.ui.component.navigation.NavigationRoute
import com.devik.homebarordermanager.util.Constants
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.Google
import io.github.jan.supabase.gotrue.providers.builtin.IDToken
import io.github.jan.supabase.postgrest.Postgrest
import kotlinx.coroutines.launch
import java.security.MessageDigest
import java.util.UUID

@Composable
fun SignInScreen(navController: NavController) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = LocalContext.current.getString(R.string.app_name),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.size(40.dp))
            GoogleSignInButton(navController)
        }
    }
}

@Composable
private fun GoogleSignInButton(navController: NavController) {

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val preferenceManager = PreferenceManager(context)
    val supabase = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_PROJECT_URL,
        supabaseKey = BuildConfig.SUPABASE_PROJECT_API_KEY
    ) {
        install(Auth)
        install(Postgrest)
    }

    val errorMessage = object {
        val SIGN_IN_CANCEL = "activity is cancelled by the user."
        val TO_MANY_CANCEL_SIGN_IN =
            "During begin sign in, failure response from one tap: 16: [28436] Caller has been temporarily blocked due to too many canceled sign-in prompts."
    }

    val onClick: () -> Unit = {
        val credentialManger = CredentialManager.create(context)

        val rawNonce = UUID.randomUUID().toString()
        val bytes = rawNonce.toString().toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        val hashedNonce = digest.fold("") { str, it -> str + "%02x".format(it) }

        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(BuildConfig.GOOGLE_CLIENT_ID)
            .setNonce(hashedNonce)
            .build()

        val request: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        coroutineScope.launch {
            try {
                val result = credentialManger.getCredential(
                    request = request,
                    context = context
                )
                val credential = result.credential
                val googleIdTokenCredential = GoogleIdTokenCredential
                    .createFrom(credential.data)

                val googleIdToken = googleIdTokenCredential.idToken

                supabase.auth.signInWith(IDToken) {
                    idToken = googleIdToken
                    provider = Google
                    nonce = rawNonce
                }

                val supabaseAuth = supabase.auth.currentUserOrNull()
                val userMetaData = supabaseAuth?.userMetadata
                val userImage = userMetaData?.get("avatar_url")

                preferenceManager.putString(Constants.KEY_MAIL_ADDRESS, supabaseAuth?.email.toString())
                preferenceManager.putString(Constants.KEY_PROFILE_IMAGE, userImage.toString())

                navController.navigate(NavigationRoute.ORDER_SCREEN) {
                    popUpTo(NavigationRoute.SIGN_IN_SCREEN) { inclusive = true }
                }

                Toast.makeText(
                    context,
                    context.getString(R.string.message_sign_in_success),
                    Toast.LENGTH_SHORT
                ).show()
            } catch (e: GetCredentialException) {
                if (e.message == errorMessage.SIGN_IN_CANCEL) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.message_sign_in_cancel),
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (e.message == errorMessage.TO_MANY_CANCEL_SIGN_IN) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.message_to_many_sign_in_cancel),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        context,
                        context.getString(R.string.message_sign_fail),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: GoogleIdTokenParsingException) {
                Toast.makeText(
                    context,
                    context.getString(R.string.message_sign_fail),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        color = Color.White,
        shadowElevation = 10.dp,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.height(40.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_google),
                contentDescription = "",
                modifier = Modifier.padding(start = 8.dp)
            )
            Text(
                text = context.getString(R.string.google_sign_in_button),
                modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                color = Color.Black,
                fontSize = 16.sp
            )
        }
    }
}