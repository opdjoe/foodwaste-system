@echo off
echo ================================================
echo  FoodTrace Frontend Server
echo  Opens at: http://localhost:5500
echo  Make sure Spring Boot is running on port 8080
echo ================================================
echo.

REM Try Python first (most likely installed)
python --version >nul 2>&1
if %errorlevel% == 0 (
    echo Starting with Python on port 5500...
    echo Open your browser at: http://localhost:5500
    echo Press Ctrl+C to stop.
    python -m http.server 5500
    goto end
)

REM Try Python3 explicitly
python3 --version >nul 2>&1
if %errorlevel% == 0 (
    echo Starting with Python3 on port 5500...
    echo Open your browser at: http://localhost:5500
    echo Press Ctrl+C to stop.
    python3 -m http.server 5500
    goto end
)

REM Try Node.js npx serve
npx --version >nul 2>&1
if %errorlevel% == 0 (
    echo Starting with npx serve on port 5500...
    echo Open your browser at: http://localhost:5500
    echo Press Ctrl+C to stop.
    npx serve -l 5500 .
    goto end
)

echo ERROR: Neither Python nor Node.js found.
echo.
echo Please install one of:
echo   Python: https://www.python.org/downloads/
echo   Node.js: https://nodejs.org/
echo.
echo OR use VS Code with the "Live Server" extension,
echo then right-click index.html and select "Open with Live Server".
echo.
pause

:end
