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


// --- 4. XỬ LÝ CẬP NHẬT SỐ LƯỢNG GIỎ HÀNG MINI ---
function updateMiniCartQty(itemId, newQty) {
    if (newQty < 1) {
        // If quantity is < 1, you might want to call the remove API, or just ignore
        // For now, let's call the remove API to delete the item
        $.ajax({
            url: '/cart/api/remove',
            type: 'POST',
            data: { id: itemId },
            success: function() {
                $('#miniCartSidebar').load('/cart/fragment');
                // Reload the page if we're on the cart page
                if(window.location.pathname === '/cart') {
                    window.location.reload();
                }
            }
        });
        return;
    }
    
    $.ajax({
        url: '/cart/api/update',
        type: 'POST',
        data: { itemId: itemId, quantity: newQty },
        success: function() {
            $('#miniCartSidebar').load('/cart/fragment');
            // Update total badge if needed, though fragment load handles the UI
            // Reload the page if we're on the cart page
            if(window.location.pathname === '/cart') {
                window.location.reload();
            }
        },
        error: function(xhr) {
            alert("Lỗi cập nhật giỏ hàng!");
        }
    });
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
        menu.classList.toggle('show');
    }
}

// Sự kiện: Bấm bất kỳ đâu ngoài màn hình thì đóng menu
document.addEventListener("click", function(event) {
    var menu = document.getElementById("userDropdown");
    var btn = document.querySelector(".user-dropdown-container");
    
    if (menu && menu.classList.contains("show")) {
        if (btn && !btn.contains(event.target)) {
            menu.classList.remove("show");
        }
    }
});


function openMobileMenu() { document.getElementById('mobileMenuOverlay').style.display = 'block'; setTimeout(() => { document.getElementById('mobileMenuSidebar').style.left = '0'; }, 10); }
function closeMobileMenu() { document.getElementById('mobileMenuSidebar').style.left = '-100%'; setTimeout(() => { document.getElementById('mobileMenuOverlay').style.display = 'none'; }, 300); }

// --- 6. XỬ LÝ YÊU THÍCH SẢN PHẨM ---
function toggleWishlist(productId, btnElement) {
    // Prevent event bubbling if the button is inside a link
    if(event) {
        event.preventDefault();
        event.stopPropagation();
    }
    $.ajax({
        url: '/api/wishlist/toggle/' + productId,
        type: 'POST',
        success: function(response) {
            if (response.status === 'success') {
                var icon = $(btnElement).find('i');
                if (response.added) {
                    icon.removeClass('far').addClass('fas');
                    icon.css('color', '#dc3545');
                } else {
                    icon.removeClass('fas').addClass('far');
                    icon.css('color', '');
                }
            } else {
                alert(response.message);
            }
        },
        error: function(xhr) {
            if (xhr.status === 401) {
                if (typeof Swal !== 'undefined') {
                    Swal.fire({
                        title: 'Thông báo',
                        text: 'Vui lòng đăng nhập để sử dụng tính năng này!',
                        icon: 'info',
                        showCancelButton: true,
                        confirmButtonText: 'Đăng nhập',
                        cancelButtonText: 'Đóng',
                        confirmButtonColor: '#000'
                    }).then((result) => {
                        if (result.isConfirmed) {
                            window.location.href = "/login";
                        }
                    });
                } else {
                    alert("Vui lòng đăng nhập để sử dụng tính năng này!");
                    window.location.href = "/login";
                }
            } else {
                var msg = "Có lỗi xảy ra!";
                if (xhr.responseJSON && xhr.responseJSON.message) {
                    msg = xhr.responseJSON.message;
                }
                alert(msg);
            }
        }
    });
}
