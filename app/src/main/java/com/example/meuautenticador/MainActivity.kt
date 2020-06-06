package com.example.meuautenticador

import android.accounts.Account
import android.accounts.AccountAuthenticatorActivity
import android.accounts.AccountManager
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AccountAuthenticatorActivity() {

    private lateinit var accountManager: AccountManager
    private lateinit var dummyServer: DummyServer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        accountManager = AccountManager.get(baseContext)
        dummyServer = DummyServer(baseContext)
        user.setText(intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME))
    }

    fun getBundle(userId: String, accountType:String?,
                  authToken: String?, passwd: String): Bundle{
        val data = Bundle()
        data.putString(AccountManager.KEY_ACCOUNT_NAME, userId)
        val acctType = accountType ?: "com.infnet.local"
        data.putString(AccountManager.KEY_ACCOUNT_TYPE, acctType)
        data.putString(AccountManager.KEY_AUTHTOKEN, authToken)
        data.putString(InfnetAuthenticator.PASSWORD,passwd)
        data.putString(InfnetAuthenticator.TOKEN_TYPE,"full")
        return data
    }

    fun createAccount(view: View?) {
        val userId = user.text.toString()
        val passwd = password.text.toString()
        val authToken = dummyServer.createUser(userId,passwd)

        if(TextUtils.isEmpty(authToken)){
            Toast.makeText(this,"Erro ao criar usuário",Toast.LENGTH_LONG).show()
            return
        }

        val accountType = intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE)
        val result = Intent()
        val dados = getBundle(userId,accountType,authToken,passwd)
        dados.putBoolean(InfnetAuthenticator.ADD_ACCOUNT,true)
        result.putExtras(dados)
        setLoginResult(result)
    }

    fun login(view: View?) {
        val userId = user.text.toString()
        val passwd = password.text.toString()
        val authToken = dummyServer.authenticate(userId,passwd)

        if(TextUtils.isEmpty(authToken)){
            Toast.makeText(this,"Erro ao efetuar login",Toast.LENGTH_LONG).show()
            return
        }

        val accountType = intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE)
        val result = Intent()
        result.putExtras(getBundle(userId,accountType,authToken,passwd))
        setLoginResult(result)
    }

    private fun accountExists(account: Account): Boolean {
        accountManager!!.accounts.filter{
            t -> t.type == "com.infnet.local"
        }.forEach{
            t -> if(t.name==account.name)
                return true
        }
        return false
    }

    private fun setLoginResult(intent: Intent) {
        val userId = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
        val passWd = intent.getStringExtra(InfnetAuthenticator.PASSWORD)
        val account =
            Account(userId, intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE))
//        if (getIntent().getBooleanExtra(InfnetAuthenticator.ADD_ACCOUNT, false)){
        if(!accountExists(account)){
            accountManager!!.addAccountExplicitly(account, passWd, null)
        }
        val authtoken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN)
        val tokenType = intent.getStringExtra(InfnetAuthenticator.TOKEN_TYPE)
        accountManager!!.setAuthToken(account, tokenType, authtoken)
        accountManager!!.setPassword(account, passWd)
//        var x = accountManager!!.getPassword(account)
//        Log.d("D", x)
        setAccountAuthenticatorResult(intent.extras)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

}
