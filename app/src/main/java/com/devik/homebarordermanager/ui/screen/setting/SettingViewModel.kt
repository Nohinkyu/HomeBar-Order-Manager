package com.devik.homebarordermanager.ui.screen.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devik.homebarordermanager.data.source.database.PreferenceManager
import com.devik.homebarordermanager.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jan.supabase.gotrue.Auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val preferenceManager: PreferenceManager,
    private val supabseAuth: Auth
) :
    ViewModel() {

    private val _signOutDialogState = MutableStateFlow<Boolean>(false)
    val signOutDialogState: StateFlow<Boolean> = _signOutDialogState

    private val _userMail =
        MutableStateFlow<String>(preferenceManager.getString(Constants.KEY_MAIL_ADDRESS, ""))
    val userMail: StateFlow<String> = _userMail

    fun openSignOutDialog() {
        _signOutDialogState.value = true
    }

    fun closeSignOutDialog() {
        _signOutDialogState.value = false
    }

    fun signOut() {
        viewModelScope.launch {
            supabseAuth.signOut()
            preferenceManager.removeString(Constants.KEY_MAIL_ADDRESS)
            preferenceManager.removeString(Constants.KEY_PROFILE_IMAGE)
        }
    }
}