package hu.bme.aut.ourtrips.screens.friends


data class FriendsUiState(
    val isAcceptedFriendsVisible : Boolean = true,
    val isPendingFriendsVisible : Boolean = false,
    val dialogForSearchResult:  Boolean = false,
    val searchText : String =""
)
