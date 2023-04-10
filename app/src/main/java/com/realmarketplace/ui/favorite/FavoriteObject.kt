package com.realmarketplace.ui.favorite

import com.realmarketplace.model.AdvertModel

object FavoriteObject {
    private var advertFavoriteIds=ArrayList<String>()
    fun addNewAdvertId(advertId:String, advert: AdvertModel, visible:Boolean){
        if(!advertFavoriteIds.contains(advertId)) {
            advertFavoriteIds.add(advertId)
            if(visible) FavoriteViewModel.addNewAdvert(advert)
        }
    }
    fun removeAdvertId(advertId: String){
        if(advertFavoriteIds.contains(advertId)) {
            advertFavoriteIds.remove(advertId)
            FavoriteViewModel.removeAdvert(advertId)
        }
    }
    fun existsAdvertId(advertId: String): Boolean {
        return advertFavoriteIds.indexOf(advertId)!=-1
    }
    fun updateAdvertId(newFavorite:ArrayList<String>){
        for(newF in newFavorite){
            if(!advertFavoriteIds.contains(newF)) advertFavoriteIds.add(newF)
        }
    }
    fun clearAdvertId(){
        advertFavoriteIds =ArrayList()
        FavoriteViewModel.clearFavoriteAdvert()
    }
}