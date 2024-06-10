package com.forsythe.iris.presentation.screens.home

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.forsythe.iris.data.room.IrisDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val irisDao: IrisDao
): ViewModel() {
     fun getRecords() = irisDao.getAllMessageRecords().stateIn(
         scope = viewModelScope,
         started = SharingStarted.WhileSubscribed(),
         initialValue = emptyList(),

     )
}