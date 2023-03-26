# EAN_REST_API
REST API built using Spring framework that allows the retrieval of product information from barcodes (EAN) as well as making CRUD petitions to the entities involved 
(Products and Provider).

# Introduction 
EANs are codes with this format: PPPPPPP+NNNNN+D.
The digits indicated with P refer to the supplier that manufactures the product. The digits indicated with N refer to the product code and the digit indicated with D refers to a destination digit.

This API allows you to enter a concrete EAN and it will provide you with the Product´s info as well as the Provider´s info and Destination.

# API ENDPOINTS
- *This is the endpoint for the EANreader functionality

* [GET] ```/getEan/{EanCode}```: Returns basic data of the product, supplier and destination.

- *These are the endpoints for the CRUD API of Products and Provider

* [GET] ```/getProduct/{ProductCode}```: Returns the data associated with that product (The productCode is the above-mentioned "NNNNN" of the EAN code)
* [POST] ```/addProduct```: Create a product (the product data must be in the body of the petition is JSON format, see example of this on the PostMan Collection).
* [PUT] ```/updateProduct/{ProductCode}```: Update an existing product (the product data must be in the body of the petition is JSON format, see example of this on the PostMan Collection)
* [Delete] ```/deleteProduct/{EanCode}```: Deletes an existing Product.
