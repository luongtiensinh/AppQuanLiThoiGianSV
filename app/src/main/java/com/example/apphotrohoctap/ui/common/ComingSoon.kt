package com.example.apphotrohoctap.ui.common

import android.content.Context
import android.widget.Toast
import com.example.apphotrohoctap.R

fun showComingSoon(context: Context) {
    Toast.makeText(context, context.getString(R.string.coming_soon), Toast.LENGTH_SHORT).show()
}
