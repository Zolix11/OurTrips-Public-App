package hu.bme.aut.ourtrips.screens.home

data class HomeUiState(
    val isRefreshing : Boolean=false,
    val openSheetMap : Boolean=false,
    val openUserProfile : Boolean=false,
    val addFriendsDialog : Boolean = false,
    var photoLatitude : Double=0.0,
    var photoLongitude : Double=0.0,
    var userId : String?
)

