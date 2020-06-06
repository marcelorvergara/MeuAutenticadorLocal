package com.example.meuautenticador

import android.app.Service
import android.content.Intent
import android.os.IBinder

class InfnetAccountService : Service() {

    override fun onBind(intent: Intent): IBinder {
        val autenticador = InfnetAuthenticator(this)
        return autenticador.iBinder
    }
}
