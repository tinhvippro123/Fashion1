import os, json

with open('mojibake_dict.json', 'r', encoding='utf-8') as f:
    replacements = json.load(f)

# Sort replacements by length descending so longer strings get replaced first
sorted_reps = sorted(replacements.items(), key=lambda x: len(x[0]), reverse=True)

# Also create a word-level dictionary from the extracted replacements for safety nets
# No wait, string-literal and comment level replacement should be extremely accurate 
# because it matches exactly what was extracted.

java_files = []
for root, _, files in os.walk('src/main/java'):
    for file in files:
        if file.endswith('.java'):
            java_files.append(os.path.join(root, file))

changed_count = 0
for path in java_files:
    with open(path, 'r', encoding='utf-8') as file:
        content = file.read()
        
    new_content = content
    for bad, good in sorted_reps:
        if bad in new_content:
            new_content = new_content.replace(bad, good)
            
    if new_content != content:
        with open(path, 'w', encoding='utf-8') as file:
            file.write(new_content)
        changed_count += 1

print(f'Fixed {changed_count} files.')
