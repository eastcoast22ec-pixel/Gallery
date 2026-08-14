package org.fossify.gallery.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import org.fossify.gallery.adapters.MediaAdapter
import org.fossify.gallery.models.Medium
import org.fossify.gallery.models.ThumbnailSection

/**
 * Selects every media item that belongs to the date section containing this button.
 *
 * The Gallery adapter already owns a stable range-selection implementation. This view
 * intentionally reuses that implementation instead of duplicating selection state.
 */
class SectionSelectButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : AppCompatImageView(context, attrs, defStyleAttr) {

    init {
        isClickable = true
        isFocusable = true
        setOnClickListener { selectCurrentSection() }
    }

    private fun selectCurrentSection() {
        val (recyclerView, sectionItemView) = findRecyclerViewAndItemView() ?: return
        val adapter = recyclerView.adapter as? MediaAdapter ?: return

        // A single-pick external intent must keep its original one-item behaviour.
        if (adapter.isAGetIntent && !adapter.allowMultiplePicks) {
            return
        }

        val sectionPosition = recyclerView.getChildAdapterPosition(sectionItemView)
        if (sectionPosition == RecyclerView.NO_POSITION || adapter.media.getOrNull(sectionPosition) !is ThumbnailSection) {
            return
        }

        val nextSectionPosition = ((sectionPosition + 1) until adapter.media.size)
            .firstOrNull { adapter.media[it] is ThumbnailSection }
            ?: adapter.media.size

        val firstMediaPosition = ((sectionPosition + 1) until nextSectionPosition)
            .firstOrNull { adapter.media[it] is Medium }
            ?: return

        val lastMediaPosition = ((nextSectionPosition - 1) downTo firstMediaPosition)
            .firstOrNull { adapter.media[it] is Medium }
            ?: return

        // Start from a clean contextual selection so a tap always means
        // "select this date" and cannot accidentally extend an older drag range.
        adapter.finishActMode()
        selectRange(recyclerView, adapter, firstMediaPosition, lastMediaPosition)
    }

    private fun selectRange(
        recyclerView: RecyclerView,
        adapter: MediaAdapter,
        firstPosition: Int,
        lastPosition: Int,
        attempt: Int = 0,
    ) {
        val firstHolder = recyclerView.findViewHolderForAdapterPosition(firstPosition)
        if (firstHolder != null) {
            val actionModeStarted = firstHolder.itemView.performLongClick()
            if (actionModeStarted && lastPosition > firstPosition) {
                adapter.itemLongClicked(lastPosition)
            }
            return
        }

        // The first row can be just outside the viewport when the section title is
        // tapped near the bottom edge. Bring it into layout, then retry briefly.
        if (attempt >= MAX_LAYOUT_RETRIES) {
            return
        }

        recyclerView.scrollToPosition(firstPosition)
        recyclerView.postDelayed(
            { selectRange(recyclerView, adapter, firstPosition, lastPosition, attempt + 1) },
            RETRY_DELAY_MS,
        )
    }

    private fun findRecyclerViewAndItemView(): Pair<RecyclerView, View>? {
        var itemView: View = this
        var currentParent = itemView.parent

        while (currentParent is View && currentParent !is RecyclerView) {
            itemView = currentParent
            currentParent = itemView.parent
        }

        val recyclerView = currentParent as? RecyclerView ?: return null
        return recyclerView to itemView
    }

    private companion object {
        const val MAX_LAYOUT_RETRIES = 3
        const val RETRY_DELAY_MS = 32L
    }
}
