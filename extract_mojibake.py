import os, subprocess, difflib, re, json

result = subprocess.run(['git', 'ls-tree', '-r', 'HEAD', '--name-only'], capture_output=True, text=True)
files = [f for f in result.stdout.split('\n') if f.endswith('.java')]

replacements = {}

def extract_strings(text):
    return re.findall(r'"([^"]*?[\xc0-\xff\u0100-\uffff][^"]*?)"', text)

for f in files:
    old_content_res = subprocess.run(['git', 'show', f'f692ab6:{f}'], capture_output=True, text=True, encoding='utf-8')
    if old_content_res.returncode != 0: continue
    old_lines = old_content_res.stdout.splitlines()
    with open(f, 'r', encoding='utf-8') as file: new_lines = file.read().splitlines()
    
    old_strings = extract_strings('\n'.join(old_lines))
    new_strings = extract_strings('\n'.join(new_lines))
    
    matcher = difflib.SequenceMatcher(None, old_strings, new_strings)
    for tag, i1, i2, j1, j2 in matcher.get_opcodes():
        if tag == 'replace' and (i2-i1) == (j2-j1):
            for o, n in zip(old_strings[i1:i2], new_strings[j1:j2]):
                if n != o and len(n) > 2:
                    replacements[n] = o

def extract_comments(lines):
    comments = []
    for l in lines:
        if '//' in l:
            c = l[l.index('//')+2:].strip()
            if any(ord(char) > 127 for char in c):
                comments.append(c)
    return comments

for f in files:
    old_content_res = subprocess.run(['git', 'show', f'f692ab6:{f}'], capture_output=True, text=True, encoding='utf-8')
    if old_content_res.returncode != 0: continue
    old_lines = old_content_res.stdout.splitlines()
    with open(f, 'r', encoding='utf-8') as file: new_lines = file.read().splitlines()
    
    old_c = extract_comments(old_lines)
    new_c = extract_comments(new_lines)
    matcher = difflib.SequenceMatcher(None, old_c, new_c)
    for tag, i1, i2, j1, j2 in matcher.get_opcodes():
        if tag == 'replace' and (i2-i1) == (j2-j1):
            for o, n in zip(old_c[i1:i2], new_c[j1:j2]):
                if n != o and len(n) > 2:
                    replacements[n] = o

print(f'Found {len(replacements)} replacements.')
with open('mojibake_dict.json', 'w', encoding='utf-8') as f:
    json.dump(replacements, f, ensure_ascii=False, indent=2)
