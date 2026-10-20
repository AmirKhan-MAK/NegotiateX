package com.negotiatex.controller;

import com.negotiatex.service.ProductService;
import com.negotiatex.service.OfferService;
import com.negotiatex.service.OrderService;
import com.negotiatex.service.AnalyticsService;
import com.negotiatex.dto.response.AnalyticsResponse;
import com.negotiatex.dto.response.OfferResponse;
import com.negotiatex.entity.User;
import com.negotiatex.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.security.Principal;
import java.util.List;

@Controller
public class PageController {

	@Autowired
	private ProductService productService;

	@Autowired
	private OfferService offerService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private AnalyticsService analyticsService;

	@Autowired
	private UserRepository userRepository;

	public PageController() {
		System.out.println("==== PageController Initialized ====");
	}

	@GetMapping("/")
	public String home() {
		return "index";
	}

	@GetMapping("/login")
	public String login() {
		return "login";
	}

	@GetMapping("/register")
	public String register() {
		return "register";
	}

	@GetMapping("/seller/dashboard")
	public String sellerDashboard(Principal principal, Model model) {
		if (principal == null)
			return "redirect:/login";
		User user = userRepository.findByEmail(principal.getName()).orElse(null);
		if (user == null || user.getRole() != com.negotiatex.entity.Role.SELLER) {
			return "redirect:/catalog"; // Boot buyers out of seller dashboard route
		}

		AnalyticsResponse metrics = analyticsService.getSellerAnalytics(user.getId());
		model.addAttribute("metrics", metrics);

		return "seller-dashboard";
	}

	@GetMapping("/catalog")
	public String catalog(Model model, Principal principal) {
		String userId = null;
		if (principal != null) {
			User user = userRepository.findByEmail(principal.getName()).orElse(null);
			if (user != null)
				userId = user.getId();
		}
		model.addAttribute("products", productService.getAllProducts(userId));
		return "catalog";
	}

	@GetMapping("/offers")
	public String offersView(Model model, Principal principal) {
		if (principal == null)
			return "redirect:/login";
		List<OfferResponse> offers = offerService.getMyOffers(principal.getName());
		model.addAttribute("offers", offers);
		return "offers";
	}

	@GetMapping("/offers/{id}")
	public String offerDetailView(@org.springframework.web.bind.annotation.PathVariable String id, Model model,
			Principal principal) {
		if (principal == null)
			return "redirect:/login";
		OfferResponse offer = offerService.getOfferDetails(id, principal.getName());
		model.addAttribute("offer", offer);
		return "offer-detail";
	}

	@GetMapping("/orders")
	public String ordersView(Model model, Principal principal) {
		if (principal == null)
			return "redirect:/login";
		List<com.negotiatex.dto.response.OrderResponse> orders = orderService.getMyOrders(principal.getName());
		model.addAttribute("orders", orders);
		return "orders";
	}
}
