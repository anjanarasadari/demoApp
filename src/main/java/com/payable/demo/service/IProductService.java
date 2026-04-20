package com.payable.demo.service;

import com.payable.demo.dto.ProductRequest;
import com.payable.demo.dto.ProductResponse;

import java.util.List;

public interface IProductService {
    ProductResponse createProduct(ProductRequest request);
    ProductResponse updateProduct(Long id, ProductRequest request);
    void deleteProduct(Long id);
    ProductResponse getProductById(Long id);
    List<ProductResponse> getAllProducts();
    ProductResponse getProductBySku(String sku);
}
