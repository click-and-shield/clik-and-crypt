# Usage:
#
#    powershell.exe -NoExit -ExecutionPolicy Bypass -File .\start.ps1 [-env (env1|env2)]
#
# Examples:
#
#    powershell.exe -NoExit -ExecutionPolicy Bypass -File .\start.ps1 -env env1
#    powershell.exe -NoExit -ExecutionPolicy Bypass -File .\start.ps1 -env env2

param([String]$env="env1")

Write-Host ([string]::Format("env = {0}", $env))

if ($env -eq "env1") {
	$env:JAVE_HOME = "C:\Users\denis\Documents\java\jdk-23"
	$env:Path += ";C:\Users\denis\Documents\java\jdk-23\bin"
	$env:Path += ";C:\Users\denis\Documents\java\apache-maven-3.9.9\bin"
	$env:Path += ";C:\Users\denis\Documents\java\wix314-binaries"
	$env:ROOT_DIR=$PSScriptRoot
	# JavaFX
	$env:PATH_TO_FX="C:\Users\denis\Documents\java\javafx-sdk-23\lib"
	$env:PATH_TO_FX_JMODS="C:\Users\denis\Documents\java\javafx-jmods-23.0.1"
} elseif ($env -eq "env2") {
	# Java
	$env:JAVE_HOME = "C:\Users\denis.beurive\Documents\java\jdk-23"
	$env:Path += ";C:\Users\denis.beurive\Documents\java\jdk-23\bin"
	$env:Path += ";C:\Users\denis.beurive\Documents\java\apache-maven-3.9.9\bin"
	$env:Path += ";C:\Users\denis.beurive\Documents\java\wix314-binaries"
	$env:ROOT_DIR=$PSScriptRoot
	# JavaFX
	$env:PATH_TO_FX="C:\Users\denis.beurisve\Documents\java\javafx-sdk-23\lib"
	$env:PATH_TO_FX_JMODS="C:\Users\denis.beurive\Documents\java\javafx-jmods-23.0.1"
} else {
	Write-Host "==========================================================="
	Write-Host ([string]::Format("Invalid environment ID `"{0}`"!", $env))
	Write-Host "The environment is not set properly !!!"
	Write-Host "==========================================================="
	Exit
}
