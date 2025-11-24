package com.example.fairshare.viewmodel

import androidx.lifecycle.ViewModel
import com.example.fairshare.ui.theme.AppTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor() : ViewModel() {

    private val _theme = MutableStateFlow<AppTheme>(AppTheme.DARK_1)
    val theme = _theme.asStateFlow()

    fun setTheme(newTheme: AppTheme) {
        _theme.value = newTheme
    }
}

