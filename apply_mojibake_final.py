# coding: utf-8
import os
java_files = []
for root, _, files in os.walk('src/main/java'):
    for file in files:
        if file.endswith('.java'):
            java_files.append(os.path.join(root, file))

reps = {
    "TÃ\xa0i": "Tài",
    "khoáº£n": "khoản",
    "vÃ´": "vô",
    "hiá»‡u": "hiệu",
    "Lá»—i": "Lỗi",
    "á»\x9f": "ở",
    "Ä‘á»•i": "đổi",
    "nháº\xadt": "nhận",
    "Tá»\xab": "Từ",
    "gá»\xadi": "gửi",
    "Cáº\xadp": "Cập",
    "vÃ\xad": "ví",
    "chÃ\xadnh": "chính",
    "thÃ\xa0nh": "thành"
}

count = 0
for path in java_files:
    with open(path, 'r', encoding='utf-8') as f:
        content = f.read()
    new_content = content
    for b, g in reps.items():
        new_content = new_content.replace(b, g)
    if new_content != content:
        with open(path, 'w', encoding='utf-8') as f:
            f.write(new_content)
        count += 1
print(f'Fixed {count} files.')
