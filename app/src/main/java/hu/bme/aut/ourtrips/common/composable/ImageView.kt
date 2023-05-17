package hu.bme.aut.ourtrips.common.composable

import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import hu.bme.aut.ourtrips.R

@Composable
fun ImageViewWithChangeIcon(
    model: String,
    contentDescription: String,
    editImage: (Boolean) -> Unit,
) {
    Box(modifier = Modifier.fillMaxWidth(0.8f)) {
        AsyncImage(
            model = Uri.parse(model),
            contentDescription = contentDescription,
            modifier = Modifier
                .clip(RoundedCornerShape(percent = 10))
                .wrapContentSize()
                .border(
                    4.dp,
                    MaterialTheme.colorScheme.onPrimaryContainer,
                    RoundedCornerShape(percent = 10)
                )
                .fillMaxHeight(0.5f)
                .align(Alignment.Center)

        )

        Icon(painter = painterResource(id = R.drawable.edit_48px),
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = stringResource(id = R.string.profilePicture_edit),
            modifier = Modifier
                .size(42.dp)
                .padding(8.dp)
                .clip(RoundedCornerShape(percent = 50))
                .align(Alignment.BottomEnd)
                .clickable {
                    editImage(true)
                })

    }

}

@Composable
fun ImageViewWithChangeAndRemoveIcon(
    model: String,
    contentDescription: String,
    editImage: (Boolean) -> Unit,
    removeImage: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth(0.8f)) {
        AsyncImage(
            model = Uri.parse(model),
            contentDescription = contentDescription,
            modifier = Modifier
                .clip(RoundedCornerShape(percent = 10))
                .wrapContentSize()
                .border(
                    4.dp,
                    MaterialTheme.colorScheme.onPrimaryContainer,
                    RoundedCornerShape(percent = 10)
                )
                .fillMaxHeight(0.5f)
                .align(Alignment.Center)

        )

        Icon(painter = painterResource(id = R.drawable.edit_48px),
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = stringResource(id = R.string.profilePicture_edit),
            modifier = Modifier
                .size(42.dp)
                .padding(8.dp)
                .clip(RoundedCornerShape(percent = 50))
                .align(Alignment.BottomEnd)
                .clickable {
                    editImage(true)
                })

        Icon(painter = painterResource(id = R.drawable.remove_48px),
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = stringResource(id = R.string.profilePicture_edit),
            modifier = Modifier
                .size(42.dp)
                .padding(8.dp)
                .clip(RoundedCornerShape(percent = 50))
                .align(Alignment.TopEnd)
                .clickable {
                    removeImage()
                })

    }

}

@Composable
fun FeedImageViewWithLocationIcon(
    model: String,
    contentDescription: String,
    openLocation: (Boolean) -> Unit,
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(model)
                .crossfade(true)
                .build(),
            loading = {CircularProgressIndicator()},
            contentDescription = contentDescription,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)

        )

        Icon(painter = painterResource(id = R.drawable.location_on_48px),
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = stringResource(id = R.string.profilePicture_edit),
            modifier = Modifier
                .size(42.dp)
                .padding(8.dp)
                .clip(RoundedCornerShape(percent = 50))
                .align(Alignment.BottomEnd)
                .clickable {
                    openLocation(true)
                })

    }
}

@Composable
fun FeedImageView(
    model: String,
    contentDescription: String,
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(model)
                .crossfade(true)
                .build(),
            loading = {CircularProgressIndicator()},
            contentDescription = contentDescription,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)

        )

    }
}
@Composable
fun FeedProfilePicture(profileUri: String) {
    Box(modifier = Modifier.padding(5.dp, 5.dp, 10.dp, 5.dp), contentAlignment = Alignment.Center) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(profileUri).crossfade(true)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clip(shape = CircleShape)
                .border(
                    2.dp,
                    MaterialTheme.colorScheme.onPrimaryContainer,
                    shape = CircleShape
                )
        )
    }
}

@Composable
fun FriendsProfilePicture(profileUri: String) {
    Box(modifier = Modifier.padding(5.dp, 5.dp, 10.dp, 5.dp), contentAlignment = Alignment.Center) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(profileUri).crossfade(true)
                .build(),
            contentDescription = null,
            modifier = Modifier
                .size(60.dp)
                .clip(shape = CircleShape)
                .border(
                    2.dp,
                    MaterialTheme.colorScheme.onPrimaryContainer,
                    shape = CircleShape
                )
        )
    }
}