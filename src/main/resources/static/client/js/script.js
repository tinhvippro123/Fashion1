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