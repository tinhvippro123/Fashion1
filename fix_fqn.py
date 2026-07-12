import os
import re

src_dir = r'd:\Project\fashion_shop_project\Fashion1\src\main\java'

fqn_patterns = [
    re.compile(r'\b(com\.fashionshop\.[a-z0-9_]+\.[A-Z][a-zA-Z0-9_]*)\b'),
    re.compile(r'\b(org\.springframework\.data\.domain\.[A-Z][a-zA-Z0-9_]*)\b'),
    re.compile(r'\b(org\.springframework\.web\.servlet\.mvc\.support\.[A-Z][a-zA-Z0-9_]*)\b')
]

for root, dirs, files in os.walk(src_dir):
    for file in files:
        if file.endswith('.java'):
            filepath = os.path.join(root, file)
            with open(filepath, 'r', encoding='utf-8') as f:
                content = f.read()

            new_imports = set()
            modified = False
            
            # Find all potential FQNs
            lines = content.split('\n')
            new_lines = []
            
            for line in lines:
                if line.startswith('package ') or line.startswith('import '):
                    new_lines.append(line)
                    continue
                
                original_line = line
                for pattern in fqn_patterns:
                    for match in pattern.finditer(original_line):
                        fqn = match.group(1)
                        class_name = fqn.split('.')[-1]
                        
                        # Add to imports
                        new_imports.add(fqn)
                        
                        # Replace in line
                        # Be careful not to replace things inside strings if possible, but java code usually doesn't have FQN in strings unless reflection
                        line = line.replace(fqn, class_name)
                        modified = True
                        
                new_lines.append(line)
                
            if modified:
                final_content = '\n'.join(new_lines)
                
                # Insert imports right after package
                if new_imports:
                    package_match = re.search(r'^package [^;]+;', final_content, re.MULTILINE)
                    if package_match:
                        insert_pos = package_match.end()
                        imports_str = '\n' + '\n'.join([f'import {imp};' for imp in new_imports])
                        final_content = final_content[:insert_pos] + imports_str + final_content[insert_pos:]
                        
                with open(filepath, 'w', encoding='utf-8') as f:
                    f.write(final_content)
                print(f"Fixed {file}")

print("Done")
