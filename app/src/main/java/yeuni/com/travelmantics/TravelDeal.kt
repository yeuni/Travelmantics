package yeuni.com.travelmantics

import java.io.Serializable

class TravelDeal : Serializable {
    var id: String? = null
    var title: String? = null
    var description: String? = null
    var price: String? = null
    var imageUrl: String? = null
    var imageName: String? = null

    constructor() {}
    constructor(title: String?, description: String?, price: String?, imageUrl: String?, imageName: String?) {
        id = id
        this.title = title
        this.description = description
        this.price = price
        this.imageUrl = imageUrl
        this.imageName = imageName
    }

}