package cbi.androidapp.myapplication.network

data class ListItemsResponse(
    val statusCode: Int,
    val message: String,
    val data: List<ItemData>
)

data class ItemData(
    val id: Int,
    val item_name: String,
    val stock: String,
    val unit: String
)
