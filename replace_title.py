import os

directory = r'd:\Project\fashion_shop_project\Fashion1\src\main\resources\templates'

def replace_in_file(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    new_content = content
    
    # Replace titles separator
    new_content = new_content.replace(' - Luxe Fashion', ' | Luxe Fashion')
    new_content = new_content.replace(' - LUXE Fashion', ' | LUXE Fashion')
    new_content = new_content.replace('- Luxe Fashion', '| Luxe Fashion')
    new_content = new_content.replace('- LUXE Fashion', '| LUXE Fashion')

    if content != new_content:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(new_content)
        return True
    return False

count = 0
for root, dirs, files in os.walk(directory):
    for file in files:
        if file.endswith('.html'):
            filepath = os.path.join(root, file)
            if replace_in_file(filepath):
                count += 1
                
print(f'Modified {count} files.')
