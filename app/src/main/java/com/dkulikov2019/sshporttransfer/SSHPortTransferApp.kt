package com.dkulikov2019.sshporttransfer

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import java.security.Security
import javax.crypto.KeyAgreement
import org.bouncycastle.jce.provider.BouncyCastleProvider

@HiltAndroidApp
class SSHPortTransferApp : Application() {

	override fun onCreate() {
		super.onCreate()
		// Android ships an older BC provider; replace it so X25519 is available for SSH key exchange.
		Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME)
		Security.insertProviderAt(BouncyCastleProvider(), 1)
		logCryptoDiagnostics()
	}

	private fun logCryptoDiagnostics() {
		val tag = "CryptoDiagnostics"
		val provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME)
		if (provider == null) {
			Log.w(tag, "BC provider is not registered")
			return
		}

		val x25519Supported = runCatching {
			KeyAgreement.getInstance("X25519", BouncyCastleProvider.PROVIDER_NAME)
		}.isSuccess

		Log.i(
			tag,
			"Active BC provider: ${provider.name} (${provider.javaClass.name}), version=${provider.version}, x25519Supported=$x25519Supported"
		)
	}
}
