$ErrorActionPreference = 'Stop'
$pack = Join-Path $PSScriptRoot '..\resourcepack'
$zip = Join-Path $PSScriptRoot '..\NomeBoxes_ResourcePack.zip'
if (Test-Path $zip) {
    Remove-Item $zip -Force
}
Compress-Archive -Path (Join-Path $pack '*') -DestinationPath $zip
Write-Host "Created $zip"
