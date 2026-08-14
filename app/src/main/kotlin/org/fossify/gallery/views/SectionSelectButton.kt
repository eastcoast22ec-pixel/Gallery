package org.fossify.gallery.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView
import org.fossify.gallery.R
import org.fossify.gallery.adapters.MediaAdapter
import org.fossify.gallery.models.Medium
import org.fossify.gallery.models.ThumbnailSection
import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * Selects or deselects every media item that belongs to one date section.
 *
 * The control also reflects manual selection changes, supports partial selection,
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

        val positions = getSectionMediumPositions(adapter, sectionContext.sectionPosition)
        if (positions.isEmpty()) {
            return
        }

        val selectedKeys = getSelectedKeys(adapter) ?: return
        val allSelected = positions.all { position ->
            val key = adapter.getItemSelectionKey(position)
            key != null && selectedKeys.contains(key)
        }

        if (allSelected) {
            applySectionSelection(adapter, positions, select = false)
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
        val selectedKeys = getSelectedKeys(adapter) ?: return
        if (selectedKeys.isNotEmpty()) {
            applySectionSelection(adapter, positions, select = true)
            return
        }

        val firstPosition = positions.first()
        val firstHolder = recyclerView.findViewHolderForAdapterPosition(firstPosition)
        if (firstHolder != null) {
            firstHolder.itemView.performLongClick()
            if (getSelectedKeys(adapter)?.isNotEmpty() == true) {
                applySectionSelection(adapter, positions, select = true)
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

    private fun applySectionSelection(
        adapter: MediaAdapter,
        positions: List<Int>,
        select: Boolean,
    ) {
        val selectedKeys = getSelectedKeys(adapter) ?: return
        val toggleMethod = getToggleSelectionMethod(adapter) ?: return

        positions.forEach { position ->
            val key = adapter.getItemSelectionKey(position) ?: return@forEach
            val isSelected = selectedKeys.contains(key)
            if (isSelected != select) {
                toggleMethod.invoke(adapter, select, position, false)
            }
        }

        getUpdateTitleMethod(adapter)?.invoke(adapter)
        scheduleRefresh()
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
        val positions = getSectionMediumPositions(adapter, sectionContext.sectionPosition)
        val selectedKeys = getSelectedKeys(adapter) ?: return
        val selectedCount = positions.count { position ->
            adapter.getItemSelectionKey(position)?.let(selectedKeys::contains) == true
        }
        val totalCount = positions.size

        isEnabled = totalCount > 0
        setImageResource(
            when {
                totalCount > 0 && selectedCount == totalCount -> R.drawable.ic_section_select_checked
                selectedCount > 0 -> R.drawable.ic_section_select_partial
                else -> R.drawable.ic_section_select_outline
            }
        )

        val section = adapter.media[sectionContext.sectionPosition] as ThumbnailSection
        val title = when {
            totalCount == 0 -> section.title
            selectedCount == 0 -> "${section.title} · $totalCount"
            else -> "${section.title} · $selectedCount/$totalCount"
        }

        sectionContext.sectionItemView.findViewById<android.widget.TextView>(R.id.thumbnail_section)?.text = title
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

    private fun getSectionMediumPositions(
        adapter: MediaAdapter,
        sectionPosition: Int,
    ): List<Int> {
        val nextSectionPosition = ((sectionPosition + 1) until adapter.media.size)
            .firstOrNull { adapter.media[it] is ThumbnailSection }
            ?: adapter.media.size

        return ((sectionPosition + 1) until nextSectionPosition)
            .filter { adapter.media[it] is Medium }
    }

    @Suppress("UNCHECKED_CAST")
    private fun getSelectedKeys(adapter: MediaAdapter): MutableSet<Int>? {
        val field = getSelectedKeysField(adapter) ?: return null
        return field.get(adapter) as? MutableSet<Int>
    }

    private fun getSelectedKeysField(adapter: MediaAdapter): Field? {
        return findField(adapter.javaClass, SELECTED_KEYS_FIELD)
    }

    private fun getToggleSelectionMethod(adapter: MediaAdapter): Method? {
        return findMethod(
            adapter.javaClass,
            TOGGLE_SELECTION_METHOD,
            Boolean::class.javaPrimitiveType,
            Int::class.javaPrimitiveType,
            Boolean::class.javaPrimitiveType,
        )
    }

    private fun getUpdateTitleMethod(adapter: MediaAdapter): Method? {
        return findMethod(adapter.javaClass, UPDATE_TITLE_METHOD)
    }

    private fun findField(startClass: Class<*>, name: String): Field? {
        var currentClass: Class<*>? = startClass
        while (currentClass != null) {
            runCatching {
                return currentClass.getDeclaredField(name).apply { isAccessible = true }
            }
            currentClass = currentClass.superclass
        }
        return null
    }

    private fun findMethod(
        startClass: Class<*>,
        name: String,
        vararg parameterTypes: Class<*>?,
    ): Method? {
        var currentClass: Class<*>? = startClass
        while (currentClass != null) {
            runCatching {
                return currentClass.getDeclaredMethod(name, *parameterTypes).apply {
                    isAccessible = true
                }
            }
            currentClass = currentClass.superclass
        }
        return null
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
        const val SELECTED_KEYS_FIELD = "selectedKeys"
        const val TOGGLE_SELECTION_METHOD = "toggleItemSelection"
        const val UPDATE_TITLE_METHOD = "updateTitle"
    }
}
