package com.example.meuautenticador

import android.accounts.Account
import android.accounts.AccountManager
import android.accounts.AccountManagerCallback
import android.accounts.AccountManagerFuture
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity


class StarterActivity : AppCompatActivity() {

    private lateinit var account: Account
    private lateinit var act: AccountManager

    private fun gravarDados(bundle: Bundle){
        var banco = getSharedPreferences("token_usu", Context.MODE_PRIVATE)
        var editor = banco.edit()
        editor.putString("usuario",bundle.getString(AccountManager.KEY_ACCOUNT_NAME))
        editor.putString("token",bundle.getString(AccountManager.KEY_AUTHTOKEN))
        editor.commit()
        Toast.makeText(this,"Dados Alterados!",Toast.LENGTH_LONG).show()
    }

    private fun adicionarConta(){
        val future: AccountManagerFuture<Bundle> = act.addAccount(
            "com.infnet.local",
            "full",
            null,
            null,
            this,
            AccountManagerCallback<Bundle> { future ->
                    val bnd = future.result
                    // Não pega o token -> login fake
                    val usuario = bnd.getString(AccountManager.KEY_ACCOUNT_NAME)
                    account = Account(usuario,"com.infnet.local")
                    verificarToken("")
            },
            null
        )
    }

    private fun verificarToken(token: String){
        val future: AccountManagerFuture<Bundle> = act.getAuthToken(
            account,
            "full",
            null,
            this,
            AccountManagerCallback<Bundle> { future ->
                    val bnd = future.result
                    val novoToken = bnd.getString(AccountManager.KEY_AUTHTOKEN)
                    if(novoToken!=token){
                        gravarDados(bnd)
                    }
            },
            null
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_starter)

        act = AccountManager.get(this)
        var banco = getSharedPreferences("token_usu", Context.MODE_PRIVATE)
        var token = banco.getString("token","")!!.trimEnd()
        var usuario = banco.getString("usuario","")!!.trimEnd()
        if(usuario==""){
            adicionarConta()
        } else {
            account = Account(usuario,"com.infnet.local")
            verificarToken(token)
        }
    }

    fun renovarToken(view: View) {
        act.clearPassword(account)
        verificarToken("")
    }

    fun alterarLogin(view: View) {
        adicionarConta()
    }
}
