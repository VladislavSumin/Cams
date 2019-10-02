package ru.vladislavsumin.cams.storage

import android.content.Context
import com.f2prateek.rx.preferences2.RxSharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CredentialStorage @Inject constructor(context: Context) {
    companion object {
        private val PROPERTIES_FILE = CredentialStorage::class.java.name
        private const val SERVER_ADDRESS = "server_address"
    }

    private val mPreferences = RxSharedPreferences.create(
            context.getSharedPreferences(PROPERTIES_FILE, Context.MODE_PRIVATE)
    )

    private val mServerAddressPref = mPreferences.getString(SERVER_ADDRESS)

    var serverAddress: String?
        set(v) {
            if (v != null) mServerAddressPref.set(v)
            else mServerAddressPref.delete()
        }
        get() = mServerAddressPref.get()


    val hasServerAddress
        get() = mServerAddressPref.isSet

    fun observeServerAddress() = mServerAddressPref.asObservable()

}