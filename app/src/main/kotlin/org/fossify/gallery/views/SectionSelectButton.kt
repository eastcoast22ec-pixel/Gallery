package org.fossify.gallery.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import org.fossify.gallery.R
import org.fossify.gallery.adapters.MediaAdapter
import org.fossify.gallery.models.ThumbnailSection

/**
 * Selects or deselects every media item that belongs to one date section.
 *
 * The control reflects manual selection changes, supports partial selection,
 * and preserves selections made in other date sections.
 */
class SectionSelectButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private var observedAdapter: RecyclerView.Adapter<*>? = null
    private var refreshPosted = false

    private val adapterObserver = object : RecyclerView.AdapterDataObserver() {
        override fun onChanged() = scheduleRefresh()

        override fun onItemRangeChanged(positionStart: Int, itemCount: Int) =
            scheduleRefresh()

        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) =
            scheduleRefresh()

        override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) =
            scheduleRefresh()

        override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) =
            scheduleRefresh()
    }

    init {
        isClickable = true
        isFocusable = true
        setOnClickListener { toggleCurrentSection() }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        scheduleRefresh()
    }

    override fun onDetachedFromWindow() {
        detachAdapterObserver()
        super.onDetachedFromWindow()
    }

    private fun toggleCurrentSection() {
        val sectionContext = findSectionContext() ?: return
        val adapter = sectionContext.adapter
        attachAdapterObserver(adapter)

        if (adapter.isAGetIntent && !adapter.allowMultiplePicks) {
            return
        }

        val positions = DateSectionSelectionController.getSectionPositions(
            adapter,
            sectionContext.sectionPosition,
        )
        if (positions.isEmpty()) {
            return
        }

        if (DateSectionSelectionController.areAllSelected(adapter, positions)) {
            DateSectionSelectionController.applySelection(adapter, positions, select = false)
            scheduleRefresh()
        } else {
            ensureSelectionModeAndSelect(sectionContext.recyclerView, adapter, positions)
        }
    }

    private fun ensureSelectionModeAndSelect(
        recyclerView: RecyclerView,
        adapter: MediaAdapter,
        positions: List<Int>,
        attempt: Int = 0,
    ) {
        if (DateSectionSelectionController.hasSelection(adapter)) {
            DateSectionSelectionController.applySelection(adapter, positions, select = true)
            scheduleRefresh()
            return
        }

        val firstPosition = positions.first()
        val firstHolder = recyclerView.findViewHolderForAdapterPosition(firstPosition)
        if (firstHolder != null) {
            firstHolder.itemView.performLongClick()
            if (DateSectionSelectionController.hasSelection(adapter)) {
                DateSectionSelectionController.applySelection(adapter, positions, select = true)
                scheduleRefresh()
            }
            return
        }

        if (attempt >= MAX_LAYOUT_RETRIES) {
            return
        }

        recyclerView.scrollToPosition(firstPosition)
        recyclerView.postDelayed(
            {
                ensureSelectionModeAndSelect(
                    recyclerView,
                    adapter,
                    positions,
                    attempt + 1,
                )
            },
            RETRY_DELAY_MS,
        )
    }

    private fun refreshState() {
        val sectionContext = findSectionContext() ?: return
        val adapter = sectionContext.adapter
        attachAdapterObserver(adapter)

        if (adapter.isAGetIntent && !adapter.allowMultiplePicks) {
            visibility = View.GONE
            return
        }

        visibility = View.VISIBLE
        val positions = DateSectionSelectionController.getSectionPositions(
            adapter,
            sectionContext.sectionPosition,
        )
        val selectedCount = DateSectionSelectionController.getSelectedCount(adapter, positions)
        val totalCount = positions.size

        isEnabled = totalCount > 0
        val iconResource = when {
            totalCount > 0 && selectedCount == totalCount -> {
                R.drawable.ic_section_select_checked
            }

            selectedCount > 0 -> R.drawable.ic_section_select_partial
            else -> R.drawable.ic_section_select_outline
        }
        setImageResource(iconResource)

        val section = adapter.media[sectionContext.sectionPosition] as ThumbnailSection
        val title = when {
            totalCount == 0 -> section.title
            selectedCount == 0 -> "${section.title} · $totalCount"
            else -> "${section.title} · $selectedCount/$totalCount"
        }

        sectionContext.sectionItemView
            .findViewById<TextView>(R.id.thumbnail_section)
            ?.text = title
        contentDescription = title
    }

    private fun scheduleRefresh() {
        if (refreshPosted) {
            return
        }

        refreshPosted = true
        post {
            refreshPosted = false
            refreshState()
        }
    }

    private fun attachAdapterObserver(adapter: RecyclerView.Adapter<*>) {
        if (observedAdapter === adapter) {
            return
        }

        detachAdapterObserver()
        observedAdapter = adapter
        adapter.registerAdapterDataObserver(adapterObserver)
    }

    private fun detachAdapterObserver() {
        observedAdapter?.unregisterAdapterDataObserver(adapterObserver)
        observedAdapter = null
    }

    private fun findSectionContext(): SectionContext? {
        var sectionItemView: View = this
        var currentParent = sectionItemView.parent

        while (currentParent is View && currentParent !is RecyclerView) {
            sectionItemView = currentParent
            currentParent = sectionItemView.parent
        }

        val recyclerView = currentParent as? RecyclerView ?: return null
        val adapter = recyclerView.adapter as? MediaAdapter ?: return null
        val sectionPosition = recyclerView.getChildAdapterPosition(sectionItemView)
        val isSection = adapter.media.getOrNull(sectionPosition) is ThumbnailSection
        if (sectionPosition == RecyclerView.NO_POSITION || !isSection) {
            return null
        }

        return SectionContext(
            recyclerView = recyclerView,
            sectionItemView = sectionItemView,
            adapter = adapter,
            sectionPosition = sectionPosition,
        )
    }

    private data class SectionContext(
        val recyclerView: RecyclerView,
        val sectionItemView: View,
        val adapter: MediaAdapter,
        val sectionPosition: Int,
    )

    private companion object {
        const val MAX_LAYOUT_RETRIES = 3
        const val RETRY_DELAY_MS = 32L
    }
}
