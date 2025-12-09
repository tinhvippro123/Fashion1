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

function switchTab(gender) {
        // 1. Ẩn hết nội dung các tab
        document.getElementById('tab-women').style.display = 'none';
        document.getElementById('tab-men').style.display = 'none';
        
        // 2. Hiện tab được chọn
        document.getElementById('tab-' + gender).style.display = 'block';
        
        // 3. Xử lý gạch chân (active class) cho tiêu đề
        // Xóa active cũ
        var tabs = document.getElementsByClassName('ivy-tab-item');
        for (var i = 0; i < tabs.length; i++) {
            tabs[i].classList.remove('active');
        }
        
        // Thêm active cho cái vừa bấm (Mẹo: tìm theo text hoặc index, ở đây làm nhanh bằng event)
        event.target.classList.add('active'); 
    }
