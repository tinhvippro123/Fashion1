import os

directory = r'd:\Project\fashion_shop_project\Fashion1\src\main\resources'

def replace_in_file(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    new_content = content
    
    # Specific phrases first
    new_content = new_content.replace('IVY moda', 'LUXE Fashion')
    new_content = new_content.replace('IVY Moda', 'LUXE Fashion')
    new_content = new_content.replace('ivy moda', 'luxe fashion')
    new_content = new_content.replace('Ivy moda', 'Luxe fashion')
    
    new_content = new_content.replace('ivymoda', 'luxe fashion')
    new_content = new_content.replace('IVYMODA', 'LUXE FASHION')
    
    # Generic replacements
    new_content = new_content.replace('IVY', 'LUXE')
    new_content = new_content.replace('ivy', 'luxe')
    new_content = new_content.replace('Ivy', 'Luxe')

    if content != new_content:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(new_content)
        return True
    return False

count = 0
for root, dirs, files in os.walk(directory):
    for file in files:
        if file.endswith(('.html', '.css', '.js', '.properties', '.yml', '.yaml')):
            filepath = os.path.join(root, file)
            if replace_in_file(filepath):
                count += 1
                
print(f'Modified {count} files.')
