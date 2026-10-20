package com.negotiatex.service;

import com.negotiatex.dto.request.BulkTierRequest;
import com.negotiatex.dto.request.ProductRequest;
import com.negotiatex.dto.response.ProductResponse;
import com.negotiatex.entity.BulkTier;
import com.negotiatex.entity.Category;
import com.negotiatex.entity.Product;
import com.negotiatex.entity.User;
import com.negotiatex.repository.CategoryRepository;
import com.negotiatex.repository.ProductRepository;
import com.negotiatex.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private CategoryRepository categoryRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ScoringService scoringService;

	@Transactional
	public ProductResponse createProduct(String sellerEmail, ProductRequest request) {
		User seller = userRepository.findByEmail(sellerEmail)
				.orElseThrow(() -> new RuntimeException("Seller not found"));

		Category category = null;
		if (request.getCategoryId() != null && !request.getCategoryId().isEmpty()) {
			category = categoryRepository.findById(request.getCategoryId())
					.orElseThrow(() -> new RuntimeException("Category not found"));
		}

		Product product = new Product();
		product.setSeller(seller);
		product.setCategory(category);
		product.setName(request.getName());
		product.setDescription(request.getDescription());
		product.setBasePrice(request.getBasePrice());
		product.setFloorPrice(request.getFloorPrice());
		product.setStock(request.getStock());
		product.setNegotiable(request.getNegotiable() != null ? request.getNegotiable() : true);
		product.setNearExpiry(request.getIsNearExpiry() != null ? request.getIsNearExpiry() : false);
		product.setExpiryDate(request.getExpiryDate());
		product.setImageUrl(request.getImageUrl());
		product.setActive(true);
		product.setAiNegotiationEnabled(request.getAiNegotiationEnabled() != null ? request.getAiNegotiationEnabled() : false);
		product.setAiTargetPrice(request.getAiTargetPrice());

		if (request.getBulkTiers() != null) {
			for (BulkTierRequest dto : request.getBulkTiers()) {
				BulkTier tier = new BulkTier();
				tier.setProduct(product);
				tier.setMinQty(dto.getMinQty());
				tier.setMaxQty(dto.getMaxQty());
				tier.setPrice(dto.getPrice());
				product.getBulkTiers().add(tier);
			}
		}

		product = productRepository.save(product);
		return mapToResponse(product, seller.getId());
	}

	@Transactional(readOnly = true)
	public List<ProductResponse> getAllProducts(String currentUserId) {
		return productRepository.findAll().stream().map(p -> mapToResponse(p, currentUserId))
				.collect(Collectors.toList());
	}

	private ProductResponse mapToResponse(Product product, String currentUserId) {
		ProductResponse response = new ProductResponse();
		response.setId(product.getId());
		response.setSellerName(product.getSeller() != null ? product.getSeller().getName() : "Unknown");
		response.setCategoryName(product.getCategory() != null ? product.getCategory().getName() : null);
		response.setName(product.getName());
		response.setDescription(product.getDescription());
		response.setBasePrice(product.getBasePrice());
		response.setFloorPrice(product.getFloorPrice());
		response.setStock(product.getStock());
		response.setNegotiable(product.getNegotiable());
		response.setIsNearExpiry(product.getNearExpiry());
		response.setExpiryDate(product.getExpiryDate());
		response.setImageUrl(product.getImageUrl());
		response.setCreatedAt(product.getCreatedAt());

		// AI Pricing: Compute and inject personalized price if buyer is logged in
		if (currentUserId != null && product.getNegotiable() != null && product.getNegotiable()) {
			double score = scoringService.calculateBuyerScore(currentUserId);
			if (score > 0) {
				java.math.BigDecimal range = product.getBasePrice().subtract(product.getFloorPrice());
				java.math.BigDecimal discount = range.multiply(new java.math.BigDecimal(score));
				java.math.BigDecimal personalized = product.getBasePrice().subtract(discount).setScale(2,
						java.math.RoundingMode.HALF_UP);
				response.setPersonalizedDiscountPrice(personalized);

				// Gamification Logic: Give badge if getting 50%+ of max possible discount
				response.setIsBestDealUnlocked(score >= 0.50);
			}
		}

		return response;
	}
}
