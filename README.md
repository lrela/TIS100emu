# TIS100emu
My TIS-100 emulator. Original motivation was to probably do some experiments with metaheuristic code generation methods like GP https://en.wikipedia.org/wiki/Genetic_programming

Nodes and ports and handled in sequence one by one but it looks like that's not how "real" TIS-100 works. At the moment I believe that each execution step result must be set into another system state instance and replace old state instance at the end of each step. Maybe.

There is also fully working TIS-100 compiler and decompiler (rela.tis100emu.TestCompilers)

## What is TIS-100?

* http://www.zachtronics.com/tis-100/
* https://store.steampowered.com/app/370360/TIS100/
