package com.cmi.flipbuy.model

class Cart {
    var name: String? = null
    var image: String? = null
    var price: String? = null
    var size: String? = null
    lateinit var id: String

    constructor() {

    }

    constructor(name: String?, image: String?, price: String?, id: String, size: String?) {
        this.name = name
        this.image = image
        this.price = price
        this.id = id
        this.size = size
    }
}