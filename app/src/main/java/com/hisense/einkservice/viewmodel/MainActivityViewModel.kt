package com.hisense.einkservice.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.hisense.einkservice.model.EinkApp
import com.hisense.einkservice.repository.EinkAppRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MainActivityViewModel(
    private val repository: EinkAppRepository,
) : ViewModel() {

    val apps: Flow<List<EinkApp>> = repository.getAll()

    fun removeApp(app: EinkApp) {
        viewModelScope.launch {
            repository.delete(app)
        }
    }
}

class MainActivityViewModelFactory(
    private val repository: EinkAppRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainActivityViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
