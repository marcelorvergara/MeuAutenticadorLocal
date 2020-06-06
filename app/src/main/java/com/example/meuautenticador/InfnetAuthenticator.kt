package com.example.meuautenticador

import android.accounts.*
import android.accounts.AccountManager.KEY_BOOLEAN_RESULT
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils


class InfnetAuthenticator(val context: Context) :
      AbstractAccountAuthenticator(context) {

    private val ACCOUNT_TYPE = "com.infnet.local"

    companion object {
        const val ADD_ACCOUNT = "addAccount"
        const val TOKEN_TYPE = "tokenType"
        const val PASSWORD = "password"
    }

    @Throws(NetworkErrorException::class)
    override fun confirmCredentials(
        response: AccountAuthenticatorResponse?,
        account: Account?, options: Bundle?
    ): Bundle? { return null }

    override fun editProperties(
        response: AccountAuthenticatorResponse?,
        accountType: String?): Bundle? { return null }

    override fun getAuthTokenLabel(s: String): String {
        return "full"
    }

    @Throws(NetworkErrorException::class)
    override fun hasFeatures(
        response: AccountAuthenticatorResponse?,
        account: Account?, features: Array<String?>?
    ): Bundle {
        val result = Bundle()
        result.putBoolean(KEY_BOOLEAN_RESULT, false)
        return result
    }

    @Throws(NetworkErrorException::class)
    override fun updateCredentials(
        response: AccountAuthenticatorResponse?,
        account: Account?, authTokenType: String?,
        options: Bundle?): Bundle? { return null }

    @Throws(NetworkErrorException::class)
    override fun addAccount(
        response: AccountAuthenticatorResponse?,
        accountType: String?,
        authTokenType: String?,
        requiredFeatures: Array<String>?,
        options: Bundle?
    ): Bundle {
        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, accountType)
        intent.putExtra(ADD_ACCOUNT, true)
        intent.putExtra(TOKEN_TYPE, "full")
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
        val bundle = Bundle()
        bundle.putParcelable(AccountManager.KEY_INTENT, intent)
        return bundle
    }
//        intent.putExtra(Config.ARG_ACCOUNT_TYPE, accountType)
//        intent.putExtra(Config.ARG_AUTH_TYPE, authTokenType)
//        intent.putExtra(Config.ARG_IS_ADDING_NEW_ACCOUNT, true)
//        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)




    @Throws(NetworkErrorException::class)
    override fun getAuthToken(
        response: AccountAuthenticatorResponse?,
        account: Account?,
        authTokenType: String?,
        options: Bundle?
    ): Bundle {
        // Extract the username and password from the Account Manager, and ask
        // the server for an appropriate AuthToken.
        val accountManager = AccountManager.get(context)
        var authToken = accountManager.peekAuthToken(account, authTokenType)

        // Se o token estiver vazio, tenta logar
        if (TextUtils.isEmpty(authToken)) {
            val password = accountManager.getPassword(account)
            if (password != null) {
                authToken = DummyServer(context).authenticate(account!!.name, password)
            }
        }

        // Conseguindo um token, retorna ele
        if (!TextUtils.isEmpty(authToken)) {
            val result = Bundle()
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account!!.name)
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type)
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken)
            return result
        }

        // Se não consegue logar, mostra novamente a tela de login
        val intent = Intent(context, MainActivity::class.java)
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
        intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, account!!.type)
        intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, account.name)
        intent.putExtra(TOKEN_TYPE, authTokenType)
        val bundle = Bundle()
        bundle.putParcelable(AccountManager.KEY_INTENT, intent)
        return bundle
    }

}