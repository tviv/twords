package com.tuvv.tword

import android.content.Context
import android.os.Looper
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import com.tuvv.tword.utils.sound.MediaPlayerManager
import com.tuvv.tword.utils.sound.SoundPlayer
import org.junit.Assert
import org.junit.Before
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


@RunWith(AndroidJUnit4::class)
class MediaPlayerManagerTest {

    lateinit var context: Context

    @Before
    fun setVars() {
        context = InstrumentationRegistry.getTargetContext()
    }

    @Test
    fun playSound_TwiceSame() {
        val mp = MediaPlayerManager(context)

        val signal = CountDownLatch(3)

        with(android.os.Handler(Looper.getMainLooper())) {
           post {
               mp.listners = object : SoundPlayer.OnPlayListners{
                   override fun onCompletionListener() {
                       mp.playSound(R.raw.test1)
                       signal.countDown()
                   }

                   override fun onPreparedListener() {
                       signal.countDown()
                   }

                   override fun onErrorListener(error: String?) {
                       Assert.assertFalse("that test must have no erros", true)
                   }
               }
               mp.playSound(R.raw.test1)
           }
        }
        signal.await(10, TimeUnit.SECONDS)
    }


    @Test
    fun playSound_TwiceDifferent() {
        val mp = MediaPlayerManager(context)

        val signal = CountDownLatch(3)

        with(android.os.Handler(Looper.getMainLooper())) {
            post {
                mp.listners = object : SoundPlayer.OnPlayListners{
                    override fun onCompletionListener() {
                        mp.playSound(R.raw.test2)
                        signal.countDown()
                    }

                    override fun onPreparedListener() {
                        signal.countDown()
                    }

                    override fun onErrorListener(error: String?) {
                        Assert.assertFalse("that test must have no erros", true)
                    }
                }
                mp.playSound(R.raw.test1)
            }
        }
        signal.await(10, TimeUnit.SECONDS)
    }

    @Test
    fun playSound_BadUrl() {
        val mp = MediaPlayerManager(context)

        val signal = CountDownLatch(1)

        with(android.os.Handler(Looper.getMainLooper())) {
            post {
                mp.listners = object : SoundPlayer.OnPlayListners{
                    override fun onCompletionListener() {
                        Assert.assertTrue("an completion here", true)
                    }

                    override fun onPreparedListener() {
                        Assert.assertFalse("no prepare here", true)
                    }

                    override fun onErrorListener(error: String?) {
                        Assert.assertTrue(error!!.isNotEmpty())
                        Assert.assertEquals("java.io.IOException: setDataSource failed.", error)
                        signal.countDown()
                    }
                }
                mp.playSound("bad url")
            }
        }
        signal.await(10, TimeUnit.SECONDS)
    }

    //only single play should be
    @Test
    fun playSound_SeveralOnce() {
        val mp = MediaPlayerManager(context)

        val signal = CountDownLatch(1)

        with(android.os.Handler(Looper.getMainLooper())) {
            post {
                mp.listners = object : SoundPlayer.OnPlayListners{
                    override fun onCompletionListener() {
                        signal.countDown()
                    }

                    override fun onPreparedListener() {
                    }

                    override fun onErrorListener(error: String?) {
                        Assert.assertFalse("that test must have no erros", true)
                    }
                }
                mp.playSound(R.raw.test1)
                mp.playSound(R.raw.test1)
                mp.playSound(R.raw.test1)
            }
        }
        signal.await(10, TimeUnit.SECONDS)
    }

}
