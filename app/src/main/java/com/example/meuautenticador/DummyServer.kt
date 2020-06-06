package com.example.meuautenticador

import android.content.Context
import java.util.*

class DummyServer(val context: Context) {

    // Apenas para demonstração
    // Não utiliza EncryptedSharedPreferences

    val cripto = Criptografador()

    fun createToken(): String{
        return cripto.getHash("XPTO"+Date().toString())
    }

    fun authenticate(name: String, password: String): String?{
        var banco = context.getSharedPreferences("usuarios", Context.MODE_PRIVATE)
        if(!banco.contains(name))
            return null
        var passEnc = banco.getString(name,"")
        passEnc = passEnc!!.trimEnd()
        if(passEnc!! == cripto.getHash(password))
            return createToken()
        else
            return null
    }

    fun createUser(name: String, password: String): String?{
        var banco = context.getSharedPreferences("usuarios", Context.MODE_PRIVATE)
        if(name==""||password==""||banco.contains(name))
            return null
        var passEnc = cripto.getHash(password)
        val editor = banco.edit()
        editor.putString(name,passEnc)
        editor.commit()
        return createToken()
    }



}