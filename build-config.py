# This script generates the JSON configuration file required by the Java launcher,
# based on the output of the following Maven command:
#
#       mvn -X clean javafx:run -Dargs="encrypt /path/to/file"
#
# Procedure:
#
#       1. Execute the Maven command provided above.
#       2. Locate and copy the line starting with "[DEBUG] Executing command line: " from the command output.
#       3. Assign this line as the value of the `MVN_SPEC` variable.
#       4. Run this script.

import re
import json
from typing import List, Final, Pattern, Match, Optional, Dict, Union

MVN_SPEC: Final[str] = '''
[DEBUG] Executing command line: [C:\\Users\\denis\\Documents\\java\\jdk-23\\bin\\java.exe, --module-path, C:\\Users\\denis\\.m2\\repository\\org\\openjfx\\javafx-base\\23\\javafx-base-23-win.jar;C:\\Users\\denis\\.m2\\repository\\org\\openjfx\\javafx-
base\\23\\javafx-base-23.jar;C:\\Users\\denis\\.m2\\repository\\org\\openjfx\\javafx-controls\\23\\javafx-controls-23-win.jar;C:\\Users\\denis\\.m2\\repository\\org\\openjfx\\javafx-controls\\23\\javafx-controls-23.jar;C:\\Users\\denis\\.m2\\repository
\\org\\openjfx\\javafx-graphics\\23\\javafx-graphics-23-win.jar;C:\\Users\\denis\\.m2\\repository\\org\\openjfx\\javafx-graphics\\23\\javafx-graphics-23.jar, --add-modules, javafx.base,javafx.controls,javafx.graphics, -classpath, C:\\Users\\den
is\\Documents\\github\\click-and-crypt\\target\\classes;C:\\Users\\denis\\.m2\\repository\\commons-cli\\commons-cli\\1.9.0\\commons-cli-1.9.0.jar;C:\\Users\\denis\\.m2\\repository\\org\\jetbrains\\annotations\\26.0.2\\annotations-26.0.2.jar, org.shadow.click_and_crypt.Main, encrypt, C:\\Users\\denis\\Documents\\github\\click-and-crypt\\test-data\\input.txt]
'''

def get_classpath(text: str) -> List[str]:
    paths: List[str] = text.split(';')
    return paths

def get_module_path(text: str) -> List[str]:
    paths: List[str] = text.split(';')
    return paths

def get_modules(text: str) -> List[str]:
    modules: List[str] = text.split(',')
    return modules

# Extract the interesting part
text: str = MVN_SPEC.replace("\n", '')
regex: Pattern = re.compile(r"^\[DEBUG] Executing command line: \[(.+)]$")
m: Optional[Match] = regex.match(text)
if m is None:
    print("Invalid specification!")
    exit(1)
text = m.group(0)

regex_main_class: Pattern = re.compile(r"^(([a-z0-9_]+\.)*[a-z0-9_]+)$", re.IGNORECASE)
data: Dict[str, List[str]] = {}
parts: List[str] = text.split(', ')
found_class: bool = False

for i in range(1, len(parts)):
    part = parts[i].strip()
    if '-classpath' == part:
        data['classpath'] = get_classpath(parts[i+1])
        i += 1
        continue
    if '--module-path' == part:
        data['module_path'] = get_module_path(parts[i+1])
        i += 1
        continue
    if '--add-modules' == part:
        data['modules'] = get_modules(parts[i+1])
        i += 1
        continue

    if not found_class:
        m: Optional[Match] = regex_main_class.match(parts[i])
        if m is not None:
            data['class'] = m.group(0)
            found_class = True
    else:
        data['arguments'] = parts[i:]
        break


config: Dict[str, Union[List[str], str, None]] = {'ModulesPaths': data['module_path'],
                                            'Modules': data['modules'],
                                            'ClassPath': data['classpath'],
                                            'MainClass': data['class'],
                                            'JavaHomePath': None}

print(json.dumps(config, indent=4, sort_keys=True, ensure_ascii=False))






