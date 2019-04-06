package com.tuvv.tword.utils.extentions

import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity

/**
 * The `fragment` is added to the container view with id `frameId`. The operation is
 * performed by the `fragmentManager`.
 */
fun AppCompatActivity.replaceFragmentInActivity( @IdRes frameId: Int, fragment: Fragment) {

    //the thre first operation are needed to work with existing injected fragment
    supportFragmentManager
            .popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

    supportFragmentManager
            .beginTransaction()
            .remove(fragment)
            .commit()

    supportFragmentManager.executePendingTransactions()

    supportFragmentManager
            .beginTransaction()
            .replace(frameId, fragment)
            .commit()

}