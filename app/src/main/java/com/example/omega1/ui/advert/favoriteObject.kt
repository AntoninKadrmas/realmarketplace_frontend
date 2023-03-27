package com.example.omega1.ui.advert

object favoriteObject {
    private var advertFavoriteIds=ArrayList<String>()
    fun addNewAdvertId(advertId:String){
        if(!advertFavoriteIds.contains(advertId))advertFavoriteIds.add(advertId)
    }
    fun removeAdvertId(advertId: String){
        if(advertFavoriteIds.contains(advertId))advertFavoriteIds.remove(advertId)
    }
    fun existsAdvertId(advertId: String): Int {
        println(advertFavoriteIds.indexOf(advertId))
        return advertFavoriteIds.indexOf(advertId)
    }
}