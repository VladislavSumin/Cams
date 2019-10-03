package ru.vladislavsumin.cams.storage

import android.content.Context
import ru.vladislavsumin.core.rx.preferences.RxStringProperty
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CredentialStorage @Inject constructor(context: Context) {

    companion object {
        private val PROPERTIES_FILE = CredentialStorage::class.java.name
        private const val SERVER_ADDRESS = "server_address"
    }

    private val mPreferences = context.getSharedPreferences(PROPERTIES_FILE, Context.MODE_PRIVATE)

    private val mServerAddress = RxStringProperty(mPreferences, SERVER_ADDRESS, "")

    var serverAddress: String by mServerAddress


    val hasServerAddress
        get() = serverAddress.isNotEmpty()

    fun observeServerAddress() = mServerAddress.observe()

}