package org.fossify.gallery.views

import org.fossify.gallery.adapters.MediaAdapter
import org.fossify.gallery.models.Medium
import org.fossify.gallery.models.ThumbnailSection

/**
 * Date-section selection policy.
 *
 * Selection ownership stays in [MediaAdapter], which already owns the Commons
 * ActionMode lifecycle and selected-key state. This controller only resolves
 * section positions and delegates state transitions to that explicit API.
 */
internal object DateSectionSelectionController {
    fun getSectionPositions(adapter: MediaAdapter, sectionPosition: Int): List<Int> {
        val nextSectionPosition = ((sectionPosition + 1) until adapter.media.size)
            .firstOrNull { adapter.media[it] is ThumbnailSection }
            ?: adapter.media.size

        return ((sectionPosition + 1) until nextSectionPosition)
            .filter { adapter.media[it] is Medium }
    }

    fun getSelectedCount(adapter: MediaAdapter, positions: List<Int>): Int {
        return adapter.getSelectedCount(positions)
    }

    fun areAllSelected(adapter: MediaAdapter, positions: List<Int>): Boolean {
        return positions.isNotEmpty() && getSelectedCount(adapter, positions) == positions.size
    }

    fun applySelection(adapter: MediaAdapter, positions: List<Int>, select: Boolean): Boolean {
        return adapter.setPositionsSelected(positions, select)
    }
}
