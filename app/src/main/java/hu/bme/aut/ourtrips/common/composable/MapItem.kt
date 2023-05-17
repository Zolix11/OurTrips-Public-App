package hu.bme.aut.ourtrips.common.composable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.maps.android.clustering.Cluster
import hu.bme.aut.ourtrips.screens.map.CustomClusterItem

@Composable
fun CustomClusterItemPointer() {
    Surface(
        Modifier.size(40.dp),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.secondaryContainer,
        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        border = BorderStroke(3.dp, MaterialTheme.colorScheme.onSecondaryContainer)
    ) {}
}

@Composable
fun CustomClusterItemsPointer(cluster : Cluster<CustomClusterItem>) {
    Surface(
        Modifier.size(40.dp),
        shape = CircleShape,
        color = MaterialTheme.colorScheme.primaryContainer,
        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        border = BorderStroke(3.dp, MaterialTheme.colorScheme.onPrimaryContainer)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                "%,d".format(cluster.size),
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}


