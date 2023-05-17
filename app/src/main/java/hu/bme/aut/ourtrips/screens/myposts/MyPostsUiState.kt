package hu.bme.aut.ourtrips.screens.myposts

data class MyPostsUiState(
    val openSheetMap : Boolean=false,
    var photoLatitude : Double=0.0,
    var photoLongitude : Double=0.0,
    var userId : String?
)
