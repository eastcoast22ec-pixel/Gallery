package org.fossify.gallery.views

import org.fossify.gallery.adapters.MediaAdapter
import org.fossify.gallery.models.Medium
import org.fossify.gallery.models.ThumbnailSection
import java.lang.reflect.Field
import java.lang.reflect.Method

internal object DateSectionSelectionController {
    fun getSectionPositions(adapter: MediaAdapter, sectionPosition: Int): List<Int> {
        val nextSectionPosition = ((sectionPosition + 1) until adapter.media.size)
            .firstOrNull { adapter.media[it] is ThumbnailSection }
            ?: adapter.media.size

        return ((sectionPosition + 1) until nextSectionPosition)
            .filter { adapter.media[it] is Medium }
    }

    fun getSelectedCount(adapter: MediaAdapter, positions: List<Int>): Int {
        val selectedKeys = getSelectedKeys(adapter) ?: return 0
        return positions.count { position ->
            adapter.getItemSelectionKey(position)?.let(selectedKeys::contains) == true
        }
    }

    fun areAllSelected(adapter: MediaAdapter, positions: List<Int>): Boolean {
        return positions.isNotEmpty() && getSelectedCount(adapter, positions) == positions.size
    }

    fun hasSelection(adapter: MediaAdapter): Boolean {
        return getSelectedKeys(adapter)?.isNotEmpty() == true
    }

    fun applySelection(adapter: MediaAdapter, positions: List<Int>, select: Boolean) {
        val selectedKeys = getSelectedKeys(adapter) ?: return
        val toggleMethod = findMethod(
            adapter.javaClass,
            TOGGLE_SELECTION_METHOD,
            Boolean::class.javaPrimitiveType!!,
            Int::class.javaPrimitiveType!!,
            Boolean::class.javaPrimitiveType!!,
        ) ?: return

        for (position in positions) {
            val key = adapter.getItemSelectionKey(position) ?: continue
            if (selectedKeys.contains(key) != select) {
                val succeeded = runCatching {
                    toggleMethod.invoke(adapter, select, position, false)
                }.isSuccess
                if (!succeeded) {
                    return
                }
            }
        }

        findMethod(adapter.javaClass, UPDATE_TITLE_METHOD)?.let { method ->
            runCatching { method.invoke(adapter) }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun getSelectedKeys(adapter: MediaAdapter): MutableSet<Int>? {
        val field = findField(adapter.javaClass, SELECTED_KEYS_FIELD) ?: return null
        return runCatching { field.get(adapter) as? MutableSet<Int> }.getOrNull()
    }

    private fun findField(startClass: Class<*>, name: String): Field? {
        var currentClass: Class<*>? = startClass
        while (currentClass != null) {
            val field = runCatching {
                currentClass.getDeclaredField(name).apply { isAccessible = true }
            }.getOrNull()
            if (field != null) {
                return field
            }
            currentClass = currentClass.superclass
        }
        return null
    }

    private fun findMethod(
        startClass: Class<*>,
        name: String,
        vararg parameterTypes: Class<*>,
    ): Method? {
        var currentClass: Class<*>? = startClass
        while (currentClass != null) {
            val method = runCatching {
                currentClass.getDeclaredMethod(name, *parameterTypes).apply {
                    isAccessible = true
                }
            }.getOrNull()
            if (method != null) {
                return method
            }
            currentClass = currentClass.superclass
        }
        return null
    }

    private const val SELECTED_KEYS_FIELD = "selectedKeys"
    private const val TOGGLE_SELECTION_METHOD = "toggleItemSelection"
    private const val UPDATE_TITLE_METHOD = "updateTitle"
}
