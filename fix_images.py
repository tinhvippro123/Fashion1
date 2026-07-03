import os
import re

directory = r'd:\Project\fashion_shop_project\Fashion1\src\main\resources\templates'

pattern = re.compile(r"@\{'' \+ \$\{(.+?)\}\}")

for root, _, files in os.walk(directory):
    for file in files:
        if file.endswith('.html'):
            filepath = os.path.join(root, file)
            with open(filepath, 'r', encoding='utf-8') as f:
                content = f.read()
            
            if "@{'' +" in content:
                # use a function for replacement to avoid escaping issues
                def repl(m):
                    var = m.group(1)
                    return f"${{{var} != null and #strings.startsWith({var}, 'http') ? {var} : '/uploads/' + {var}}}"
                
                new_content = pattern.sub(repl, content)
                with open(filepath, 'w', encoding='utf-8') as f:
                    f.write(new_content)
                print("Updated " + filepath)
