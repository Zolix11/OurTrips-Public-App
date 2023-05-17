package hu.bme.aut.ourtrips.common.composable.ext

import android.util.Patterns

private const val MIN_PASS_LENGHT = 6
fun String.isValidEmail() : Boolean{
    return this.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isValidPassword() : Boolean{
    return this.isNotBlank() && this.length>= MIN_PASS_LENGHT
}

fun String.passwordMatches(repeatedPassword : String): Boolean {
    return this == repeatedPassword
}
