package com.undef.superahorro.signorini.haron.data

val mockPurchases = listOf(
    mockPurchase(
        id = 1,
        marketName = "Coto Sucursal Centro",
        date = "03/05/2026",
        products = listOf(
            Product(id = 1, name = "Leche entera 1L", quantity = 2, price = 1450.0),
            Product(id = 2, name = "Pan lactal integral", quantity = 1, price = 2650.0),
            Product(id = 3, name = "Arroz largo fino 1kg", quantity = 2, price = 1850.0),
            Product(id = 4, name = "Yerba mate 1kg", quantity = 1, price = 4300.0),
            Product(id = 5, name = "Queso cremoso 500g", quantity = 1, price = 5200.0),
            Product(id = 6, name = "Manzanas rojas 1kg", quantity = 1, price = 2100.0)
        )
    ),
    mockPurchase(
        id = 2,
        marketName = "Carrefour Express",
        date = "01/05/2026",
        products = listOf(
            Product(id = 1, name = "Fideos tirabuzon", quantity = 2, price = 1250.0),
            Product(id = 2, name = "Pure de tomate", quantity = 2, price = 890.0),
            Product(id = 3, name = "Aceite girasol 900ml", quantity = 1, price = 3600.0),
            Product(id = 4, name = "Galletitas de agua", quantity = 2, price = 1100.0),
            Product(id = 5, name = "Detergente 750ml", quantity = 1, price = 1750.0)
        )
    ),
    mockPurchase(
        id = 7,
        marketName = "Disco Barrio Jardin",
        date = "06/05/2026",
        products = listOf(
            Product(id = 1, name = "Atun al natural", quantity = 3, price = 2100.0),
            Product(id = 2, name = "Mayonesa 500g", quantity = 1, price = 1750.0),
            Product(id = 3, name = "Papel higienico pack x4", quantity = 1, price = 3400.0),
            Product(id = 4, name = "Jugo de naranja 1L", quantity = 2, price = 1850.0)
        )
    ),
    mockPurchase(
        id = 8,
        marketName = "Vea Nueva Cordoba",
        date = "08/05/2026",
        products = listOf(
            Product(id = 1, name = "Hamburguesas x4", quantity = 1, price = 5900.0),
            Product(id = 2, name = "Pan de hamburguesa", quantity = 2, price = 2100.0),
            Product(id = 3, name = "Queso cheddar fetas", quantity = 1, price = 3200.0),
            Product(id = 4, name = "Gaseosa cola 2.25L", quantity = 1, price = 2800.0)
        )
    ),
    mockPurchase(
        id = 3,
        marketName = "Ahorro Market",
        date = "29/04/2026",
        products = listOf(
            Product(id = 1, name = "Pollo entero", quantity = 1, price = 6900.0),
            Product(id = 2, name = "Papas 2kg", quantity = 1, price = 2400.0),
            Product(id = 3, name = "Zanahoria 1kg", quantity = 1, price = 1300.0),
            Product(id = 4, name = "Cebolla 1kg", quantity = 1, price = 1500.0),
            Product(id = 5, name = "Soda 2.25L", quantity = 2, price = 950.0)
        )
    ),
    mockPurchase(
        id = 4,
        marketName = "Despensa Norte",
        date = "21/04/2026",
        products = listOf(
            Product(id = 1, name = "Huevos docena", quantity = 1, price = 3100.0),
            Product(id = 2, name = "Harina 0000 1kg", quantity = 2, price = 850.0),
            Product(id = 3, name = "Azucar 1kg", quantity = 1, price = 1600.0),
            Product(id = 4, name = "Cafe molido 250g", quantity = 1, price = 4800.0),
            Product(id = 5, name = "Mermelada frutilla", quantity = 1, price = 1450.0)
        )
    ),
    mockPurchase(
        id = 5,
        marketName = "DIA Market",
        date = "11/03/2026",
        products = listOf(
            Product(id = 1, name = "Yogur firme pack x4", quantity = 1, price = 2600.0),
            Product(id = 2, name = "Cereal 500g", quantity = 1, price = 3900.0),
            Product(id = 3, name = "Bananas 1kg", quantity = 1, price = 1800.0),
            Product(id = 4, name = "Jabon liquido ropa", quantity = 1, price = 5200.0)
        )
    ),
    mockPurchase(
        id = 6,
        marketName = "Verduleria San Martin",
        date = "28/02/2026",
        products = listOf(
            Product(id = 1, name = "Tomate redondo 1kg", quantity = 1, price = 2300.0),
            Product(id = 2, name = "Lechuga criolla", quantity = 2, price = 900.0),
            Product(id = 3, name = "Naranja 2kg", quantity = 1, price = 2600.0),
            Product(id = 4, name = "Zapallo anco", quantity = 1, price = 1700.0)
        )
    ),
    mockPurchase(
        id = 9,
        marketName = "Hiper Libertad",
        date = "17/01/2026",
        products = listOf(
            Product(id = 1, name = "Helado 1kg", quantity = 1, price = 7800.0),
            Product(id = 2, name = "Papas fritas", quantity = 2, price = 1650.0),
            Product(id = 3, name = "Cerveza lata", quantity = 6, price = 1250.0)
        )
    ),
    mockPurchase(
        id = 10,
        marketName = "Mayorista Del Centro",
        date = "14/12/2025",
        products = listOf(
            Product(id = 1, name = "Arvejas lata", quantity = 6, price = 950.0),
            Product(id = 2, name = "Lentejas secas 400g", quantity = 4, price = 1350.0),
            Product(id = 3, name = "Lavandina 2L", quantity = 2, price = 1900.0),
            Product(id = 4, name = "Servilletas pack", quantity = 3, price = 980.0)
        )
    )
)

private fun mockPurchase(
    id: Int,
    marketName: String,
    date: String,
    products: List<Product>
): Purchase {
    return Purchase(
        id = id,
        marketName = marketName,
        date = date,
        total = products.sumOf { it.quantity * it.price },
        productsCount = products.sumOf { it.quantity },
        products = products
    )
}
