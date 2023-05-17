package hu.bme.aut.ourtrips.common.composable

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import hu.bme.aut.ourtrips.R
import hu.bme.aut.ourtrips.common.composable.ext.removeRipple
import hu.bme.aut.ourtrips.model.FireStorePost
import java.util.Date

@Composable
fun AnimatedHeartButton(
    isLiked: Boolean = false,
    likePost: () -> Unit,
    dislikePost: () -> Unit
) {
    var state by remember { mutableStateOf(isLiked) }

    val heartIcon = if (state) {
        R.drawable.favorite_heart_red_48px
    } else {
        R.drawable.favorite_heart_empty_48px
    }

    val alpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        )
    )


    val tint = if (state) {
        Color.Red
    } else {
        Color.Gray
    }
    Icon(
        painter = painterResource(id = heartIcon),
        contentDescription = "Heart",
        tint = tint,
        modifier = Modifier
            .removeRipple {
                if (state) {
                    dislikePost()
                } else {
                    likePost()
                }
                state = !state
            }
    )
}

@Composable
fun PostElapsedTimeText(postdate: Date) {
    val elapsed = (System.currentTimeMillis() - postdate.time) / 1000
    val minutes = elapsed / 60
    val hours = minutes / 60
    val days = hours / 24
    val timeAgo = when {
        minutes < 60 -> "$minutes minutes ago"
        hours == 1L -> "1 hour ago"
        hours < 24 -> "$hours hours ago"
        days == 1L -> "1 day ago"
        else -> "$days days ago"
    }
    Text(
        text = timeAgo,
    )
}

@Composable
fun PostCardBottomDetails(
    isLiked: Boolean,
    likePost: () -> Unit,
    dislikePost: () -> Unit,
    post: FireStorePost
) {
    val likesState = remember { mutableStateOf(post.likes.size) }
    Column() {
        if (post.description.isBlank()) {
            Row() {
                Text(
                    text = post.description,
                    modifier = Modifier
                        .padding(start = 5.dp)
                        .fillMaxWidth()
                )
            }
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(bottom = 5.dp)
                .fillMaxWidth()
        )
        {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth(0.2f)
            ) {
                AnimatedHeartButton(isLiked = isLiked, likePost = {
                    likesState.value++
                    likePost()
                }, dislikePost = {
                    dislikePost()
                    likesState.value--
                })

                Text(
                    text = likesState.value.toString(),
                    modifier = Modifier
                        .padding(start = 8.dp)
                )
            }
            PostElapsedTimeText(
                postdate = post.postdate.toDate()
            )
        }
    }
}
