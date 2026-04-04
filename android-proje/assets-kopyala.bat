@echo off
echo Dosya kopyalaniyor...
copy /Y "..\before-after.html" "app\src\main\assets\before-after.html"
if %errorlevel% == 0 (
    echo [OK] before-after.html assets klasorune kopyalandi.
) else (
    echo [HATA] Kopyalama basarisiz.
)
pause
