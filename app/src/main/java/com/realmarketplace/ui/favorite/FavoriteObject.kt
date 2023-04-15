package com.realmarketplace.ui.favorite

import com.realmarketplace.model.AdvertModel

/**
 * A group of *tool*.
 *
 * Object contains advert string id list of favorite advert ids.
 */
object FavoriteObject {
    private var advertFavoriteIds=ArrayList<String>()
    /**
     * A group of *tool_function*.
     *
     * Function used to add new favorite advert id into favorite advert ids list.
     *
     * @param advert advert which id is added into favorite advert ids list viz. AdvertModel
     */
    fun addNewAdvertId(advert: AdvertModel){
        if(!advertFavoriteIds.contains(advert._id)) {
            advertFavoriteIds.add(advert._id)
            if(advert.visible) FavoriteViewModel.addNewAdvert(advert)
        }
    }
    /**
     * A group of *tool_function*.
     *
     * Function used to remove advert from favorite advert ids list by specific advert id.
     *
     * @param advertId id of advert that would be deleted from favorite advert ids list
     */
    fun removeAdvertId(advertId: String){
        if(advertFavoriteIds.contains(advertId)) {
            advertFavoriteIds.remove(advertId)
            FavoriteViewModel.removeAdvert(advertId)
        }
    }
    /**
     * A group of *tool_function*.
     *
     * Function used to find if advert with given id already exists in favorite advert ids list.
     *
     * @param advertId id of advert id which would be search in favorite advert ids list
     * @return true if id exists in favorite advert ids list else false
     */
    fun existsAdvertId(advertId: String): Boolean {
        return advertFavoriteIds.indexOf(advertId)!=-1
    }
    /**
     * A group of *tool_function*.
     *
     * Function used to add new advert id into favorite list if the id did not exist in the favorite advert ids list already.
     *
     * @param newFavorite list of new favorite advert ids that would be added into favorite advert ids list
     */
    fun updateAdvertId(newFavorite:ArrayList<String>){
        for(newF in newFavorite){
            if(!advertFavoriteIds.contains(newF)) advertFavoriteIds.add(newF)
        }
    }
    /**
     * A group of *tool_function*.
     *
     * Function used to clear all favorite advert ids in list so list would be empty.
     */
    fun clearAdvertId(){
        advertFavoriteIds =ArrayList()
        FavoriteViewModel.clearFavoriteAdvert()
    }
}