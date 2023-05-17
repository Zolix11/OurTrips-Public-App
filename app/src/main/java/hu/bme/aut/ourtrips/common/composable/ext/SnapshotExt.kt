package hu.bme.aut.ourtrips.common.composable.ext

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList

@Composable
fun <T> List<T>.toSnapshotStateList(): SnapshotStateList<T> {
    val snapshotStateList = remember { SnapshotStateList<T>() }
    snapshotStateList.addAll(this)
    return snapshotStateList
}