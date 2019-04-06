package com.tuvv.tword.utils.components

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.text.TextUtilsCompat
import android.support.v4.view.ViewCompat
import android.support.v4.widget.TextViewCompat
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.tuvv.tword.R
import com.tuvv.tword.features.SoundState
import com.tuvv.tword.utils.extentions.setVisible
import com.tuvv.tword.utils.extentions.showMessage
import kotlinx.android.synthetic.main.play_media_button.view.*
import kotlinx.android.synthetic.main.text_and_sound_button.view.*
import java.util.*
import android.widget.RelativeLayout


class TextAndSoundPlayerButton(context: Context?, attrs: AttributeSet?) : FrameLayout(context, attrs) {
    companion object {


    }
    private var buttonView: SoundPlayerButton
    private var textView: TextView

    private var isButtonOnRightSide = false

    var isTextSelectable: Boolean = false
        set(value) {
            field = value
            setListner()
        }

    var text: CharSequence? = null
        set(value) {
            field = value
            textView.text = text //to auto resize depending on content
            setInnerTextIsSelectable(false)
            requestLayout()
        }
    var textAppearance: Int = 0
        set(value) {
            field = value
            if (value > 0) TextViewCompat.setTextAppearance(textView, textAppearance)
            requestLayout()
        }
    var state = SoundPlayerButton.NORMAL
        set(value) {
            field = value
            requestLayout()
        }
    var buttonVisible: Boolean = true
        set(value) {
            field = value
            requestLayout()
        }

    var onPlayBlock: (() -> Unit)? = null
        set(value) {
            field = value
            if (value != null) setListner()
        }
    var onStopBlock: (() -> Unit)? = null
        set(value) {
            field = value
            if (value != null) setListner()
        }

    init {
        View.inflate(getContext(), R.layout.text_and_sound_button, this)
        buttonView = findViewById(R.id.textSound_button)
        textView = findViewById(R.id.textSound_text)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            textView.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM)
        }
        buttonWrapper.background = null

        setupAttributes(attrs)

//        background = textSoundWrapper.background
//        textSoundWrapper.background = null

        addRipple();
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        buttonView.state = state
        textView.text = text
        buttonView.setVisible(buttonVisible)

        val isLeftToRight = TextUtilsCompat.getLayoutDirectionFromLocale(Locale.getDefault()) == ViewCompat.LAYOUT_DIRECTION_LTR
        textSoundWrapper.layoutDirection =
                if (isLeftToRight)
                    if (isButtonOnRightSide) View.LAYOUT_DIRECTION_LTR else View.LAYOUT_DIRECTION_RTL
                else
                    if (isButtonOnRightSide) View.LAYOUT_DIRECTION_RTL else View.LAYOUT_DIRECTION_LTR

        super.onLayout(changed, l, t, r, b)
    }

    private fun setupAttributes(attrs: AttributeSet?) {
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.TextAndSoundPlayerButton,
                0, 0)

        state = typedArray.getInt(R.styleable.TextAndSoundPlayerButton_state, SoundPlayerButton.NORMAL)
        text = typedArray.getString(R.styleable.TextAndSoundPlayerButton_text)

        textAppearance = typedArray.getResourceId(R.styleable.TextAndSoundPlayerButton_textAppearance, -1)

        textView.setLineSpacing(
                textView.lineSpacingExtra,
                typedArray.getFloat(R.styleable.TextAndSoundPlayerButton_lineSpacingMultiplier, 1f))

        typedArray.getInteger(R.styleable.TextAndSoundPlayerButton_maxLines, -1).let { if (it > 0) textView.maxLines = it }

//        with(textSoundWrapper) {setPadding(
//                typedArray.getDimension(R.styleable.TextAndSoundPlayerButton_leftInset, 0f).toInt(),
//                paddingTop,
//                typedArray.getDimension(R.styleable.TextAndSoundPlayerButton_rightInset, 0f).toInt(),
//                paddingBottom)}

        isButtonOnRightSide = typedArray.getBoolean(R.styleable.TextAndSoundPlayerButton_buttonOnRightSide, true)

        isTextSelectable = typedArray.getBoolean(R.styleable.TextAndSoundPlayerButton_textIsSelectable, false)

        buttonVisible = typedArray.getBoolean(R.styleable.TextAndSoundPlayerButton_buttonVisible, true)


        typedArray.recycle()
    }

    override fun onSaveInstanceState(): Parcelable {

        return Bundle().apply {
            putInt("state", state)
            putCharSequence("text", text)
            putParcelable("superState", super.onSaveInstanceState())
        }
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        var viewState = state
        if (viewState is Bundle) {
            this.state = viewState.getInt("state", SoundPlayerButton.NORMAL)
            this.text = viewState.getCharSequence("text")
            viewState = viewState.getParcelable("superState")
        }
        super.onRestoreInstanceState(viewState)
    }

    //aux funcs
    private fun setListner() {
        setOnClickListener { if (state == SoundState.N.ordinal) onPlayBlock?.invoke() else onStopBlock?.invoke()}
        if (isTextSelectable) {
            setOnLongClickListener { v ->
                if (v is FrameLayout) setInnerTextIsSelectable(!textView.isTextSelectable)
                context.showMessage(if (textView.isTextSelectable)
                    R.string.turn_on_text_selectable_mode else
                    R.string.turn_off_text_selectable_mode)
                true
            }
        }
    }

    private fun setInnerTextIsSelectable(value: Boolean) {
        textView.layoutParams = RelativeLayout.LayoutParams(
                if (value) LayoutParams.MATCH_PARENT else LayoutParams.WRAP_CONTENT ,
                LayoutParams.WRAP_CONTENT)
        textView.setTextIsSelectable(value)
        selectableIcon.setVisible(value)
    }
}

private fun View.addRipple() = with(TypedValue()) {
    context.theme.resolveAttribute(android.R.attr.selectableItemBackground, this, true)
    setBackgroundResource(resourceId)
}