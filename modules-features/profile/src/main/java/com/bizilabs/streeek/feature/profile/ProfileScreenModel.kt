package com.bizilabs.streeek.feature.profile

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.bizilabs.streeek.lib.domain.models.AccountDomain
import com.bizilabs.streeek.lib.domain.repositories.AccountRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.dsl.module

val profileModule = module {
    factory { ProfileScreenModel(accountRepository = get()) }
}

data class ProfileScreenState(
    val account: AccountDomain? = null
)

class ProfileScreenModel(
    private val accountRepository: AccountRepository
) : StateScreenModel<ProfileScreenState>(ProfileScreenState()) {

    init {
        getAccount()
        observeAccount()
    }

    private fun getAccount(){
        screenModelScope.launch {
            accountRepository.syncAccount()
        }
    }

    private fun observeAccount() {
        screenModelScope.launch {
            accountRepository.account.collectLatest { account ->
                mutableState.update { it.copy(account = account) }
            }
        }
    }

}