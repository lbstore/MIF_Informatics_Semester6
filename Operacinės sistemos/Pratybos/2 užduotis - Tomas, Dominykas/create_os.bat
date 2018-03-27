@ECHO OFF 
SET compiler_cmd=g++
SET source_file=operating_system.c
SET output_file=os.exe

ECHO Kompiliuojame "%source_file%"...
%compiler_cmd% %source_file% -o %output_file%
ECHO.
goto code%ERRORLEVEL%

:code0
ECHO Kompiliavimas baigtas! Sukurtas failas: %output_file%!
goto pause

:code1
ECHO Ivyko kompiliavimo klaida. %output_file% neatnaujintas!
goto pause

:pause
ECHO.
ECHO.
PAUSE