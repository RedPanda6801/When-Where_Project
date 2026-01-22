package com.example.whenandwhere

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class GridSpacingItemDecoration(
    private val context: Context,
    private val spanCount: Int,
    private val horizontalSpacing: Int,
    private val verticalSpacing: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view) // 아이템의 위치

        // spanCount에 따라 아이템의 위치를 계산하여 가로 및 세로 간격을 설정
        val column = position % spanCount
        outRect.left = column * horizontalSpacing / spanCount
        outRect.right = horizontalSpacing - (column + 1) * horizontalSpacing / spanCount

        if (position >= spanCount) {
            outRect.top = verticalSpacing // 세로 간격 설정
        }
    }
}
