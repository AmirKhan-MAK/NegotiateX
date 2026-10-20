package com.negotiatex.controller;

import com.negotiatex.dto.request.ProductRequest;
import com.negotiatex.dto.response.ProductResponse;
import com.negotiatex.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping("/seller")
    public ResponseEntity<ProductResponse> createProduct(@AuthenticationPrincipal UserDetails userDetails, @RequestBody ProductRequest request) {
        ProductResponse response = productService.createProduct(userDetails.getUsername(), request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts(null));
    }
}
