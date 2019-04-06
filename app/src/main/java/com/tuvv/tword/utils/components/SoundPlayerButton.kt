package com.tuvv.tword.utils.components

import android.content.Context
import android.graphics.drawable.Animatable
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.tuvv.tword.R
import kotlinx.android.synthetic.main.play_media_button.view.*

class SoundPlayerButton(context: Context?, attrs: AttributeSet?) : LinearLayout(context, attrs) {
    companion object {
        private const val DEFAULT_LEFT_ICON_INSET = 12.0f

        const val NORMAL = 0
        const val DOWNLOADING = 1
        const val PLAYING = 2
    }
    private var icon: ImageView

    private var leftIconInset: Float
    var state = NORMAL
        set(value) {
            field = value
            requestLayout()
        }

    init {
        View.inflate(getContext(), R.layout.play_media_button, this)
        icon = findViewById(R.id.imageViewSoundButton)
        leftIconInset = DEFAULT_LEFT_ICON_INSET * resources.displayMetrics.density
        setupAttributes(attrs)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        when (state) {
            NORMAL -> setIconContent(R.drawable.ic_volume_up_24dp)
            DOWNLOADING -> {
                setIconContent(R.drawable.animate_progress_circular_24)
                (icon.drawable as? Animatable)?.start()
            }
            PLAYING -> setIconContent(R.drawable.ic_stop_24dp)
        }

        with(buttonWrapper) {setPadding(leftIconInset.toInt(), paddingTop, paddingRight, paddingBottom)}
        super.onLayout(changed, l, t, r, b)

    }

    private fun setIconContent(resId: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            icon.setImageDrawable(context.getDrawable(resId))
    }

    private fun setupAttributes(attrs: AttributeSet?) {
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.SoundPlayerButton,
                0, 0)

        state = typedArray.getInt(R.styleable.SoundPlayerButton_state, NORMAL)
        leftIconInset = typedArray.getDimension(R.styleable.SoundPlayerButton_leftIconInset,
                leftIconInset)
        typedArray.recycle()
    }

    override fun onSaveInstanceState(): Parcelable {

        return Bundle().apply {
            putInt("state", state)
            putParcelable("superState", super.onSaveInstanceState())
        }
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        var viewState = state
        if (viewState is Bundle) {
            this.state = viewState.getInt("state", NORMAL)
            viewState = viewState.getParcelable("superState")
        }
        super.onRestoreInstanceState(viewState)
    }
}