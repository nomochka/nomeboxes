$ErrorActionPreference = 'Stop'
Add-Type -AssemblyName System.Drawing

$dir = Join-Path $PSScriptRoot '..\resourcepack\assets\nomeboxes\textures\item'
New-Item -ItemType Directory -Force -Path $dir | Out-Null

function New-Texture {
    param([string]$File, [string]$Base, [string]$Dark, [string]$Flap)
    $bmp = New-Object System.Drawing.Bitmap 16, 16
    $g = [System.Drawing.Graphics]::FromImage($bmp)

    $baseColor = [System.Drawing.ColorTranslator]::FromHtml($Base)
    $darkColor = [System.Drawing.ColorTranslator]::FromHtml($Dark)

    $g.Clear($baseColor)

    $borderPen = [System.Drawing.Pen]::new($darkColor)
    $g.DrawRectangle($borderPen, 0, 0, 15, 15)
    $g.DrawRectangle($borderPen, 1, 1, 13, 13)

    $flapBrush = [System.Drawing.SolidBrush]::new([System.Drawing.ColorTranslator]::FromHtml($Flap))
    $g.FillRectangle($flapBrush, 3, 2, 10, 6)

    $buttonBrush = [System.Drawing.SolidBrush]::new([System.Drawing.ColorTranslator]::FromHtml('#3B3B3B'))
    $g.FillRectangle($buttonBrush, 6, 6, 4, 4)

    $g.Dispose()
    $bmp.Save((Join-Path $dir $File), [System.Drawing.Imaging.ImageFormat]::Png)
    $bmp.Dispose()
    Write-Host "Created $($File)"
}

New-Texture 'backpack_small.png'  '#B07A45' '#6E4A28' '#D9A066'
New-Texture 'backpack_medium.png' '#96613A' '#5C3A20' '#C08A52'
New-Texture 'backpack_large.png'  '#7A4E2E' '#4A2E19' '#A67447'
New-Texture 'backpack_super.png'  '#5C4033' '#33221B' '#8A6547'

Write-Host 'Placeholder textures generated.'
