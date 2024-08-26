@echo off
echo.
echo ***
echo *** createProject v1.0 (2024-08-24)
echo ***
echo *** This script will help to setup an ATCS project
echo *** and connect it to a repo on Github
echo ***
echo.
rem  Save this script anywhere on your PC  (e.g. C:\AT\createProject.bat)
rem  Then open a command shell:  CMD
rem  and call the script in the shell.

if "%1"=="" goto :noParamError

echo You have entered:
echo.
echo ATCS-project: %1  
echo in Directory: %2
echo Github user:  %3
echo.
pause
if "%3"=="" goto :help
if not "%4"=="" goto :help

if not exist %2 md %2

rem --- Create ATCS project (if not already done)
:atcs
if exist %2\%1\created\drawable\char_hero.png goto :git
echo.
echo Do the following steps:
echo.
echo 1.  Start ATCS
echo 2.  Create your ATCS workspace in  %2
echo 3.  Add the new project  %1
echo 4.  Exit ATCS
echo.
pause
goto :atcs

rem --- Create git repository
rem     This is the really important part :)
:git
if exist %2\%1\.gitignore goto :ok
cd %2\%1
git init -b main

echo # %1 > readme.md
git add readme.md
rem git add LICENSE
echo .workspace > .gitignore
echo .project >> .gitignore
echo altered/drawable >> .gitignore
echo altered/drawable/* >> .gitignore
echo created/drawable >> .gitignore
echo created/drawable/* >> .gitignore
echo tmp/res/values/loadresources.xml >> .gitignore
git add .gitignore
git commit -m "Init project"
git remote add origin https://github.com/%3/%1.git

:ok
echo.
echo Now create your repo %1 on https://github.com/%3  (if not already done)
echo.
pause
git push -u origin main

echo.
echo At last you have to find the repo in your git client:
echo - In Smartgit: Menu option: Repository / Search for repository
echo.
goto :ende

:noParamError
echo.
echo You have given no parameter. 
echo Maybe you have just double clicked it? That won't work.
echo I have opened a CMD shell for you. Enter the command in that shell.
start cmd

:help
echo.
echo Please switch to the CMD shell 
echo and enter the command with 3 parameters:
echo %0  project  path  git-user
echo.
echo Example:  %0  feygard_1  c:\AT\ATCS\feygard_1  NutAndor
echo.

:ende
echo.
echo *** End ***
pause
