@echo off
echo Dosya kopyalaniyor...
copy /Y "..\before-after.html" "OnceSonra\before-after.html"
if %errorlevel% == 0 (
    echo [OK] before-after.html ios-proje\OnceSonra klasorune kopyalandi.
) else (
    echo [HATA] Kopyalama basarisiz oldu.
)
pause
