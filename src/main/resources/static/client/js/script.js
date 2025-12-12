// --- 1. XỬ LÝ MINI CART (SIDEBAR) ---
function openMiniCart() {
	document.getElementById("miniCartSidebar").classList.add("open");
	document.getElementById("cartOverlay").classList.add("open");
	document.body.style.overflow = "hidden"; // Chặn cuộn trang chính
}

function closeMiniCart() {
	document.getElementById("miniCartSidebar").classList.remove("open");
	document.getElementById("cartOverlay").classList.remove("open");
	document.body.style.overflow = "auto"; // Cho phép cuộn lại
}

// --- 2. XỬ LÝ CHUYỂN TAB (NAM/NỮ) ---
function switchTab(gender, event) {
	// A. Ẩn tất cả nội dung tab
	$('.product-tab-content').hide();

	// B. Xóa class active ở tất cả các nút
	$('.ivy-tab-item').removeClass('active');

	// C. Hiện tab được chọn
	var selectedTab = $('#tab-' + gender);
	selectedTab.show();

	// D. Thêm class active cho nút vừa bấm
	if (event) {
		$(event.target).addClass('active');
	}

	// E. QUAN TRỌNG: Làm mới lại Slider (Owl Carousel)
	// Vì khi ẩn đi (display: none), slider bị mất kích thước. 
	// Khi hiện lại cần lệnh này để nó tính toán lại chiều rộng.
	selectedTab.find('.product-slider').trigger('refresh.owl.carousel');
}

// --- 3. XỬ LÝ QUICK BUY (CHỌN SIZE NHANH) ---

// Hàm bật/tắt danh sách Size
function toggleSizeList(productId, event) {
	// Ngăn chặn click lan ra ngoài (để không nhảy trang)
	event.stopPropagation();
	event.preventDefault();

	// Đóng tất cả các popup khác đang mở
	$('.quick-size-popup').hide();

	// Hiện popup của sản phẩm này
	var popup = $('#size-list-' + productId);
	popup.toggle();
}

// Đóng popup khi click bất kỳ đâu ra ngoài
$(document).click(function() {
	$('.quick-size-popup').hide();
});

// Hàm thêm vào giỏ hàng nhanh (AJAX)
function quickAddToCart(variantId) {
	$.ajax({
		url: '/cart/api/add',
		type: 'POST',
		data: { variantId: variantId, quantity: 1 },
		success: function(response) {
			$('.quick-size-popup').hide();

			// 1. Cập nhật số trên Icon Header (Cái chấm đỏ)
			if (response.totalItems !== undefined) {
				var badge = $('.cart-badge');
				if (badge.length > 0) badge.text(response.totalItems);
				else $('.header-cart-icon').append('<span class="cart-badge">' + response.totalItems + '</span>');
			}

			// 2. QUAN TRỌNG: Tải lại nội dung Sidebar (Mini Cart)
			// Lệnh load() sẽ gọi Controller -> Lấy HTML mới -> Đắp vào sidebar
			$('#miniCartSidebar').load('/cart/fragment');

			// 3. Hiện thông báo thành công
			$('#successModal').css('display', 'flex');
			setTimeout(function() { $('#successModal').fadeOut(); }, 2000);
		},
		error: function(xhr) {
			alert("Lỗi: " + (xhr.responseJSON ? xhr.responseJSON.message : "Không xác định"));
		}
	});
}

// Đóng Modal thông báo thành công
function closeSuccessModal() {
	$('#successModal').fadeOut();
}


// --- 5. XỬ LÝ THÊM GIỎ HÀNG TỪ TRANG CHI TIẾT ---
function addToCartFromDetail() {
	// 1. Lấy variantId (Size) mà khách đã chọn
	// Tìm input radio có name="variantId" và đang được checked
	var selectedRadio = document.querySelector('input[name="variantId"]:checked');

	// Validate: Nếu chưa chọn size
	if (!selectedRadio) {
		$('#size-error').show(); // Hiện thông báo lỗi có sẵn của bạn
		return;
	}

	var variantId = selectedRadio.value;

	// 2. Lấy số lượng
	var quantity = $('#qtyInput').val();

	// Validate số lượng
	if (!quantity || quantity < 1) quantity = 1;

	// 3. Gửi AJAX (Copy logic từ quickAddToCart nhưng thay đổi data quantity)
	$.ajax({
		url: '/cart/api/add',
		type: 'POST',
		data: {
			variantId: variantId,
			quantity: quantity // Sử dụng số lượng khách chọn
		},
		success: function(response) {
			// A. Cập nhật Icon Header
			if (response.totalItems !== undefined) {
				var badge = $('.cart-badge');
				if (badge.length > 0) badge.text(response.totalItems);
				else $('.header-cart-icon').append('<span class="cart-badge">' + response.totalItems + '</span>');
			}

			// B. Reload Sidebar Mini Cart
			$('#miniCartSidebar').load('/cart/fragment');

			// C. Hiện Modal Thông báo Thành công
			$('#successModal').css('display', 'flex');
			setTimeout(function() { $('#successModal').fadeOut(); }, 2000);
		},
		error: function(xhr) {
			var msg = "Có lỗi xảy ra!";
			if (xhr.responseJSON && xhr.responseJSON.message) {
				msg = xhr.responseJSON.message;
			}
			alert(msg);
		}
	});
}



// --- 5.2. XỬ LÝ NÚT "MUA HÀNG" (Chuyển ngay sang trang Giỏ hàng) ---
function buyNow() {
    // 1. Validate Size
    var selectedRadio = document.querySelector('input[name="variantId"]:checked');
    if (!selectedRadio) {
        $('#size-error').show();
        return;
    }
    var variantId = selectedRadio.value;

    // 2. Lấy số lượng
    var quantity = $('#qtyInput').val();
    if (!quantity || quantity < 1) quantity = 1;

    // 3. Gửi Ajax
    $.ajax({
        url: '/cart/api/add',
        type: 'POST',
        data: { variantId: variantId, quantity: quantity },
        success: function(response) {
            // THÀNH CÔNG -> CHUYỂN HƯỚNG NGAY LẬP TỨC
            window.location.href = "/cart";
        },
        error: function(xhr) {
            alert("Lỗi: " + (xhr.responseJSON ? xhr.responseJSON.message : "Không xác định"));
        }
    });
}





/* ======================================================= */
/* ===           XỬ LÝ DROPDOWN USER (HEADER)          === */
/* ======================================================= */
function toggleUserMenu(event) {
    // 1. Ngăn chặn hành vi mặc định (tránh load lại trang hoặc nhảy link)
    event.preventDefault();
    event.stopPropagation();

    // 2. Tìm menu
    var menu = document.getElementById("userDropdown");
    
    // 3. Bật/Tắt class 'show'
    if (menu) {
        // Kiểm tra xem đang hiện hay ẩn để toggle
        if (menu.style.display === "block") {
            menu.style.display = "none";
        } else {
            menu.style.display = "block";
        }
    }
}

// Sự kiện: Bấm bất kỳ đâu ngoài màn hình thì đóng menu
document.addEventListener("click", function(event) {
    var menu = document.getElementById("userDropdown");
    var container = document.querySelector(".user-dropdown-container");

    // Nếu bấm ra ngoài vùng container thì ẩn menu đi
    if (menu && container && !container.contains(event.target)) {
        menu.style.display = "none";
    }
});

