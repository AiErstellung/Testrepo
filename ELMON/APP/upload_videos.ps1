<#
.SYNOPSIS
Uploads all files from a folder to an Ionos S3 bucket via the AWS CLI and keeps pending/videos.json in sync.

.DESCRIPTION
  The script reads credentials and defaults from local.properties, copies every supported video
  (mp4/mov/avi/mkv/webm) into the pending prefix, and automatically merges the new entries into
  the bucket's pending/videos.json. The console stays open so you can review logs in the spawned PowerShell session.
#>

param(
    [string]$Folder = "E:\Videoupload",
    [string]$Bucket,
    [string]$Prefix = "pending",
    [string]$Profile = "",
    [string]$Endpoint,
    [string]$Region,
    [string]$LocalProperties = "$PSScriptRoot\local.properties"
)

function Read-LocalProperties {
    param([string]$Path)

    $result = [ordered]@{}
    if (-not (Test-Path -LiteralPath $Path)) {
        return $result
    }

    Get-Content -LiteralPath $Path | ForEach-Object {
        $line = $_.Trim()
        if (-not $line -or $line.StartsWith("#")) {
            return
        }

        $separatorIndex = $line.IndexOf("=")
        if ($separatorIndex -lt 0) {
            return
        }

        $key = $line.Substring(0, $separatorIndex).Trim()
        $value = $line.Substring($separatorIndex + 1).Trim()
        $result[$key] = $value
    }

    return $result
}

function Ensure-AwsCli {
    if (-not (Get-Command aws -ErrorAction SilentlyContinue)) {
        throw "Cannot find the AWS CLI in PATH. Install it (https://aws.amazon.com/cli/) and make sure `aws` is reachable."
    }
}

function Invoke-AwsCommand {
    param(
        [string[]]$Arguments,
        [string]$Context
    )

    Write-Host "aws $($Arguments -join ' ')" -ForegroundColor Cyan
    $output = & aws @Arguments 2>&1
    $exit = $LASTEXITCODE
    if ($exit -ne 0) {
        throw "AWS CLI $Context failed (exit $exit):`n$output"
    }

    return $output
}

$localProps = Read-LocalProperties -Path $LocalProperties

$accessKey = $localProps["ELMON_STORAGE_ACCESS_KEY"]
$secretKey = $localProps["ELMON_STORAGE_SECRET_KEY"]
$sessionToken = $localProps["ELMON_STORAGE_SESSION_TOKEN"]

if (-not $accessKey -or -not $secretKey) {
    throw "Access key/secret key missing from $LocalProperties."
}

$env:AWS_ACCESS_KEY_ID = $accessKey
$env:AWS_SECRET_ACCESS_KEY = $secretKey
if ($sessionToken) {
    $env:AWS_SESSION_TOKEN = $sessionToken
}

if (-not $Region) {
    $Region = $localProps["ELMON_STORAGE_REGION"]
}

if (-not $Bucket) {
    $Bucket = $localProps["ELMON_STORAGE_BUCKET"]
}

if (-not $Endpoint) {
    $Endpoint = $localProps["ELMON_STORAGE_ENDPOINT"]
}

if (-not $Bucket) {
    throw "The target bucket is not set. Provide -Bucket or add ELMON_STORAGE_BUCKET to $LocalProperties."
}

if (-not $Endpoint) {
    throw "The endpoint is not set. Provide -Endpoint or add ELMON_STORAGE_ENDPOINT to $LocalProperties."
}

Ensure-AwsCli

$folderPath = Resolve-Path -LiteralPath $Folder -ErrorAction Stop
Write-Host "Uploading from $folderPath to s3://$Bucket/$Prefix"

$allowedExtensions = '.mp4', '.mov', '.avi', '.mkv', '.webm'
$videos = Get-ChildItem -LiteralPath $folderPath -File | Where-Object {
    $allowedExtensions -contains $_.Extension.ToLowerInvariant()
} | Sort-Object Name

if (-not $videos) {
    Write-Host "No supported videos found in $folderPath."
    return
}

$endpointArgs = @()
if ($Endpoint) {
    $endpointArgs += "--endpoint-url"
    $endpointArgs += $Endpoint
}

$regionArgs = @()
if ($Region) {
    $regionArgs += "--region"
    $regionArgs += $Region
}

$tempJson = New-TemporaryFile
try {
    $existingEntries = @()
$profileArgs = @()
if ($Profile) {
    $profileArgs = @("--profile", $Profile)
}
$downloadArgs = @("s3", "cp", "s3://$Bucket/$Prefix/videos.json", $tempJson.FullName, "--no-progress") + $profileArgs + $endpointArgs + $regionArgs
    $downloadOutput = & aws @downloadArgs 2>&1
    if ($LASTEXITCODE -ne 0) {
        if ($downloadOutput -match "NoSuchKey") {
            Write-Host "pending/videos.json does not exist yet; starting with an empty list."
        } else {
            throw "Downloading pending/videos.json failed (exit $LASTEXITCODE):`n$downloadOutput"
        }
    } else {
        $raw = Get-Content -LiteralPath $tempJson.FullName -Raw
        if ($raw.Trim()) {
            $existingEntries = $raw | ConvertFrom-Json
        }
    }

    if (-not $existingEntries) {
        $existingEntries = @()
    }

    $existingEntries = @($existingEntries)
    $newEntries = @()
    $endpointBase = $Endpoint.TrimEnd("/")

    foreach ($video in $videos) {
        $objectKey = "$Prefix/$($video.Name)"
        $destination = "s3://$Bucket/$objectKey"
        $uploadArgs = @("s3", "cp", $video.FullName, $destination, "--acl", "bucket-owner-full-control", "--no-progress") + $profileArgs + $endpointArgs + $regionArgs

        Invoke-AwsCommand -Arguments $uploadArgs -Context "uploading $($video.Name)"
        $url = "$endpointBase/$Bucket/$objectKey"
        $newEntries += [pscustomobject]@{
            id    = $video.BaseName
            title = $video.BaseName
            url   = $url
            s3Key = $objectKey
        }
        Write-Host "Uploaded $($video.Name) -> $url"
    }

    $mergedEntries = @()
    if ($existingEntries) {
        $mergedEntries += $existingEntries
    }
    $mergedEntries += $newEntries

    $payload = ($mergedEntries | ConvertTo-Json -Depth 5).TrimStart([char]0xFEFF)
    $utf8Encoder = New-Object System.Text.UTF8Encoding($false)
    [System.IO.File]::WriteAllText($tempJson.FullName, $payload, $utf8Encoder)

    $putArgs = @("s3", "cp", $tempJson.FullName, "s3://$Bucket/$Prefix/videos.json", "--content-type", "application/json", "--acl", "bucket-owner-full-control", "--no-progress") + $profileArgs + $endpointArgs + $regionArgs
    Invoke-AwsCommand -Arguments $putArgs -Context "uploading pending/videos.json"
    Write-Host "pending/videos.json updated with $($newEntries.Count) new entries."
}
finally {
    Remove-Item -LiteralPath $tempJson.FullName -ErrorAction SilentlyContinue
}
